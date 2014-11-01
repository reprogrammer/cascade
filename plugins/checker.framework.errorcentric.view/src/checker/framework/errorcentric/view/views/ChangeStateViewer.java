package checker.framework.errorcentric.view.views;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

import static com.google.common.collect.Sets.difference;
import static com.google.common.collect.Sets.newHashSet;

import static com.google.common.collect.Maps.newHashMap;

public class ChangeStateViewer {
    private Set<TreeObject> disabledNodes;
    private TreeViewer viewer;
    private Map<MarkerResolutionTreeNode, Set<TreeObject>> disabledNodesMap;

    public ChangeStateViewer(TreeViewer viewer) {
        this.disabledNodes = newHashSet();
        this.viewer = viewer;
        this.disabledNodesMap = newHashMap();
    }

    public void resetState() {
        disabledNodes.clear();
    }

    public void recomputeDisabledChanges() {
        Set<TreeObject> treeObjects = newHashSet(disabledNodes);
        for (TreeObject treeObject : treeObjects) {
            if (treeObject instanceof MarkerResolutionTreeNode) {
                disableChange((MarkerResolutionTreeNode) treeObject);
            }
        }
    }

    /**
     * Disables a change node and (1) all the errors it fixes (2) all the other
     * "equivalent" change node in the tree, i.e. is the same change and fixes
     * the same set of errors (3) all the errors that (2) fixes
     * 
     * @param resolutionTreeNode
     */
    public void disableChange(MarkerResolutionTreeNode resolutionTreeNode) {
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
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                viewer.refresh();
            }
        });
        if (disabledNodesMap.containsKey(resolutionTreeNode)) {
            disabledNodesMap.get(resolutionTreeNode).addAll(newDisabledNodes);
        } else {
            disabledNodesMap.put(resolutionTreeNode, newDisabledNodes);
        }
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

    private void enableChange(Set<TreeObject> nodesToBeEnabled) {
        disabledNodes.removeAll(nodesToBeEnabled);
        viewer.refresh();
    }

    public void enableChange(MarkerResolutionTreeNode markerResolutionTreeNode) {
        Set<TreeObject> disabledNodesToBeEnabled = disabledNodesMap
                .get(markerResolutionTreeNode);
        enableChange(disabledNodesToBeEnabled);
    }

    private Set<TreeObject> getRelatedNodes(Set<TreeObject> nodes) {
        Set<TreeObject> relatedNodes = new HashSet<>();
        relatedNodes.addAll(getRelatedNodes(((MarkerResolutionTreeNode) viewer
                .getInput()).getExistingChildren(), nodes));
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

            relatedNodes.addAll(getRelatedNodes(
                    candidate.getExistingChildren(), existingNodes));
        }
        return relatedNodes;
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
