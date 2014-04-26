package checker.framework.quickfixes.variabledeclarationfixer;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.Name;

import checker.framework.quickfixes.descriptors.CompilationUnitDescriptor;

import com.google.common.base.Function;
import com.google.common.base.Optional;

public class VariableDeclarationFixerUtils {

    static Optional<String> getNodeBindingKey(Optional<Name> receiverNode) {
        return receiverNode.transform(node -> node.resolveBinding().getKey());
    }

    static Optional<ICompilationUnit> getCompilationUnit(
            Optional<Name> receiverNode) {
        return receiverNode.transform(node -> {
            return (ICompilationUnit) node.resolveBinding().getJavaElement()
                    .getAncestor(IJavaElement.COMPILATION_UNIT);
        });
    }

    public static Optional<VariableDeclarationFixerDescriptor> createVariableDeclarationFixerDescriptor(
            String newTypeString, CompilationUnitDescriptor cuDescriptor,
            Optional<Name> receiverNode) {
        Optional<String> receiverNodeBindingKey = getNodeBindingKey(receiverNode);
        Optional<VariableDeclarationFixerDescriptor> fixer = receiverNodeBindingKey
                .transform(getBindingKeyToDescriptorFunction(cuDescriptor,
                        newTypeString));
        return fixer;
    }

    private static Function<String, VariableDeclarationFixerDescriptor> getBindingKeyToDescriptorFunction(
            final CompilationUnitDescriptor compilationUnitDescriptor,
            final String newTypeString) {
        return receiverNodeBindingKey -> new VariableDeclarationFixerDescriptor(
                compilationUnitDescriptor, newTypeString,
                new VariableDeclarationDescriptor(receiverNodeBindingKey));
    }
}
