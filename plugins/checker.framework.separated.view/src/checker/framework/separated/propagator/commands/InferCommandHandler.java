package checker.framework.separated.propagator.commands;

import org.checkerframework.eclipse.actions.CheckerHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import checker.framework.separated.view.views.list.SeparatedErrorsView;
import checker.framework.separated.view.views.tree.SeparatedChangesView;

import com.google.common.base.Optional;

public abstract class InferCommandHandler extends CheckerHandler {

    public static String checkerID;

    public static Optional<IJavaProject> selectedJavaProject = Optional
            .absent();

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            selectedJavaProject = getSelectedProject(getSelection(event));
            if (selectedJavaProject.isPresent()) {
                // Adapted from http://stackoverflow.com/a/172082
                IWorkbenchPage activePage = PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getActivePage();
                activePage.showView(SeparatedChangesView.ID);
                activePage.showView(SeparatedErrorsView.ID);
                IViewPart findView = activePage
                        .findView(SeparatedChangesView.ID);
            }
        } catch (PartInitException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private Optional<IJavaProject> getSelectedProject(ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            return Optional
                    .of((IJavaProject) ((IStructuredSelection) selection)
                            .getFirstElement());
        } else {
            return Optional.absent();
        }
    }

}
