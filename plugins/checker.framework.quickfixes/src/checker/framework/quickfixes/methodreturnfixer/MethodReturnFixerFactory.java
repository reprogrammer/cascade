package checker.framework.quickfixes.methodreturnfixer;

import org.eclipse.jdt.core.IJavaProject;

import checker.framework.quickfixes.descriptors.BindingBasedMethodFactory;
import checker.framework.quickfixes.descriptors.CompilationUnitDescriptor;
import checker.framework.quickfixes.descriptors.CompilationUnitFactory;
import checker.framework.quickfixes.descriptors.FixerFactory;

public class MethodReturnFixerFactory extends FixerFactory {

    private final MethodReturnFixerDescriptor descriptor;

    private CompilationUnitDescriptor compilationUnitDescriptor;

    private CompilationUnitFactory compilationUnitFactory;

    private BindingBasedMethodFactory methodFactory;

    public MethodReturnFixerFactory(MethodReturnFixerDescriptor descriptor,
            IJavaProject javaProject) {
        super(javaProject);
        this.descriptor = descriptor;
        this.compilationUnitDescriptor = descriptor
                .getCompilationUnitDescriptor();
        this.compilationUnitFactory = new CompilationUnitFactory(javaProject,
                compilationUnitDescriptor);
        this.methodFactory = new BindingBasedMethodFactory(
                compilationUnitFactory, descriptor.getMethodDescriptor());
    }

    @Override
    public MethodReturnFixer get() {
        return new MethodReturnFixer(compilationUnitFactory.getASTNode(),
                methodFactory.getASTNode(), descriptor.getNewReturnTypeString());
    }

}
