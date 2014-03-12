package checker.framework.separated.view.views.list;

import static com.google.common.collect.Sets.newHashSet;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import checker.framework.change.propagator.ActionableMarkerResolution;
import checker.framework.change.propagator.ComparableMarker;
import checker.framework.change.propagator.MarkerLocation;
import checker.framework.separated.view.views.CodeSnippetRevealer;
import checker.framework.separated.view.views.Colors;
import checker.framework.separated.view.views.Resolutions;
import checker.framework.separated.view.views.Views;
import checker.framework.separated.view.views.tree.MarkerResolutionTreeNode;
import checker.framework.separated.view.views.tree.SeparatedChangesView;

import com.google.common.base.Optional;

public class SeparatedErrorsView extends ViewPart implements ISelectionListener {
    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "checker.framework.separated.view.views.list.SeparatedErrorsView";

    private TableViewer viewer;

    /**
     * String representation of the previous selection in the changes view;
     */
    private TreeSelection prevSelection;

    private Set<ComparableMarker> input;

    private Set<ComparableMarker> removedMarkers = newHashSet();

    private Action doubleClickAction;

    /**
     * The constructor.
     */
    public SeparatedErrorsView() {
    }

    /**
     * This is a callback during the initialization phase.
     */
    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        Views.setErrorsView(this);
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    @Override
    public void createPartControl(Composite parent) {
        viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
                | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

        final TableViewerColumn viewerColumn = new TableViewerColumn(viewer,
                SWT.NONE);
        final TableColumn column = viewerColumn.getColumn();
        column.setText("column1");
        column.setWidth(100);
        column.setResizable(true);
        column.setMoveable(true);
        viewerColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return element.toString();
            }
        });
        final Table table = viewer.getTable();
        table.setLinesVisible(true);
        viewer.setLabelProvider(new ListLabelProvider());
        viewer.setContentProvider(new ListContentProvider());
        viewer.setInput(prepareInput());

        getSite().setSelectionProvider(viewer);
        getSite().getPage().addSelectionListener(SeparatedChangesView.ID, this);

        createAndHookDoubleClickAction();
    }

    private void createAndHookDoubleClickAction() {
        doubleClickAction = new Action() {
            public void run() {
                StructuredSelection selection = (StructuredSelection) viewer
                        .getSelection();
                if (selection != null) {
                    ComparableMarker marker = (ComparableMarker) selection
                            .getFirstElement();
                    Optional<MarkerLocation> optionalMarkerLocation = marker
                            .createMarkerLocation();
                    if (optionalMarkerLocation.isPresent()) {
                        MarkerLocation markerLocation = optionalMarkerLocation
                                .get();
                        new CodeSnippetRevealer().reveal(
                                markerLocation.getCompilationUnit(),
                                markerLocation.getOffset(),
                                markerLocation.getLength());
                    }
                }
            }
        };
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                doubleClickAction.run();
            }
        });
    }

    public void markAsFixed(Set<ComparableMarker> removedErrors) {
        removedMarkers.addAll(removedErrors);
    }

    private Set<ComparableMarker> prepareInput() {
        input = new HashSet<ComparableMarker>();
        Set<ActionableMarkerResolution> resolutions = Resolutions.get();
        if (resolutions != null) {
            for (ActionableMarkerResolution resolution : resolutions) {
                // FIXME this seems to be overwriting the old input every time
                input = resolution.getAllMarkersBeforeResolution();
            }
        }
        return input;
    }

    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        TreeSelection treeSelection = (TreeSelection) selection;
        if (!isNewNonEmptyNonNull(treeSelection)) {
            return;
        }
        prevSelection = treeSelection;
        rebuildErrorsList(selection);
    }

    private boolean isNewNonEmptyNonNull(TreeSelection selection) {
        return selection != null && !selection.isEmpty()
                && (prevSelection == null || !selection.equals(prevSelection));
    }

    public void rebuildErrorsList(ISelection selection) {
        Set<ComparableMarker> markersOnlyBeforeResolution = new HashSet<ComparableMarker>();
        Set<ComparableMarker> markersOnlyAfterResolution = new HashSet<ComparableMarker>();
        Set<ComparableMarker> markersBeforeAndAfterResolution = new HashSet<ComparableMarker>();
        getMarkersForSelection((TreeSelection) selection,
                markersOnlyBeforeResolution, markersOnlyAfterResolution,
                markersBeforeAndAfterResolution);
        Set<ComparableMarker> allErrors = new HashSet<ComparableMarker>();
        allErrors.addAll(markersOnlyBeforeResolution);
        allErrors.addAll(markersOnlyAfterResolution);
        allErrors.addAll(markersBeforeAndAfterResolution);
        Table table = viewer.getTable();
        table.setRedraw(false);
        viewer.setInput(allErrors);
        highlightErrors(markersOnlyBeforeResolution, markersOnlyAfterResolution);
        table.setRedraw(true);
    }

    private void getMarkersForSelection(TreeSelection treeSelection,
            Set<ComparableMarker> markersOnlyBeforeResolution,
            Set<ComparableMarker> markersOnlyAfterResolution,
            Set<ComparableMarker> markersBeforeAndAfterResolution) {
        Iterator iterator = treeSelection.iterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof MarkerResolutionTreeNode) {
                MarkerResolutionTreeNode markerTreeNode = (MarkerResolutionTreeNode) next;
                markersOnlyBeforeResolution.addAll(markerTreeNode
                        .getAllMarkersOnlyBeforeResolution());
                markersOnlyAfterResolution.addAll(markerTreeNode
                        .getAllMarkersOnlyAfterResolution());
                markersBeforeAndAfterResolution.addAll(markerTreeNode
                        .getAllMarkersBeforeAndAfterResolution());
            }
        }
    }

    private void highlightErrors(Set<ComparableMarker> toBeRemovedMarkers,
            Set<ComparableMarker> toBeAddedMarkers) {
        Table table = viewer.getTable();
        Color defaultColor = table.getForeground();
        for (int i = 0; i < table.getItemCount(); ++i) {
            TableItem item = table.getItem(i);
            ComparableMarker marker = (ComparableMarker) item.getData();
            if (removedMarkers.contains(marker)) {
                highlightRemovedItem(item);
            } else if (toBeRemovedMarkers.contains(marker)) {
                highlightToBeRemovedItem(item);
            } else if (toBeAddedMarkers.contains(marker)) {
                highlightToBeAddedItem(item);
            } else {
                unhighlightItem(item, defaultColor);
            }
        }
    }

    private void highlightRemovedItem(TableItem item) {
        item.setForeground(Colors.GRAY);
    }

    private void highlightToBeRemovedItem(TableItem item) {
        item.setForeground(Colors.GREEN);
    }

    private void highlightToBeAddedItem(TableItem item) {
        item.setForeground(Colors.RED);
    }

    private void unhighlightItem(TableItem item, Color defaultColor) {
        item.setForeground(defaultColor);
    }
}
