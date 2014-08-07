package checker.framework.errorcentric.view.views;

/**
 * We need this class for ErrorTreeNodes which do not need to be able to update
 * the state of the tree.
 */
public class NoOpTreeUpdater implements TreeUpdater {

    @Override
    public void update(TreeObject node) {
    }

    @Override
    public void recomputeDisabledNodes() {
    }

}
