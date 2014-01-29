package checker.framework.quickfixes.methodparameterfixer;

import org.eclipse.jdt.core.IJavaProject;

import checker.framework.quickfixes.descriptors.BindingBasedMethodFactory;
import checker.framework.quickfixes.descriptors.CompilationUnitDescriptor;
import checker.framework.quickfixes.descriptors.CompilationUnitFactory;
import checker.framework.quickfixes.descriptors.Fixer;
import checker.framework.quickfixes.descriptors.FixerFactory;

@Deprecated
public class MethodParameterFixerFactory extends FixerFactory {

    private final MethodParameterFixerDescriptor descriptor;

    private final CompilationUnitFactory compilationUnitFactory;

    private final BindingBasedMethodFactory methodFactory;

    private final CompilationUnitDescriptor compilationUnitDescriptor;

    public MethodParameterFixerFactory(
            MethodParameterFixerDescriptor descriptor, IJavaProject javaProject) {
        super(javaProject);
        this.descriptor = descriptor;
        compilationUnitDescriptor = descriptor.getCompilationUnitDescriptor();
        this.compilationUnitFactory = new CompilationUnitFactory(javaProject,
                compilationUnitDescriptor);
        this.methodFactory = new BindingBasedMethodFactory(
                compilationUnitFactory, descriptor.getMethodDescriptor());
    }

    @Override
    public Fixer get() {
        return new MethodParameterFixer(compilationUnitFactory.getASTNode(),
                methodFactory.getASTNode(),
                descriptor.getSelectedArgumentPosition(),
                descriptor.getNewTypeString());
    }

}
