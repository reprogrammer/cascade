package checker.framework.errorcentric.view.views;

import java.lang.reflect.Field;
import java.util.Map;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.commands.operations.TriggeredOperations;
import org.eclipse.ltk.core.refactoring.ContentStamp;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.UndoTextFileChange;
import org.eclipse.ltk.internal.core.refactoring.UndoableOperation2ChangeAdapter;

import static com.google.common.collect.Maps.newHashMap;

public class ChangeUndoRedoSupporter {
    private MarkerResolutionTreeNode resolutionTreeNode;
    private Map<ContentStamp, MarkerResolutionTreeNode> operationMap;

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

                        if (event.getEventType() == OperationHistoryEvent.ABOUT_TO_UNDO) {
                            TriggeredOperations to = (TriggeredOperations) event
                                    .getOperation();
                            UndoableOperation2ChangeAdapter triggeringOperation = (UndoableOperation2ChangeAdapter) to
                                    .getTriggeringOperation();
                            UndoTextFileChange undoTextFileChange = (UndoTextFileChange) triggeringOperation
                                    .getChange();
                            try {
                                Field declaredField = UndoTextFileChange.class
                                        .getDeclaredField("fContentStampToRestore");
                                declaredField.setAccessible(true);
                                ; // NoSuchFieldException
                                ContentStamp contentStampToRestore = (ContentStamp) declaredField
                                        .get(undoTextFileChange);

                                MarkerResolutionTreeNode markerResolutionTreeNode = operationMap
                                        .get(contentStampToRestore);
                                if (markerResolutionTreeNode != null) {
                                    changeStateViewer
                                            .enableChange(markerResolutionTreeNode);
                                }
                            } catch (IllegalArgumentException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (NoSuchFieldException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (SecurityException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        } else if (event.getEventType() == OperationHistoryEvent.REDONE) {
                            TriggeredOperations to = (TriggeredOperations) event
                                    .getOperation();
                            UndoableOperation2ChangeAdapter triggeringOperation = (UndoableOperation2ChangeAdapter) to
                                    .getTriggeringOperation();
                            UndoTextFileChange undoTextFileChange = (UndoTextFileChange) triggeringOperation
                                    .getChange();
                            try {
                                Field declaredField = UndoTextFileChange.class
                                        .getDeclaredField("fContentStampToRestore");
                                declaredField.setAccessible(true);
                                ; // NoSuchFieldException
                                ContentStamp contentStampToRestore = (ContentStamp) declaredField
                                        .get(undoTextFileChange);

                                MarkerResolutionTreeNode markerResolutionTreeNode = operationMap
                                        .get(contentStampToRestore);
                                if (markerResolutionTreeNode != null) {
                                    changeStateViewer
                                            .disableChange(markerResolutionTreeNode);

                                }
                            } catch (IllegalArgumentException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (NoSuchFieldException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (SecurityException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        } else if (event.getEventType() == OperationHistoryEvent.ABOUT_TO_EXECUTE) {
                            TriggeredOperations operation = (TriggeredOperations) event
                                    .getOperation();
                            UndoableOperation2ChangeAdapter triggeringOperation = (UndoableOperation2ChangeAdapter) operation
                                    .getTriggeringOperation();
                            TextFileChange change = (TextFileChange) triggeringOperation
                                    .getChange();

                            Field declaredField;
                            try {
                                declaredField = TextFileChange.class
                                        .getDeclaredField("fContentStamp");
                                declaredField.setAccessible(true);
                                ; // NoSuchFieldException
                                ContentStamp contentStamp = (ContentStamp) declaredField
                                        .get(change);
                                if (resolutionTreeNode != null) {
                                    operationMap.put(contentStamp,
                                            resolutionTreeNode);
                                }
                            } catch (NoSuchFieldException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (SecurityException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IllegalArgumentException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
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
