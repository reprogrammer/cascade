package checker.framework.quickfixes.methodreceiverfixer;

import org.eclipse.jdt.core.IJavaProject;

import checker.framework.quickfixes.descriptors.BindingBasedMethodFactory;
import checker.framework.quickfixes.descriptors.CompilationUnitDescriptor;
import checker.framework.quickfixes.descriptors.CompilationUnitFactory;
import checker.framework.quickfixes.descriptors.Fixer;
import checker.framework.quickfixes.descriptors.FixerFactory;

public class MethodReceiverFixerFactory extends FixerFactory {

    private final MethodReceiverFixerDescriptor descriptor;

    private final CompilationUnitFactory compilationUnitFactory;

    private final BindingBasedMethodFactory methodFactory;

    private final CompilationUnitDescriptor compilationUnitDescriptor;

    public MethodReceiverFixerFactory(MethodReceiverFixerDescriptor descriptor,
            IJavaProject javaProject) {
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
        return new MethodReceiverFixer(compilationUnitFactory.getASTNode(),
                methodFactory.getASTNode(), descriptor.getNewTypeString());
    }

}
