package checker.framework.change.propagator;

import java.util.Set;

import org.checkerframework.eclipse.util.ResourceUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;

import checker.framework.quickfixes.WorkspaceUtils;

public class ShadowProject {

    protected final IJavaProject project;

    public ShadowProject(IJavaProject shadowProject) {
        this.project = shadowProject;
    }

    public IJavaProject getProject() {
        return project;
    }

    public IJavaProject getPrimaryProject() {
        return WorkspaceUtils
                .getJavaProject(project
                        .getProject()
                        .getName()
                        .substring(
                                ShadowProjectFactory.SHADOW_PROJECT_PREFIX
                                        .length()));
    }

    public Set<String> getSourceFiles() {
        try {
            return ResourceUtils.sourceFilesOf(project);
        } catch (CoreException e) {
            throw new RuntimeException();
        }
    }

    public void updateToPrimaryProject() {
        WorkspaceUtils.copyResource(getPrimaryProject().getProject(),
                project.getProject());

    }

}
