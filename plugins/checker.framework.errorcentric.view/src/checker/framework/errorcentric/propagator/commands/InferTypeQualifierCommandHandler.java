package checker.framework.errorcentric.propagator.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.InputDialog;

public class InferTypeQualifierCommandHandler extends InferCommandHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        InputDialog dialog = new InputDialog(
                null,
                "Enter type checker",
                "Enter the fully qualified name of the type checker you want to use. For example, org.checkerframework.checker.nullness.NullnessChecker.",
                null, null);
        if (dialog.open() == InputDialog.OK) {
            checkerID = dialog.getValue();
        } else {
            return null;
        }
        return super.execute(event);
    }

}
