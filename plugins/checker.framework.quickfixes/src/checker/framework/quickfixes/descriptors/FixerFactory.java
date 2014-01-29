package checker.framework.quickfixes.descriptors;

import org.eclipse.jdt.core.IJavaProject;

public abstract class FixerFactory {

    protected final IJavaProject javaProject;

    protected FixerFactory(IJavaProject javaProject) {
        this.javaProject = javaProject;
    }

    public abstract Fixer get();

}
