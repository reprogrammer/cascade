package checker.framework.errorcentric.view.views;

import static com.google.common.collect.Sets.difference;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.TreeItem;

public class ChangeStateViewer {
    private Set<TreeObject> disabledNodes;
    private TreeViewer viewer;

    public ChangeStateViewer(TreeViewer viewer) {
        this.disabledNodes = newHashSet();
        this.viewer = viewer;
    }

    public void resetState() {
        disabledNodes.clear();
    }

    public Set<TreeObject> disableChange(
            MarkerResolutionTreeNode resolutionTreeNode) {
        // init
        Set<TreeObject> clonedDisabledNodes = newHashSet(disabledNodes);
        clonedDisabledNodes.add(resolutionTreeNode);
        clonedDisabledNodes.addAll(ErrorTreeNode.createTreeNodesFrom(
                newHashSet(resolutionTreeNode.getResolution()),
                new NoOpTreeUpdater(), false));
        // find fixed point
        while (canAddRelatedNodes(clonedDisabledNodes)) {
        }
        Set<TreeObject> newDisabledNodes = newHashSet(difference(
                clonedDisabledNodes, disabledNodes));
        // swap the clone
        disabledNodes = clonedDisabledNodes;
        viewer.refresh();
        return newDisabledNodes;
    }

    private boolean canAddRelatedNodes(Set<TreeObject> nodes) {
        Set<TreeObject> relatedNodes = getRelatedNodes(nodes);
        if (nodes.containsAll(relatedNodes)) {
            return false;
        } else {
            Set<TreeObject> newNodes = difference(relatedNodes, nodes);
            for (TreeObject newNode : newNodes) {
                if (newNode instanceof MarkerResolutionTreeNode) {
                    nodes.add(newNode);
                    Collection<ErrorTreeNode> errorTreeNodes = ErrorTreeNode
                            .createTreeNodesFrom(
                                    newHashSet(((MarkerResolutionTreeNode) newNode)
                                            .getResolution()),
                                    new NoOpTreeUpdater(), false);
                    nodes.addAll(errorTreeNodes);

                }
            }
            return true;
        }
    }

    public void enableChange(Set<TreeObject> nodesToBeEnabled) {
        disabledNodes.removeAll(nodesToBeEnabled);
        viewer.refresh();
    }

    private Set<TreeObject> getRelatedNodes(Set<TreeObject> nodes) {
        Set<TreeObject> relatedNodes = new HashSet<>();
        relatedNodes.addAll(getRelatedNodes(
                ((MarkerResolutionTreeNode) viewer.getInput()).getChildren(),
                nodes));
        return relatedNodes;
    }

    private Set<TreeObject> getRelatedNodes(TreeObject[] candidates,
            Set<TreeObject> existingNodes) {
        Set<TreeObject> relatedNodes = new HashSet<>();
        for (TreeObject candidate : candidates) {
            if (isRelated(candidate, existingNodes)) {
                relatedNodes.add(candidate);
                if (candidate instanceof MarkerResolutionTreeNode) {
                    Set<ErrorTreeNode> errorsFixed = ((MarkerResolutionTreeNode) candidate)
                            .getErrorsFixed();
                    relatedNodes.addAll(errorsFixed);
                }
            }
            relatedNodes.addAll(getRelatedNodes(candidate.getChildren(),
                    existingNodes));
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

    private boolean isRelated(TreeObject treeObject,
            Set<TreeObject> existingNodes) {
        if (treeObject instanceof ErrorTreeNode) {
            return existingNodes.contains(treeObject);
        } else if (treeObject instanceof MarkerResolutionTreeNode) {
            MarkerResolutionTreeNode nodeToTest = ((MarkerResolutionTreeNode) treeObject);
            for (TreeObject node : existingNodes) {
                if (node instanceof MarkerResolutionTreeNode) {
                    MarkerResolutionTreeNode existingNode = ((MarkerResolutionTreeNode) node);
                    if (nodeToTest.hasSameResolution(existingNode)
                            || existingNodes.containsAll(nodeToTest
                                    .getErrorsFixed())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
