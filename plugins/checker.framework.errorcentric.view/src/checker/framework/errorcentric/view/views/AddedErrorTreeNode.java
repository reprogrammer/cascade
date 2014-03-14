package checker.framework.errorcentric.view.views;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import checker.framework.change.propagator.ActionableMarkerResolution;
import checker.framework.change.propagator.ComparableMarker;

public class AddedErrorTreeNode extends ErrorTreeNode {

    public AddedErrorTreeNode(ComparableMarker marker) {
        super(marker);
    }

    public static Collection<ErrorTreeNode> createTreeNodesFrom(
            Set<ActionableMarkerResolution> resolutions) {
        Map<ComparableMarker, ErrorTreeNode> allNodes = new HashMap<ComparableMarker, ErrorTreeNode>();
        for (ActionableMarkerResolution resolution : resolutions) {
            Set<ComparableMarker> markersToBeResolved = resolution
                    .getMarkersToBeResolvedByFixer();
            for (ComparableMarker comparableMarker : markersToBeResolved) {
                if (allNodes.containsKey(comparableMarker)) {
                    allNodes.get(comparableMarker).addResolution(resolution);
                } else {
                    AddedErrorTreeNode newErrorNode = new AddedErrorTreeNode(
                            comparableMarker);
                    newErrorNode.addResolution(resolution);
                    allNodes.put(comparableMarker, newErrorNode);
                }
            }
        }
        return allNodes.values();
    }

}
