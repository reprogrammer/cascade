package checker.framework.errorcentric.view.views;

import java.util.Map;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryEvent;

import static com.google.common.collect.Maps.newHashMap;

public class ChangeUndoRedoSupporter {
    private MarkerResolutionTreeNode resolutionTreeNode;
    private Map<IUndoableOperation, MarkerResolutionTreeNode> operationMap;
    private IOperationHistory operationHistory;
    private ChangeStateViewer changeStateViewer;

    public ChangeUndoRedoSupporter(IOperationHistory operationHistory,
            ChangeStateViewer changeStateViewer) {
        this.operationMap = newHashMap();
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
                                changeStateViewer
                                        .enableChange(markerResolutionTreeNode);
                            }
                        } else if (event.getEventType() == OperationHistoryEvent.REDONE) {
                            MarkerResolutionTreeNode markerResolutionTreeNode = operationMap
                                    .get(event.getOperation());
                            if (markerResolutionTreeNode != null) {
                                changeStateViewer
                                        .disableChange(markerResolutionTreeNode);

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

    public void applyUndoableChange(MarkerResolutionTreeNode resolutionTreeNode) {
        this.resolutionTreeNode = resolutionTreeNode;
        resolutionTreeNode.getResolution().run();
        changeStateViewer.disableChange(resolutionTreeNode);
    }
}
