package checker.framework.quickfixes.descriptors;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class BindingBasedMethodFactory {

    private final CompilationUnitFactory compilationUnitFactory;

    private final BindingBasedMethodDescriptor methodDescriptor;

    public BindingBasedMethodFactory(
            CompilationUnitFactory compilationUnitFactory,
            BindingBasedMethodDescriptor methodDescriptor) {
        this.compilationUnitFactory = compilationUnitFactory;
        this.methodDescriptor = methodDescriptor;
    }

    public IMethod getJavaElement() {
        throw new UnsupportedOperationException();
    }

    public MethodDeclaration getASTNode() {
        return (MethodDeclaration) compilationUnitFactory.getASTNode()
                .findDeclaringNode(methodDescriptor.getBindingKey());
    }

}
