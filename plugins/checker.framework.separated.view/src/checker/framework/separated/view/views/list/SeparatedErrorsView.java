package checker.framework.separated.view.views.list;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
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
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import checker.framework.change.propagator.ActionableMarkerResolution;
import checker.framework.change.propagator.ComparableMarker;
import checker.framework.separated.view.views.Resolutions;
import checker.framework.separated.view.views.tree.MarkerResolutionTreeNode;
import checker.framework.separated.view.views.tree.SeparatedChangesView;

public class SeparatedErrorsView extends ViewPart implements ISelectionListener {
    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "checker.framework.separated.view.views.list.SeparatedErrorsView";

    private TableViewer viewer;

    /**
     * String representation of the previous selection;
     */
    private String prevSelection = "";

    private boolean isHighlighted;

    /**
     * The constructor.
     */
    public SeparatedErrorsView() {
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
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setInput(prepareInput());
        getSite().getPage().addSelectionListener(SeparatedChangesView.ID, this);
    }

    private List<String> prepareInput() {
        Set<String> existingMessages = new HashSet<String>();
        List<String> messages = new ArrayList<String>();
        Set<ActionableMarkerResolution> resolutions = Resolutions.get();
        for (ActionableMarkerResolution resolution : resolutions) {
            Set<ComparableMarker> markers = resolution.getAllMarkers();
            for (ComparableMarker marker : markers) {
                String message = marker.getMessage();
                if (!existingMessages.contains(message)) {
                    existingMessages.add(message);
                    messages.add(message);
                }
            }
        }
        return messages;
    }

    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        if (!(selection instanceof TreeSelection)) {
            return;
        }
        if (selection.isEmpty()) {
            return;
        }
        if (prevSelection.equals(selection.toString())) {
            return;
        }
        prevSelection = selection.toString();

        TreeSelection treeSelection = (TreeSelection) selection;

        Iterator iterator = treeSelection.iterator();
        Set<String> removedErrors = new HashSet<String>();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof MarkerResolutionTreeNode) {
                ActionableMarkerResolution resolution = ((MarkerResolutionTreeNode) next)
                        .getResolution();
                Set<ComparableMarker> markers = resolution.getMarkers();
                for (ComparableMarker marker : markers) {
                    removedErrors.add(marker.getMessage());
                }
            }
        }

        Table table = viewer.getTable();
        Color defaultColor = table.getForeground();
        Color highlightColor = new Color(null, 255, 0, 0);
        for (int i = 0; i < table.getItemCount(); ++i) {
            TableItem item = table.getItem(i);
            isHighlighted = removedErrors.contains(item.getText());
            item.setForeground(isHighlighted ? highlightColor : defaultColor);
        }
    }
}
