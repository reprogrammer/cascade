package checker.framework.errorcentric.view.views;

import java.util.HashSet;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

import checker.framework.change.propagator.ActionableMarkerResolution;
import checker.framework.change.propagator.ShadowOfShadowProject;
import checker.framework.change.propagator.ShadowOfShadowProjectFactory;
import checker.framework.change.propagator.ShadowProject;
import checker.framework.change.propagator.ShadowProjectFactory;
import checker.framework.errorcentric.propagator.commands.InferCommandHandler;
import checker.framework.errorcentric.propagator.commands.InferNullnessCommandHandler;
import checker.framework.errorcentric.view.Activator;
import checker.framework.errorcentric.view.Messages;
import checker.framework.quickfixes.WorkspaceUtils;
import checker.framework.quickfixes.descriptors.Fixer;

import com.google.common.base.Optional;

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

public class ErrorCentricView extends ViewPart implements TreeUpdater {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "checker.framework.errorcentric.view.views.ErrorCentricView"; //$NON-NLS-1$

    private TreeViewer viewer;
    private DrillDownAdapter drillDownAdapter;
    private Action refreshAction;
    private Action doubleClickAction;
    private MarkerResolutionTreeNode invisibleRoot;
    private IJavaProject javaProject;

    private ChangeUndoRedoSupporter changeUndoRedoSupporter;
    private ChangeStateViewer changeStateViewer;

    /**
     * The constructor.
     */
    public ErrorCentricView() {
    }

    private TreeObject initializeInput() {
        if (InferCommandHandler.checkerID == null) {
            return null;
        }
        if (!InferCommandHandler.selectedJavaProject.isPresent()) {
            return null;
        }
        javaProject = InferCommandHandler.selectedJavaProject.get();
        ShadowProject shadowProject = new ShadowProjectFactory(javaProject)
                .get();
        ShadowOfShadowProject shadowOfShadowProject = new ShadowOfShadowProjectFactory(
                shadowProject.getProject()).get();
        shadowOfShadowProject.runChecker(InferCommandHandler.checkerID);

        ActionableMarkerResolution identityResolution = new ActionableMarkerResolution(
                shadowOfShadowProject, new IdentityMarkerResolution(),
                new HashSet<>(), new IdentityFixerDescriptor(), new HashSet<>());
        invisibleRoot = new MarkerResolutionTreeNode(identityResolution, this);
        invisibleRoot.computeChangeEffect();
        return invisibleRoot;
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    public void createPartControl(Composite parent) {
        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        drillDownAdapter = new DrillDownAdapter(viewer);
        changeStateViewer = new ChangeStateViewer(viewer);
        viewer.setContentProvider(new ViewContentProvider());
        resetLabelProvider();
        viewer.setSorter(new NameSorter());
        viewer.setInput(initializeInput());
        viewer.getTree().setLinesVisible(true);
        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        hookSelectionAction();
        contributeToActionBars();
        initializeChangeUndoRedoSupporter();
    }

    private void initializeChangeUndoRedoSupporter() {
        IWorkbench workbench = getSite().getWorkbenchWindow().getWorkbench();
        IOperationHistory operationHistory = workbench.getOperationSupport()
                .getOperationHistory();
        changeUndoRedoSupporter = new ChangeUndoRedoSupporter(operationHistory,
                changeStateViewer);
        changeUndoRedoSupporter.initialize();
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
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

    public void refreshView() {
        WorkspaceUtils.saveAllEditors();
        changeStateViewer.resetState();
        resetLabelProvider();
        viewer.setInput(initializeInput());
    }

    private void makeActions() {
        refreshAction = new Action() {
            @Override
            public void run() {
                refreshView();
            }
        };
        refreshAction.setText(Messages.ErrorCentricView_refresh_text);
        refreshAction
                .setToolTipText(Messages.ErrorCentricView_refresh_tool_tip);
        refreshAction.setImageDescriptor(ImageDescriptor
                .createFromImage(Activator.getImageDescriptor(
                        Messages.ErrorCentricView_refresh_icon).createImage()));

        doubleClickAction = new Action() {
            public void run() {
                Optional<TreeObject> selectedTreeObject = getSelectedTreeObject(viewer
                        .getSelection());
                Optional<MarkerResolutionTreeNode> resolution = getSelectedMarkResolution(selectedTreeObject);
                if (resolution.isPresent()) {
                    if (changeStateViewer.isDisabled(selectedTreeObject.get())) {
                        return;
                    }
                    final MarkerResolutionTreeNode resolutionTreeNode = resolution
                            .get();
                    changeUndoRedoSupporter
                            .prepareToApplyUndoableChange(resolutionTreeNode);
                    changeUndoRedoSupporter
                            .applyUndoableChange(resolutionTreeNode);
                }
                Optional<ErrorTreeNode> error = getSelectedError(selectedTreeObject);
                if (error.isPresent()) {
                    ErrorTreeNode errorTreeNode = error.get();
                    errorTreeNode.reveal();
                }

            }

        };
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
                    Optional<MarkerResolutionTreeNode> optionalResolution = getSelectedMarkResolution(selectedTreeObject);
                    if (optionalResolution.isPresent()) {
                        MarkerResolutionTreeNode resolutionTreeNode = optionalResolution
                                .get();
                        IJavaProject javaProject = InferNullnessCommandHandler.selectedJavaProject
                                .get();
                        Fixer fixer = resolutionTreeNode.getResolution()
                                .createFixer(javaProject);
                        selectAndReveal(fixer);
                        viewer.setLabelProvider(new DisabledNodesLabelProvider(
                                new ViewLabelProvider(),
                                new FixedErrorDecorator(resolutionTreeNode),
                                changeStateViewer));

                    } else {
                        resetLabelProvider();
                    }
                }
            }

            private void selectAndReveal(Fixer fixer) {
                new CodeSnippetRevealer().reveal(fixer.getCompilationUnit(),
                        fixer.getOffset(), fixer.getLength());
            }

        });
    }

    private void resetLabelProvider() {
        viewer.setLabelProvider(new DisabledNodesLabelProvider(
                new ViewLabelProvider(), null, changeStateViewer));
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    @Override
    public void update(final TreeObject node) {
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                if (!viewer.isBusy()) {
                    viewer.refresh();
                }
            }
        });
    }

    public MarkerResolutionTreeNode getRoot() {
        return invisibleRoot;
    }
}