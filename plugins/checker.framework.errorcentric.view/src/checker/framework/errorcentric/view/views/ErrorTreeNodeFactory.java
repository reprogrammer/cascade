package checker.framework.errorcentric.view.views;

import checker.framework.change.propagator.ComparableMarker;

public class ErrorTreeNodeFactory {

    public ErrorTreeNode get(ComparableMarker comparableMarker) {
        return new ErrorTreeNode(comparableMarker);
    }

}
