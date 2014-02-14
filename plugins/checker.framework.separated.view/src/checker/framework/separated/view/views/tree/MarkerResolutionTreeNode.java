package checker.framework.separated.view.views.tree;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Sets.difference;
import static com.google.common.collect.Sets.newHashSet;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import checker.framework.change.propagator.ActionableMarkerResolution;
import checker.framework.change.propagator.ComparableMarker;
import checker.framework.change.propagator.ShadowProject;
import checker.framework.separated.propagator.commands.InferCommandHandler;
import checker.framework.quickfixes.WorkspaceUtils;
import checker.framework.quickfixes.descriptors.FixerDescriptor;

import com.google.common.base.Predicate;

public class MarkerResolutionTreeNode extends TreeObject {

    private ActionableMarkerResolution resolution;
    public Set<ComparableMarker> getAddedMarkers() {
        calculateAndCacheMarkers();
        return addedMarkers;
    }

    public Set<ComparableMarker> getRemovedMarkers() {
        calculateAndCacheMarkers();
        return removedMarkers;
    }

    private Set<ComparableMarker> addedMarkers;
    private Set<ComparableMarker> removedMarkers;
    private Set<ComparableMarker> allMarkersAfterResolution;
    private LinkedList<FixerDescriptor> parentFixerDescriptors;

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

    private Set<MarkerResolutionTreeNode> createMarkerTreeNodesFrom(
            Set<ActionableMarkerResolution> resolutions) {
        Set<MarkerResolutionTreeNode> nodes = new HashSet<>();
        for (ActionableMarkerResolution resolution : resolutions) {
            nodes.add(new MarkerResolutionTreeNode(resolution));
        }
        return nodes;
    }

    private List<FixerDescriptor> getParentFixerDescriptors() {
        if (parentFixerDescriptors == null) {
            parentFixerDescriptors = new LinkedList<>();
            TreeObject parent = getParent();
            while (parent != null && parent instanceof MarkerResolutionTreeNode) {
                parentFixerDescriptors
                        .addLast(((MarkerResolutionTreeNode) parent)
                                .getResolution().getFixerDescriptor());
                parent = parent.getParent();
            }
        }
        return parentFixerDescriptors;

    }

    public TreeObject[] getChildren() {
        calculateAndCacheMarkers();
        ShadowProject shadowProject = resolution.getShadowProject();
        Set<ActionableMarkerResolution> newResolutions = shadowProject
                .getResolutions(allMarkersAfterResolution, addedMarkers);
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
        addChildren(createMarkerTreeNodesFrom(historicallyNewResolutions));
        return super.getChildren();
    }

    /**
     * addedMarkers, removedMarkers, allMarkersAfterResolution, and
     * parentFixerDescriptors are initialized and cached here
     */
    private void calculateAndCacheMarkers() {
        if (addedMarkers == null || removedMarkers == null
                || allMarkersAfterResolution == null) {
            resolution.getShadowProject().updateToPrimaryProjectWithChanges(
                    getParentFixerDescriptors());
            resolution.apply();
            WorkspaceUtils.saveAllEditors();
            resolution.getShadowProject().runChecker(
                    InferCommandHandler.checkerID);
            allMarkersAfterResolution = resolution.getShadowProject()
                    .getMarkers();
            removedMarkers = difference(resolution.getAllMarkers(),
                    allMarkersAfterResolution);
            addedMarkers = difference(allMarkersAfterResolution,
                    resolution.getAllMarkers());
        }
    }
}
