package checker.framework.change.propagator.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import checker.framework.change.propagator.CheckerID;

public class InferNullnessCommandHandler extends InferCommandHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        checkerID = CheckerID.NULLNESS.getId();
        return super.execute(event);
    }

}
