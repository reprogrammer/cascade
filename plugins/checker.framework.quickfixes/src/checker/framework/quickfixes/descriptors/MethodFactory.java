package checker.framework.quickfixes.descriptors;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodFactory {

    private final CompilationUnitFactory compilationUnitFactory;

    private final MethodDescriptor methodDescriptor;

    public MethodFactory(CompilationUnitFactory compilationUnitFactory,
            MethodDescriptor methodDescriptor) {
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
