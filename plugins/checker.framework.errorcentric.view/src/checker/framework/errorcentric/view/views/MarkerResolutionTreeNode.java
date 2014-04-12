package checker.framework.errorcentric.view.views;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import checker.framework.change.propagator.ActionableMarkerResolution;
import checker.framework.change.propagator.ComparableMarker;
import checker.framework.change.propagator.ShadowProject;
import checker.framework.errorcentric.propagator.commands.InferCommandHandler;
import checker.framework.quickfixes.descriptors.FixerDescriptor;

import com.google.common.base.Predicate;

import static com.google.common.collect.Iterables.filter;

import static com.google.common.collect.Sets.difference;
import static com.google.common.collect.Sets.newHashSet;

public class MarkerResolutionTreeNode extends TreeObject {

    private ActionableMarkerResolution resolution;

    private Job job;

    public MarkerResolutionTreeNode(ActionableMarkerResolution resolution) {
        super(resolution.getLabel());
        this.resolution = resolution;
    }

    public ActionableMarkerResolution getResolution() {
        return resolution;
    }

    public boolean hasChildren() {
        return true;
    }

    private List<FixerDescriptor> getParentFixerDescriptors() {
        LinkedList<FixerDescriptor> fixerDescriptors = new LinkedList<>();
        TreeObject parent = getParent();
        while (parent != null) {
            if (parent instanceof MarkerResolutionTreeNode) {
                fixerDescriptors.addLast(((MarkerResolutionTreeNode) parent)
                        .getResolution().getFixerDescriptor());
            }
            parent = parent.getParent();
        }
        return fixerDescriptors;
    }

    public void computeChangeEffectAsync() {
        final MarkerResolutionTreeNode thisNode = this;
        job = new Job("Computing the effect of the change") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {

                monitor.beginTask("Computing the effect of the change", 25);
                final List<FixerDescriptor> parentFixerDescriptors = getParentFixerDescriptors();
                resolution.getShadowProject()
                        .updateToPrimaryProjectWithChanges(
                                parentFixerDescriptors);
                monitor.worked(3);
                resolution.apply();

                monitor.worked(3);
                // WorkspaceUtils.saveAllEditors();
                monitor.worked(3);
                ShadowProject shadowProject = resolution.getShadowProject();
                shadowProject.runChecker(InferCommandHandler.checkerID);
                monitor.worked(10);

                Set<ComparableMarker> allMarkersAfterResolution = shadowProject
                        .getMarkers();
                Set<ComparableMarker> addedMarkers = difference(
                        allMarkersAfterResolution,
                        resolution.getAllMarkersBeforeResolution());
                monitor.worked(1);
                Set<ActionableMarkerResolution> newResolutions = shadowProject
                        .getResolutions(allMarkersAfterResolution, addedMarkers);
                System.out.println(resolution + "\n" + "fixes "
                        + resolution.getMarkersToBeResolvedByFixer().size()
                        + " errors=n" + "introduces " + addedMarkers.size()
                        + " errors");
                monitor.worked(3);
                HashSet<ActionableMarkerResolution> historicallyNewResolutions = newHashSet(filter(
                        newResolutions,
                        new Predicate<ActionableMarkerResolution>() {
                            @Override
                            public boolean apply(
                                    ActionableMarkerResolution newResolution) {
                                FixerDescriptor fixerDescriptor = newResolution
                                        .getFixerDescriptor();
                                return !resolution.getFixerDescriptor().equals(
                                        fixerDescriptor)
                                        && !parentFixerDescriptors
                                                .contains(fixerDescriptor);
                            }
                        }));
                monitor.worked(1);
                addChildren(AddedErrorTreeNode.createTreeNodesFrom(
                        historicallyNewResolutions, thisNode.getLabelUpdater()));
                monitor.worked(1);
                monitor.done();
                JobManager.done(thisNode);
                thisNode.setName(thisNode.getName() + " ("
                        + resolution.getMarkersToBeResolvedByFixer().size()
                        + ")");
                thisNode.getLabelUpdater().update(thisNode);
                return Status.OK_STATUS;
            }
        };
        job.setRule(ResourcesPlugin.getWorkspace().getRoot());
        job.schedule();
    }

    public TreeObject[] getChildren() {
        try {
            job.join();
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
        return super.getChildren();
    }

}
