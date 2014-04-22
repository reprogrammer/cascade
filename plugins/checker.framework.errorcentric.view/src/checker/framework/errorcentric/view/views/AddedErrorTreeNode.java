package checker.framework.errorcentric.view.views;

import java.util.Collection;
import java.util.Set;

import checker.framework.change.propagator.ActionableMarkerResolution;
import checker.framework.change.propagator.ComparableMarker;

public class AddedErrorTreeNode extends ErrorTreeNode {

    public AddedErrorTreeNode(ComparableMarker marker) {
        super(marker);
    }

    public static Collection<ErrorTreeNode> createTreeNodesFrom(
            Set<ActionableMarkerResolution> resolutions, TreeUpdater treeUpdater) {
        return ErrorTreeNode.createTreeNodesFrom(
                new AddedErrorTreeNodeFactory(), resolutions, treeUpdater);
    }

}
