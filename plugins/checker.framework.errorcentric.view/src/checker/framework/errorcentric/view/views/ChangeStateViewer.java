package checker.framework.errorcentric.view.views;

import java.util.HashSet;
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
        nodesToBeDisabled.addAll(ErrorTreeNode.createTreeNodesFrom(
                newHashSet(resolutionTreeNode.getResolution()),
                new NoOpTreeUpdater(), false));
        Set<TreeObject> relatedNodes = getRelatedNodes(nodesToBeDisabled);
        nodesToBeDisabled.addAll(relatedNodes);
        setNodeColor(nodesToBeDisabled, Colors.GRAY);
        disabledNodes.addAll(nodesToBeDisabled);
    }

    public void enableChange(MarkerResolutionTreeNode resolutionTreeNode) {
        Set<TreeObject> nodesToBeEnabled = newHashSet();
        nodesToBeEnabled.addAll(ErrorTreeNode.createTreeNodesFrom(
                newHashSet(resolutionTreeNode.getResolution()),
                new NoOpTreeUpdater(), false));
        Set<TreeObject> relatedNodes = getRelatedNodes(nodesToBeEnabled);
        nodesToBeEnabled.addAll(relatedNodes);
        setNodeColor(nodesToBeEnabled, viewer.getTree().getForeground());
        disabledNodes.removeAll(nodesToBeEnabled);
    }

    private Set<TreeObject> getRelatedNodes(Set<TreeObject> nodes) {
        Set<TreeObject> relatedNodes = new HashSet<>();
        relatedNodes
                .addAll(getRelatedNodes(viewer.getTree().getItems(), nodes));
        return relatedNodes;
    }

    private Set<TreeObject> getRelatedNodes(TreeItem[] items,
            Set<TreeObject> nodes) {
        Set<TreeObject> relatedNodes = new HashSet<>();
        for (TreeItem item : items) {
            TreeObject node = (TreeObject) item.getData();
            if (isRelated(node, nodes)) {
                relatedNodes.add(node);
            }
            relatedNodes.addAll(getRelatedNodes(item.getItems(), nodes));
        }
        return relatedNodes;
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

    private boolean isRelated(TreeObject treeObject, Set<TreeObject> nodes) {
        if (nodes.contains(treeObject)) {
            return true;
        }

        if (treeObject instanceof MarkerResolutionTreeNode) {
            MarkerResolutionTreeNode resolutionTreeNode = ((MarkerResolutionTreeNode) treeObject);
            Set<ErrorTreeNode> errorsFixedByResolution = newHashSet(ErrorTreeNode
                    .createTreeNodesFrom(
                            newHashSet(resolutionTreeNode.getResolution()),
                            new NoOpTreeUpdater(), false));
            errorsFixedByResolution.removeAll(nodes);
            return errorsFixedByResolution.isEmpty();
        }
        return false;
    }
}
