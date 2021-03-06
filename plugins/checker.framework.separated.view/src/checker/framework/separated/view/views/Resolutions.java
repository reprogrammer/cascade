package checker.framework.separated.view.views;

import java.util.Set;

import org.eclipse.jdt.core.IJavaProject;

import checker.framework.change.propagator.ActionableMarkerResolution;
import checker.framework.change.propagator.ShadowOfShadowProject;
import checker.framework.change.propagator.ShadowOfShadowProjectFactory;
import checker.framework.change.propagator.ShadowProject;
import checker.framework.change.propagator.ShadowProjectFactory;
import checker.framework.separated.propagator.commands.InferCommandHandler;

public class Resolutions {
    private static Set<ActionableMarkerResolution> resolutions;

    public static void clear() {
        resolutions = null;
    }

    public static Set<ActionableMarkerResolution> get() {
        if (InferCommandHandler.checkerID == null) {
            return null;
        }
        if (!InferCommandHandler.selectedJavaProject.isPresent()) {
            return null;
        }
        if (resolutions == null) {
            IJavaProject javaProject = InferCommandHandler.selectedJavaProject
                    .get();
            ShadowProject shadowProject = new ShadowProjectFactory(javaProject)
                    .get();
            ShadowOfShadowProject shadowOfShadowProject = new ShadowOfShadowProjectFactory(
                    shadowProject.getProject()).get();
            shadowOfShadowProject.runChecker(InferCommandHandler.checkerID);
            resolutions = shadowOfShadowProject.getResolutions();
        }
        return resolutions;
    }
}
