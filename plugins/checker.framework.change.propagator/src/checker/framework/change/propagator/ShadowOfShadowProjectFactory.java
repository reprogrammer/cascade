package checker.framework.change.propagator;

import org.eclipse.jdt.core.IJavaProject;

public class ShadowOfShadowProjectFactory extends ShadowProjectFactory {

    public ShadowOfShadowProjectFactory(IJavaProject javaProject) {
        super(javaProject);
    }

    public ShadowOfShadowProject get() {
        return new ShadowOfShadowProject(createShadowJavaProject());
    }

}
