package checker.framework.errorcentric.propagator.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.google.common.base.Optional;

import checker.framework.change.propagator.CheckerID;
import checker.framework.errorcentric.view.views.ChangesView;
import checkers.eclipse.actions.CheckerHandler;

public abstract class InferCommandHandler extends CheckerHandler {

    public static CheckerID checkerID;

    public static Optional<IJavaProject> selectedJavaProject = Optional
            .absent();

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            selectedJavaProject = getSelectedProject(getSelection(event));
            if (selectedJavaProject.isPresent()) {
                // Adapted from http://stackoverflow.com/a/172082
                PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getActivePage().showView(ChangesView.ID);
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
