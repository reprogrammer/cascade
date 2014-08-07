package checker.framework.errorcentric.view.views;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import checker.framework.change.propagator.ActionableMarkerResolution;
import checker.framework.change.propagator.ComparableMarker;
import checker.framework.change.propagator.ShadowOfShadowProject;
import checker.framework.errorcentric.propagator.commands.InferCommandHandler;
import checker.framework.quickfixes.descriptors.FixerDescriptor;

import com.google.common.base.Predicate;

import static com.google.common.collect.Sets.difference;
import static com.google.common.collect.Sets.filter;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.union;

import static com.google.common.collect.Iterables.transform;

public class ChangeComputationJob extends Job {

    private MarkerResolutionTreeNode markerResolutionTreeNode;

    // We are using our own synchronization here. See MarkerResolutionTreeNode
    // for more detaila.
    private static Object lock = new Object();

    public ChangeComputationJob(String name,
            MarkerResolutionTreeNode markerResolutionTreeNode) {
        super(name);
        this.markerResolutionTreeNode = markerResolutionTreeNode;
    }

    // TODO(reprogrammer): I suggest that we break this method into several
    // smaller ones.
    @Override
    protected IStatus run(IProgressMonitor monitor) {
        synchronized (lock) {
            monitor.beginTask(getName(), 23);
            monitor.subTask("Updating shadow project: " + getThread());
            final List<FixerDescriptor> parentFixerDescriptors = markerResolutionTreeNode
                    .getParentFixerDescriptors();
            ActionableMarkerResolution resolution = markerResolutionTreeNode
                    .getResolution();
            resolution.getShadowProject().updateToPrimaryProjectWithChanges(
                    parentFixerDescriptors);
            monitor.worked(3);
            monitor.subTask("Applying resolution: " + getThread());
            resolution.apply();
            monitor.worked(3);
            // WorkspaceUtils.saveAllEditors();
            // monitor.worked(3);
            monitor.subTask("Running checker: " + getThread());
            ShadowOfShadowProject shadowProject = resolution.getShadowProject();
            shadowProject.runChecker(InferCommandHandler.checkerID);
            monitor.worked(10);
            monitor.subTask("Getting new markers: " + getThread());
            Set<ComparableMarker> allMarkersAfterResolution = shadowProject
                    .getMarkers();
            Set<ComparableMarker> addedMarkers = difference(
                    allMarkersAfterResolution,
                    resolution.getAllMarkersBeforeResolution());
            monitor.worked(1);
            monitor.subTask("Getting new resolutions: " + getThread());
            Set<ActionableMarkerResolution> newResolutions = shadowProject
                    .getResolutions(allMarkersAfterResolution, addedMarkers);
            yieldRule(monitor);
            monitor.worked(3);
            monitor.subTask("Filtering resolutions: " + getThread());
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
            monitor.subTask("Adding children to tree: " + getThread());
            Set<ComparableMarker> fixedMarkers = newHashSet();
            for (ActionableMarkerResolution historicallyNewResolution : historicallyNewResolutions) {
                fixedMarkers.addAll(historicallyNewResolution
                        .getMarkersToBeResolvedByFixer());
            }
            Set<ComparableMarker> unresolvedMarkers = difference(addedMarkers,
                    fixedMarkers);
            Set<ErrorTreeNode> errorTreeNodesWithoutResolutions = newHashSet(transform(
                    unresolvedMarkers, marker -> new AddedErrorTreeNode(marker)));
            HashSet<ErrorTreeNode> errorTreeNodesWithResolutions = newHashSet(AddedErrorTreeNode
                    .createTreeNodesFrom(historicallyNewResolutions,
                            markerResolutionTreeNode.getTreeUpdater()));
            markerResolutionTreeNode.addChildren(union(
                    errorTreeNodesWithResolutions,
                    errorTreeNodesWithoutResolutions));
            int errorsFixed = resolution.getMarkersToBeResolvedByFixer().size();
            markerResolutionTreeNode.setFixedErrorsCount(errorsFixed);
            // TODO(reprogrammer): I suggest to redesign the classes such that
            // the following statement doesn't duplicate the reference to
            // markerResolutionTreeNode.
            markerResolutionTreeNode.setName(markerResolutionTreeNode.getName()
                    + " (" + errorsFixed + ")");
            monitor.worked(1);
            monitor.subTask("Updating tree: " + getThread());
            markerResolutionTreeNode.getTreeUpdater().update(
                    markerResolutionTreeNode);
            monitor.worked(1);
            monitor.done();
            return Status.OK_STATUS;
        }
    }

}
