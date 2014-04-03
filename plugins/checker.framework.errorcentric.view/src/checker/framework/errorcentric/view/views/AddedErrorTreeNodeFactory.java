package checker.framework.errorcentric.view.views;

import checker.framework.change.propagator.ComparableMarker;

public class AddedErrorTreeNodeFactory extends ErrorTreeNodeFactory {

    @Override
    public ErrorTreeNode get(ComparableMarker comparableMarker) {
        return new AddedErrorTreeNode(comparableMarker);
    }

}
