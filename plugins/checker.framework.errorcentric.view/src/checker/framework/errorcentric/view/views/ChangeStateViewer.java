package checker.framework.errorcentric.view.views;
import java.util.Set;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.TreeItem;

import static com.google.common.collect.Sets.newHashSet;

public class ChangeStateViewer {
    private Set<TreeObject> disabledNodes;
    private TreeViewer viewer;

    public ChangeStateViewer(TreeViewer viewer) {
        this.disabledNodes = newHashSet();
        this.viewer = viewer;

    }

    public void disableChange(MarkerResolutionTreeNode resolutionTreeNode) {
        Set<TreeObject> nodesToBeDisabled = newHashSet();
        nodesToBeDisabled.add(resolutionTreeNode);
        nodesToBeDisabled.addAll(ErrorTreeNode.createTreeNodesFrom(
                newHashSet(resolutionTreeNode.getResolution()),
                new NoOpTreeUpdater(), false));
        setNodeColor(nodesToBeDisabled, Colors.GRAY);
        disabledNodes.addAll(nodesToBeDisabled);
    }

    public void enableChange(MarkerResolutionTreeNode resolutionTreeNode) {
        Set<TreeObject> nodesToBeEnabled = newHashSet();
        nodesToBeEnabled.add(resolutionTreeNode);
        nodesToBeEnabled.addAll(ErrorTreeNode.createTreeNodesFrom(
                newHashSet(resolutionTreeNode.getResolution()),
                new NoOpTreeUpdater(), false));
        setNodeColor(nodesToBeEnabled, viewer.getTree().getForeground());
        disabledNodes.removeAll(nodesToBeEnabled);
    }

    private void setNodeColor(Set<TreeObject> nodes, Color color) {
        setNodeColor(viewer.getTree().getItems(), nodes, color);
    }

    private void setNodeColor(TreeItem[] items, Set<TreeObject> nodes,
            Color color) {
        for (TreeItem item : items) {
            if (nodes.contains(item.getData())) {
                setNodeColor(item, color);
            }
            setNodeColor(item.getItems(), nodes, color);
        }
    }

    private void setNodeColor(TreeItem item, Color color) {
        item.setForeground(color);
    }

    public boolean isDisabled(TreeObject treeObject) {
        return disabledNodes.contains(treeObject);
    }
}
