package checker.framework.errorcentric.view.views;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryEvent;

public class ChangeUndoRedoSupporter {
    private class MarkerResolutionTreeNodeContext implements IUndoContext {
        private MarkerResolutionTreeNode markerResolutionTreeNode;

        public MarkerResolutionTreeNodeContext(
                MarkerResolutionTreeNode markerResolutionTreeNode) {
            this.markerResolutionTreeNode = markerResolutionTreeNode;
        }

        public MarkerResolutionTreeNode getMarkerResolutionTreeNode() {
            return markerResolutionTreeNode;
        }

        @Override
        public String getLabel() {
            return markerResolutionTreeNode.getName();
        }

        @Override
        public boolean matches(IUndoContext context) {
            if (context instanceof MarkerResolutionTreeNodeContext) {
                MarkerResolutionTreeNodeContext other = (MarkerResolutionTreeNodeContext) context;
                return this.markerResolutionTreeNode
                        .equals(other.markerResolutionTreeNode);
            }
            return false;
        }

    }

    private MarkerResolutionTreeNode resolutionTreeNode;

    private IOperationHistory operationHistory;
    private ChangeStateViewer changeStateViewer;

    public ChangeUndoRedoSupporter(IOperationHistory operationHistory,
            ChangeStateViewer changeStateViewer) {
        this.operationHistory = operationHistory;
        this.changeStateViewer = changeStateViewer;
    }

    public void initialize() {
        operationHistory
                .addOperationHistoryListener(new IOperationHistoryListener() {
                    @Override
                    public void historyNotification(OperationHistoryEvent event) {

                        if (event.getEventType() == OperationHistoryEvent.ABOUT_TO_UNDO) {
                            IUndoContext[] contexts = event.getOperation()
                                    .getContexts();
                            for (IUndoContext context : contexts) {
                                if (context instanceof MarkerResolutionTreeNodeContext) {
                                    MarkerResolutionTreeNode markerResolutionTreeNode = ((MarkerResolutionTreeNodeContext) context)
                                            .getMarkerResolutionTreeNode();
                                    changeStateViewer
                                            .enableChange(markerResolutionTreeNode);
                                }
                            }
                        } else if (event.getEventType() == OperationHistoryEvent.REDONE) {
                            IUndoContext[] contexts = event.getOperation()
                                    .getContexts();
                            for (IUndoContext context : contexts) {
                                if (context instanceof MarkerResolutionTreeNodeContext) {
                                    MarkerResolutionTreeNode markerResolutionTreeNode = ((MarkerResolutionTreeNodeContext) context)
                                            .getMarkerResolutionTreeNode();
                                    changeStateViewer
                                            .disableChange(markerResolutionTreeNode);
                                }
                            }
                        } else if (event.getEventType() == OperationHistoryEvent.ABOUT_TO_EXECUTE) {
                            if (resolutionTreeNode != null) {
                                IUndoableOperation operation = event
                                        .getOperation();
                                operation
                                        .addContext(new MarkerResolutionTreeNodeContext(
                                                resolutionTreeNode));
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
        changeStateViewer.disableChange(resolutionTreeNode);
    }

    public void applyUndoableChange(MarkerResolutionTreeNode resolutionTreeNode) {
        resolutionTreeNode.getResolution().run();
    }
}
