package checker.framework.errorcentric.view.views;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

import checker.framework.change.propagator.ActionableMarkerResolution;
import checker.framework.change.propagator.ShadowProject;
import checker.framework.change.propagator.ShadowProjectFactory;
import checker.framework.errorcentric.propagator.commands.InferCommandHandler;
import checker.framework.errorcentric.propagator.commands.InferNullnessCommandHandler;
import checker.framework.errorcentric.view.Activator;
import checker.framework.quickfixes.descriptors.Fixer;

import com.google.common.base.Optional;

import static com.google.common.collect.Maps.newHashMap;

import static com.google.common.collect.Sets.newHashSet;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class ErrorCentricView extends ViewPart implements TreeLabelUpdater {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "checker.framework.errorcentric.view.views.ErrorCentricView";

    private TreeViewer viewer;
    private DrillDownAdapter drillDownAdapter;
    private Action refreshAction;
    private Action doubleClickAction;
    private TreeObject invisibleRoot;
    private IJavaProject javaProject;
    private Set<TreeObject> disabledNodes;

    private MarkerResolutionTreeNode selectedResolutionTreeNode;
    private Map<IUndoableOperation, MarkerResolutionTreeNode> operationMap;

    /**
     * The constructor.
     */
    public ErrorCentricView() {
    }

    private TreeObject initializeInput() {
        invisibleRoot = new TreeObject("");
        if (InferCommandHandler.checkerID == null) {
            return null;
        }
        if (!InferCommandHandler.selectedJavaProject.isPresent()) {
            return null;
        }
        javaProject = InferCommandHandler.selectedJavaProject.get();
        ShadowProject shadowProject = new ShadowProjectFactory(javaProject)
                .get();
        shadowProject.runChecker(InferCommandHandler.checkerID);
        Set<ActionableMarkerResolution> resolutions = shadowProject
                .getResolutions();
        invisibleRoot.addChildren(AddedErrorTreeNode.createTreeNodesFrom(
                resolutions, this));
        return invisibleRoot;
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    public void createPartControl(Composite parent) {
        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        drillDownAdapter = new DrillDownAdapter(viewer);
        viewer.setContentProvider(new ViewContentProvider());
        viewer.setLabelProvider(new ViewLabelProvider());
        viewer.setSorter(new NameSorter());
        viewer.setInput(initializeInput());
        viewer.getTree().setLinesVisible(true);
        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        hookSelectionAction();
        contributeToActionBars();
        disabledNodes = newHashSet();
        operationMap = newHashMap();
        registerOperationHistoryListeners();
    }

    private void registerOperationHistoryListeners() {
        IWorkbench workbench = getSite().getWorkbenchWindow().getWorkbench();
        IOperationHistory operationHistory = workbench.getOperationSupport()
                .getOperationHistory();
        operationHistory
                .addOperationHistoryListener(new IOperationHistoryListener() {
                    @Override
                    public void historyNotification(OperationHistoryEvent event) {
                        if (event.getEventType() == OperationHistoryEvent.UNDONE) {
                            MarkerResolutionTreeNode markerResolutionTreeNode = operationMap
                                    .get(event.getOperation());
                            if (markerResolutionTreeNode != null) {
                                enableChange(markerResolutionTreeNode);
                            }
                        } else if (event.getEventType() == OperationHistoryEvent.REDONE) {
                            MarkerResolutionTreeNode markerResolutionTreeNode = operationMap
                                    .get(event.getOperation());
                            if (markerResolutionTreeNode != null) {
                                disableChange(markerResolutionTreeNode);

                            }
                        } else if (event.getEventType() == OperationHistoryEvent.ABOUT_TO_EXECUTE) {
                            if (selectedResolutionTreeNode != null) {
                                operationMap.put(event.getOperation(),
                                        selectedResolutionTreeNode);
                            }
                        } else if (event.getEventType() == OperationHistoryEvent.DONE) {
                            if (selectedResolutionTreeNode != null) {
                                selectedResolutionTreeNode = null;
                            }
                        }
                    }
                });
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                ErrorCentricView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager) {
        manager.add(refreshAction);
    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(refreshAction);
        manager.add(new Separator());
        drillDownAdapter.addNavigationActions(manager);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(refreshAction);
        manager.add(new Separator());
        drillDownAdapter.addNavigationActions(manager);
    }

    private void makeActions() {
        refreshAction = new Action() {
            public void run() {
                viewer.refresh();
            }
        };
        refreshAction.setText("Refresh");
        refreshAction.setToolTipText("Recomputes the error/change tree.");
        refreshAction.setImageDescriptor(ImageDescriptor
                .createFromImage(Activator.getImageDescriptor(
                        "icons/refresh.gif").createImage()));

        final ErrorCentricView view = this;
        doubleClickAction = new Action() {
            public void run() {
                Optional<TreeObject> selectedTreeObject = getSelectedTreeObject(viewer
                        .getSelection());
                if (selectedTreeObject.isPresent()
                        && disabledNodes.contains(selectedTreeObject.get())) {
                    return;
                }
                Optional<MarkerResolutionTreeNode> resolution = getSelectedMarkResolution(selectedTreeObject);
                if (resolution.isPresent()) {
                    MarkerResolutionTreeNode resolutionTreeNode = resolution
                            .get();
                    view.selectedResolutionTreeNode = resolutionTreeNode;
                    resolutionTreeNode.getResolution().run();
                    view.selectedResolutionTreeNode = null;
                    disableChange(resolutionTreeNode);
                }
                Optional<ErrorTreeNode> error = getSelectedError(selectedTreeObject);
                if (error.isPresent()) {
                    ErrorTreeNode errorTreeNode = error.get();
                    errorTreeNode.reveal();
                }

            }

        };
    }

    private void disableChange(MarkerResolutionTreeNode resolutionTreeNode) {
        Set<TreeObject> nodesToBeDisabled = newHashSet();
        nodesToBeDisabled.add(resolutionTreeNode);
        nodesToBeDisabled.addAll(ErrorTreeNode.createTreeNodesFrom(
                newHashSet(resolutionTreeNode.getResolution()),
                new NoOpTreeLabelUpdater(), false));
        setNodeColor(nodesToBeDisabled, Colors.GRAY);
        disabledNodes.addAll(nodesToBeDisabled);
    }

    private void enableChange(MarkerResolutionTreeNode resolutionTreeNode) {
        Set<TreeObject> nodesToBeEnabled = newHashSet();
        nodesToBeEnabled.add(resolutionTreeNode);
        nodesToBeEnabled.addAll(ErrorTreeNode.createTreeNodesFrom(
                newHashSet(resolutionTreeNode.getResolution()),
                new NoOpTreeLabelUpdater(), false));
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

    private Optional<MarkerResolutionTreeNode> getSelectedMarkResolution(
            Optional<TreeObject> optionalTreeObject) {
        Optional<MarkerResolutionTreeNode> optionalMarkerTreeNode = Optional
                .absent();
        if (optionalTreeObject.isPresent()) {
            if (optionalTreeObject.isPresent()) {
                TreeObject treeObject = optionalTreeObject.get();
                if (treeObject instanceof MarkerResolutionTreeNode) {
                    optionalMarkerTreeNode = Optional
                            .of((MarkerResolutionTreeNode) treeObject);
                }
            }
        }
        return optionalMarkerTreeNode;
    }

    private Optional<ErrorTreeNode> getSelectedError(
            Optional<TreeObject> optionalTreeObject) {
        Optional<ErrorTreeNode> optionalMarkerTreeNode = Optional.absent();
        if (optionalTreeObject.isPresent()) {
            if (optionalTreeObject.isPresent()) {
                TreeObject treeObject = optionalTreeObject.get();
                if (treeObject instanceof ErrorTreeNode) {
                    optionalMarkerTreeNode = Optional
                            .of((ErrorTreeNode) treeObject);
                }
            }
        }
        return optionalMarkerTreeNode;
    }

    private Optional<TreeObject> getSelectedTreeObject(ISelection selection) {
        Object selectedObject = ((IStructuredSelection) selection)
                .getFirstElement();
        if (selectedObject instanceof TreeObject) {
            return Optional.of((TreeObject) selectedObject);
        }
        return Optional.absent();
    }

    private void hookDoubleClickAction() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                doubleClickAction.run();
            }
        });
    }

    private void hookSelectionAction() {
        viewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                Optional<TreeObject> selectedTreeObject = getSelectedTreeObject(event
                        .getSelection());
                if (selectedTreeObject.isPresent()) {
                    if (disabledNodes.contains(selectedTreeObject.get())) {
                        return;
                    }
                    Optional<MarkerResolutionTreeNode> optionalResolution = getSelectedMarkResolution(selectedTreeObject);
                    if (optionalResolution.isPresent()) {
                        MarkerResolutionTreeNode resolutionTreeNode = optionalResolution
                                .get();
                        IJavaProject javaProject = InferNullnessCommandHandler.selectedJavaProject
                                .get();
                        Fixer fixer = resolutionTreeNode.getResolution()
                                .createFixer(javaProject);
                        selectAndReveal(fixer);
                        viewer.setLabelProvider(new DecoratingLabelProvider(
                                new ViewLabelProvider(),
                                new FixedErrorDecorator(resolutionTreeNode)));
                    } else {
                        viewer.setLabelProvider(new ViewLabelProvider());
                    }
                }
            }

            private void selectAndReveal(Fixer fixer) {
                new CodeSnippetRevealer().reveal(fixer.getCompilationUnit(),
                        fixer.getOffset(), fixer.getLength());
            }

        });
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    @Override
    public void update(final TreeObject node) {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                viewer.update(node, null);
            }
        });
    }
}