package checker.framework.errorcentric.view.views;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import checker.framework.change.propagator.ActionableMarkerResolution;
import checker.framework.change.propagator.ComparableMarker;
import checker.framework.change.propagator.MarkerLocation;

import com.google.common.base.Optional;

public class ErrorTreeNode extends TreeObject {

    protected ComparableMarker marker;

    protected Set<ActionableMarkerResolution> resolutions = new HashSet<>();

    public ErrorTreeNode(ComparableMarker marker) {
        super(marker.getMessage());
        this.marker = marker;
    }

    public void reveal() {
        Optional<MarkerLocation> optionalMarkerLocation = marker
                .createMarkerLocation();
        if (optionalMarkerLocation.isPresent()) {
            MarkerLocation markerLocation = optionalMarkerLocation.get();
            new CodeSnippetRevealer().reveal(
                    markerLocation.getCompilationUnit(),
                    markerLocation.getOffset(), markerLocation.getLength());
        }
    }

    @Override
    public int hashCode() {
        return marker.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ErrorTreeNode)
                && marker.equals(((ErrorTreeNode) obj).marker);
    }

    public void addResolution(ActionableMarkerResolution resolution,
            TreeUpdater treeUpdater) {
        resolutions.add(resolution);
        MarkerResolutionTreeNode child = new MarkerResolutionTreeNode(
                resolution, treeUpdater);
        child.computeChangeEffect();
        addChild(child);
    }

    @Override
    public int getRank() {
        return getMaxErrorsFixedByChildren();
    }

    private int getMaxErrorsFixedByChildren() {
        int maxErrorsFixed = 0;
        for (TreeObject child : getChildren()) {
            if (child instanceof MarkerResolutionTreeNode) {
                maxErrorsFixed = Integer.max(maxErrorsFixed,
                        ((MarkerResolutionTreeNode) child).getErrorsFixed());
            }
        }
        return maxErrorsFixed;
    }

    public TreeObject[] getChildren() {
        return super.getChildren();
    }

    public static Collection<ErrorTreeNode> createTreeNodesFrom(
            ErrorTreeNodeFactory errorTreeNodeFactory,
            Set<ActionableMarkerResolution> resolutions,
            TreeUpdater treeUpdater, boolean computeResolutionEffects) {
        Map<ComparableMarker, ErrorTreeNode> allNodes = new HashMap<>();
        for (ActionableMarkerResolution resolution : resolutions) {
            Set<ComparableMarker> markersToBeResolved = resolution
                    .getMarkersToBeResolvedByFixer();
            for (ComparableMarker comparableMarker : markersToBeResolved) {
                if (!allNodes.containsKey(comparableMarker)) {
                    ErrorTreeNode newErrorNode = errorTreeNodeFactory
                            .get(comparableMarker);
                    allNodes.put(comparableMarker, newErrorNode);
                }
                if (computeResolutionEffects) {
                    allNodes.get(comparableMarker).addResolution(resolution,
                            treeUpdater);
                }
            }
        }
        return allNodes.values();
    }

    public static Collection<ErrorTreeNode> createTreeNodesFrom(
            ErrorTreeNodeFactory errorTreeNodeFactory,
            Set<ActionableMarkerResolution> resolutions, TreeUpdater treeUpdater) {
        return createTreeNodesFrom(errorTreeNodeFactory, resolutions,
                treeUpdater, true);
    }

    public static Collection<ErrorTreeNode> createTreeNodesFrom(
            Set<ActionableMarkerResolution> resolutions,
            TreeUpdater treeUpdater, boolean computeResolutionEffects) {
        return ErrorTreeNode.createTreeNodesFrom(new ErrorTreeNodeFactory(),
                resolutions, treeUpdater, computeResolutionEffects);
    }

}
