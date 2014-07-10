package checker.framework.separated.propagator.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import checker.framework.change.propagator.CheckerID;
import checker.framework.separated.view.views.Resolutions;

public class InferNullnessCommandHandler extends InferCommandHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        checkerID = CheckerID.NULLNESS.getId();
        Resolutions.clear();
        return super.execute(event);
    }

}
