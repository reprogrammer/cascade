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
import checker.framework.change.propagator.ShadowProject;
import checker.framework.errorcentric.propagator.commands.InferCommandHandler;
import checker.framework.quickfixes.descriptors.FixerDescriptor;

import com.google.common.base.Predicate;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

import static com.google.common.collect.Sets.difference;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.union;

public class ChangeComputationJob extends Job {

    MarkerResolutionTreeNode markerResolutionTreeNode;

    public ChangeComputationJob(String name,
            MarkerResolutionTreeNode markerResolutionTreeNode) {
        super(name);
        this.markerResolutionTreeNode = markerResolutionTreeNode;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask(getName(), 25);
        final List<FixerDescriptor> parentFixerDescriptors = markerResolutionTreeNode
                .getParentFixerDescriptors();
        ActionableMarkerResolution resolution = markerResolutionTreeNode
                .getResolution();
        resolution.getShadowProject().updateToPrimaryProjectWithChanges(
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
                newResolutions, new Predicate<ActionableMarkerResolution>() {
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
        markerResolutionTreeNode
                .addChildren(union(errorTreeNodesWithResolutions,
                        errorTreeNodesWithoutResolutions));
        monitor.worked(1);
        monitor.done();
        JobManager.done(markerResolutionTreeNode);
        int errorsFixed = resolution.getMarkersToBeResolvedByFixer().size();
        markerResolutionTreeNode.setErrorsFixed(errorsFixed);
        markerResolutionTreeNode.setName(markerResolutionTreeNode.getName()
                + " (" + errorsFixed + ")");
        markerResolutionTreeNode.getTreeUpdater().update(
                markerResolutionTreeNode);
        return Status.OK_STATUS;

    }

}
