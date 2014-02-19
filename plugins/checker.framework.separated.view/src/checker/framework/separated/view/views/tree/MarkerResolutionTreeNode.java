package checker.framework.separated.view.views.tree;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Sets.difference;
import static com.google.common.collect.Sets.intersection;
import static com.google.common.collect.Sets.newHashSet;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import checker.framework.change.propagator.ActionableMarkerResolution;
import checker.framework.change.propagator.ComparableMarker;
import checker.framework.change.propagator.ShadowProject;
import checker.framework.quickfixes.WorkspaceUtils;
import checker.framework.quickfixes.descriptors.FixerDescriptor;
import checker.framework.separated.propagator.commands.InferCommandHandler;

import com.google.common.base.Predicate;

public class MarkerResolutionTreeNode extends TreeObject {

    private ActionableMarkerResolution resolution;

    private Set<ComparableMarker> allMarkersAfterResolution;
    private Set<ComparableMarker> allMarkersOnlyAfterResolution;
    private Set<ComparableMarker> allMarkersOnlyBeforeResolution;
    private Set<ComparableMarker> allMarkersBeforeAndAfterResolution;
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

    public Set<ComparableMarker> getAllMarkersOnlyAfterResolution() {
        calculateAndCacheMarkers();
        return allMarkersOnlyAfterResolution;
    }

    public Set<ComparableMarker> getAllMarkersOnlyBeforeResolution() {
        calculateAndCacheMarkers();
        return allMarkersOnlyBeforeResolution;
    }

    public Set<ComparableMarker> getAllMarkersBeforeAndAfterResolution() {
        calculateAndCacheMarkers();
        return allMarkersBeforeAndAfterResolution;
    }

    public TreeObject[] getChildren() {
        calculateAndCacheMarkers();
        ShadowProject shadowProject = resolution.getShadowProject();
        Set<ActionableMarkerResolution> newResolutions = shadowProject
                .getResolutions(allMarkersAfterResolution,
                        allMarkersOnlyAfterResolution);
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
        if (allMarkersOnlyAfterResolution == null
                || allMarkersOnlyBeforeResolution == null
                || allMarkersBeforeAndAfterResolution == null) {
            resolution.getShadowProject().updateToPrimaryProjectWithChanges(
                    getParentFixerDescriptors());
            resolution.apply();
            WorkspaceUtils.saveAllEditors();
            resolution.getShadowProject().runChecker(
                    InferCommandHandler.checkerID);
            allMarkersAfterResolution = resolution.getShadowProject()
                    .getMarkers();
            Set<ComparableMarker> allMarkersBeforeResolution = resolution
                    .getAllMarkersBeforeResolution();

            allMarkersOnlyBeforeResolution = difference(
                    allMarkersBeforeResolution, allMarkersAfterResolution);

            allMarkersOnlyAfterResolution = difference(
                    allMarkersAfterResolution, allMarkersBeforeResolution);

            allMarkersBeforeAndAfterResolution = intersection(
                    allMarkersAfterResolution, allMarkersBeforeResolution);
        }
    }
}
