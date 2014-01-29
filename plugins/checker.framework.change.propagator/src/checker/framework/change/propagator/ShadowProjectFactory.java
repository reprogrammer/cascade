package checker.framework.change.propagator;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;

import checker.framework.quickfixes.WorkspaceUtils;

public class ShadowProjectFactory {

    static final String SHADOW_PROJECT_PREFIX = "ShadowOf";

    final private IJavaProject javaProject;

    public ShadowProjectFactory(IJavaProject project) {
        this.javaProject = project;
    }

    public ShadowProject get() {
        IProject shadowProject = WorkspaceUtils
                .createProject(SHADOW_PROJECT_PREFIX
                        + javaProject.getProject().getName());
        WorkspaceUtils.copyResource(javaProject.getProject(), shadowProject);
        return new ShadowProject(WorkspaceUtils.createJavaProject(shadowProject
                .getProject()));
    }

}
