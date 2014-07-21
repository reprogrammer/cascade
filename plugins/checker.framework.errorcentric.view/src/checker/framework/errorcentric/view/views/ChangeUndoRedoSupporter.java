package checker.framework.errorcentric.view.views;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryEvent;

import static com.google.common.collect.Maps.newHashMap;

public class ChangeUndoRedoSupporter {
    private MarkerResolutionTreeNode resolutionTreeNode;
    private Map<IUndoableOperation, MarkerResolutionTreeNode> operationMap;
    private Map<MarkerResolutionTreeNode, Set<TreeObject>> disabledNodesMap;
    private IOperationHistory operationHistory;
    private ChangeStateViewer changeStateViewer;

    public ChangeUndoRedoSupporter(IOperationHistory operationHistory,
            ChangeStateViewer changeStateViewer) {
        this.operationMap = newHashMap();
        this.disabledNodesMap = newHashMap();
        this.operationHistory = operationHistory;
        this.changeStateViewer = changeStateViewer;
    }

    public void initialize() {
        operationHistory
                .addOperationHistoryListener(new IOperationHistoryListener() {
                    @Override
                    public void historyNotification(OperationHistoryEvent event) {
                        if (event.getEventType() == OperationHistoryEvent.UNDONE) {
                            MarkerResolutionTreeNode markerResolutionTreeNode = operationMap
                                    .get(event.getOperation());
                            if (markerResolutionTreeNode != null) {
                                Set<TreeObject> disabledNodes = disabledNodesMap
                                        .get(markerResolutionTreeNode);
                                changeStateViewer.enableChange(disabledNodes);
                            }
                        } else if (event.getEventType() == OperationHistoryEvent.REDONE) {
                            MarkerResolutionTreeNode markerResolutionTreeNode = operationMap
                                    .get(event.getOperation());
                            if (markerResolutionTreeNode != null) {
                                Set<TreeObject> newDisabledNodes = changeStateViewer
                                        .disableChange(markerResolutionTreeNode);
                                disabledNodesMap.put(markerResolutionTreeNode,
                                        newDisabledNodes);

                            }
                        } else if (event.getEventType() == OperationHistoryEvent.ABOUT_TO_EXECUTE) {
                            if (resolutionTreeNode != null) {
                                operationMap.put(event.getOperation(),
                                        resolutionTreeNode);
                            }
                        } else if (event.getEventType() == OperationHistoryEvent.DONE) {
                            if (resolutionTreeNode != null) {
                                resolutionTreeNode = null;
                            }
                        }
                    }
                });
    }

    public void prepareToApplyUndoableChange(
            MarkerResolutionTreeNode resolutionTreeNode) {
        this.resolutionTreeNode = resolutionTreeNode;
        Set<TreeObject> newDisabledNodes = changeStateViewer
                .disableChange(resolutionTreeNode);
        disabledNodesMap.put(resolutionTreeNode, newDisabledNodes);
    }

    public void applyUndoableChange(MarkerResolutionTreeNode resolutionTreeNode) {
        resolutionTreeNode.getResolution().run();
    }
}
