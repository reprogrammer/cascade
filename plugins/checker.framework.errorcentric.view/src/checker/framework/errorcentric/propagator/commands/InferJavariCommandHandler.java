package checker.framework.errorcentric.propagator.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import checker.framework.change.propagator.CheckerID;

public class InferJavariCommandHandler extends InferCommandHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        checkerID = CheckerID.JAVARI;
        return super.execute(event);
    }

}
