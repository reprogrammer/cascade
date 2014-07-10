package checker.framework.errorcentric.propagator.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.InputDialog;

import checker.framework.change.propagator.CheckerID;

public class InferNullnessCommandHandler extends InferCommandHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        checkerID = CheckerID.NULLNESS;
        InputDialog dialog = new InputDialog(
                null,
                "Enter type checker",
                "Enter the fully qualified name of the type checker you want to use (e.g., org.checkerframework.checker.nullness.NullnessChecker",
                "org.checkerframework.checker.nullness.NullnessChecker", null);
        int open = dialog.open();
        if (open == InputDialog.OK) {
            System.out.println(dialog.getValue());
        } else {
            // what should we do?
        }
        return super.execute(event);
    }

}
