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
import static com.google.common.collect.Iterables.transform;

import static com.google.common.collect.Sets.difference;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.union;

public class MarkerResolutionTreeNode extends TreeObject {

    private ActionableMarkerResolution resolution;

    private Job job;
    private volatile int errorsFixed;
    private Set<ComparableMarker> unresolvableMarkers;

    public MarkerResolutionTreeNode(ActionableMarkerResolution resolution) {
        super(resolution.getLabel());
        this.resolution = resolution;
        this.unresolvableMarkers = new HashSet<>();
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

    // TODO(amarin): Remove Async from the name of the method.
    public void computeChangeEffectAsync() {
        final MarkerResolutionTreeNode thisNode = this;
        String progressBarLabel = String.format("Computing the effect of: %s",
                resolution.getLabel());
        // TODO(amarin): Convert this anon. class into a top-level class.
        job = new Job(progressBarLabel) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {

                monitor.beginTask(progressBarLabel, 25);
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

                Set<ComparableMarker> fixedMarkers = newHashSet();
                for (ActionableMarkerResolution resolution : historicallyNewResolutions) {
                    fixedMarkers.addAll(resolution
                            .getMarkersToBeResolvedByFixer());
                }
                Set<ComparableMarker> unresolvedMarkers = difference(
                        addedMarkers, fixedMarkers);
                Set<ErrorTreeNode> errorTreeNodesWithoutResolutions = newHashSet(transform(
                        unresolvedMarkers, marker -> new AddedErrorTreeNode(
                                marker)));
                HashSet<ErrorTreeNode> errorTreeNodesWithResolutions = newHashSet(AddedErrorTreeNode
                        .createTreeNodesFrom(historicallyNewResolutions,
                                thisNode.getTreeUpdater()));
                addChildren(union(errorTreeNodesWithResolutions,
                        errorTreeNodesWithoutResolutions));
                monitor.worked(1);
                monitor.done();
                JobManager.done(thisNode);
                errorsFixed = resolution.getMarkersToBeResolvedByFixer().size();
                thisNode.setName(thisNode.getName() + " (" + errorsFixed + ")");
                thisNode.getTreeUpdater().update(thisNode);
                return Status.OK_STATUS;
            }
        };
        job.setRule(ResourcesPlugin.getWorkspace().getRoot());
        job.setPriority(Job.LONG);
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

    @Override
    public int getRank() {
        return getErrorsFixed();
    }

    public int getErrorsFixed() {
        return errorsFixed;
    }

    public Set<ComparableMarker> getUnresolvableMarkers() {
        return unresolvableMarkers;
    }
}
