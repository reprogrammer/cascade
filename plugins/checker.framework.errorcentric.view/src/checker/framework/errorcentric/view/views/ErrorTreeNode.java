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
            TreeLabelUpdater labelUpdater) {
        resolutions.add(resolution);
        MarkerResolutionTreeNode child = new MarkerResolutionTreeNode(
                resolution);
        child.setLabelUpdateListener(labelUpdater);
        child.computeChangeEffectAsync();
        addChild(child);
    }

    @Override
    public boolean hasChildren() {
        return true;
    }

    public TreeObject[] getChildren() {
        return super.getChildren();
    }

    public static Collection<ErrorTreeNode> createTreeNodesFrom(
            ErrorTreeNodeFactory errorTreeNodeFactory,
            Set<ActionableMarkerResolution> resolutions,
            TreeLabelUpdater labelUpdater, boolean computeResolutionEffects) {
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
                            labelUpdater);
                }
            }
        }
        return allNodes.values();
    }

    public static Collection<ErrorTreeNode> createTreeNodesFrom(
            ErrorTreeNodeFactory errorTreeNodeFactory,
            Set<ActionableMarkerResolution> resolutions,
            TreeLabelUpdater labelUpdater) {
        return createTreeNodesFrom(errorTreeNodeFactory, resolutions,
                labelUpdater, true);
    }

    public static Collection<ErrorTreeNode> createTreeNodesFrom(
            Set<ActionableMarkerResolution> resolutions,
            TreeLabelUpdater labelUpdater, boolean computeResolutionEffects) {
        return ErrorTreeNode.createTreeNodesFrom(new ErrorTreeNodeFactory(),
                resolutions, labelUpdater, computeResolutionEffects);
    }

}
