package checker.framework.quickfixes.descriptors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import com.google.common.base.Optional;

public class MethodDeclarationFinder extends ASTVisitor {

    private final MethodDescriptor targetMethodDescriptor;

    private Optional<MethodDeclaration> matchingMethodDeclaration = Optional
            .absent();

    public MethodDeclarationFinder(MethodDescriptor targetMethodDescriptor) {
        this.targetMethodDescriptor = targetMethodDescriptor;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        if (new MethodDescriptorFactory().get(node).equals(
                targetMethodDescriptor)) {
            matchingMethodDeclaration = Optional.of(node);
        }
        return true;
    }

    public MethodDeclaration getResult() {
        return matchingMethodDeclaration.get();
    }

}
