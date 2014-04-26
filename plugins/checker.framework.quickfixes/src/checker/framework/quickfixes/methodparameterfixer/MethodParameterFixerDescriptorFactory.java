package checker.framework.quickfixes.methodparameterfixer;

import static com.google.common.collect.Sets.newHashSet;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;

import checker.framework.quickfixes.ErrorKey;
import checker.framework.quickfixes.Flags;
import checker.framework.quickfixes.MarkerContext;
import checker.framework.quickfixes.descriptors.CompilationUnitDescriptor;
import checker.framework.quickfixes.descriptors.CompilationUnitDescriptorFactory;
import checker.framework.quickfixes.descriptors.CompilationUnitFactory;
import checker.framework.quickfixes.descriptors.DescriptorUtils;
import checker.framework.quickfixes.descriptors.FixerDescriptorFactory;
import checker.framework.quickfixes.variabledeclarationfixer.VariableDeclarationDescriptor;
import checker.framework.quickfixes.variabledeclarationfixer.VariableDeclarationFixerDescriptor;
import checker.framework.quickfixes.variabledeclarationfixer.VariableDeclarationFixerUtils;

import com.google.common.base.Optional;

public class MethodParameterFixerDescriptorFactory extends
        FixerDescriptorFactory<VariableDeclarationFixerDescriptor> {

    private final CompilationUnitDescriptorFactory compilationUnitDescriptorFactory = new CompilationUnitDescriptorFactory();

    public MethodParameterFixerDescriptorFactory(MarkerContext context) {
        super(context);
    }

    @Override
    protected Set<ErrorKey> getSupportedErrorKeys() {
        return newHashSet(ErrorKey.ArgumentTypeIncompatible,
                ErrorKey.OverrideParamInvalid, ErrorKey.ReturnTypeIncompatible);
    }

    private Optional<MethodInvocationInfo> extractMethodInvocationInfo(
            ASTNode selectedNode) {
        MethodInvocationInfo info = null;
        if (selectedNode instanceof Expression) {
            ASTNode invocationNode = selectedNode.getParent();
            if (invocationNode instanceof MethodInvocation) {
                MethodInvocation methodInvocation = (MethodInvocation) invocationNode;
                info = new MethodInvocationInfo(methodInvocation
                        .resolveMethodBinding().getMethodDeclaration(),
                        methodInvocation.arguments(), selectedNode);
            } else if (invocationNode instanceof ClassInstanceCreation) {
                ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) invocationNode;
                IMethodBinding methodBinding = classInstanceCreation
                        .resolveConstructorBinding().getMethodDeclaration();
                info = new MethodInvocationInfo(methodBinding,
                        classInstanceCreation.arguments(), selectedNode);
            } else if (invocationNode instanceof SuperMethodInvocation) {
                SuperMethodInvocation superInvocation = (SuperMethodInvocation) invocationNode;
                IMethodBinding methodBinding = superInvocation
                        .resolveMethodBinding().getMethodDeclaration();
                info = new MethodInvocationInfo(methodBinding,
                        superInvocation.arguments(), selectedNode);
            } else if (invocationNode instanceof SuperConstructorInvocation) {
                SuperConstructorInvocation superInvocation = (SuperConstructorInvocation) invocationNode;
                IMethodBinding methodBinding = superInvocation
                        .resolveConstructorBinding().getMethodDeclaration();
                info = new MethodInvocationInfo(methodBinding,
                        superInvocation.arguments(), selectedNode);
            } else if (invocationNode instanceof ConstructorInvocation) {
                ConstructorInvocation superInvocation = (ConstructorInvocation) invocationNode;
                IMethodBinding methodBinding = superInvocation
                        .resolveConstructorBinding().getMethodDeclaration();
                info = new MethodInvocationInfo(methodBinding,
                        superInvocation.arguments(), selectedNode);
            }
        }
        return Optional.fromNullable(info);
    }

    // Adapted from
    // org.eclipse.jdt.internal.ui.text.correction.UnresolvedElementsSubProcessor.getMethodProposals(IInvocationContext,
    // IProblemLocation, boolean, Collection<ICommandAccess>)
    @Override
    public Set<VariableDeclarationFixerDescriptor> doGet() {
        ASTNode selectedNode = context.getCoveringNode();
        ICompilationUnit cu = context.getCompilationUnit();
        if (selectedNode instanceof Expression) {
            Set<VariableDeclarationFixerDescriptor> descriptors = new HashSet<>();
            if (Flags.propagateQualifiersFromLeftToRight) {
                Optional<Name> identifier = DescriptorUtils
                        .extractIdentifier((Expression) selectedNode);
                CompilationUnitDescriptor cuDescriptor = compilationUnitDescriptorFactory
                        .get(cu);
                Optional<VariableDeclarationFixerDescriptor> variableDeclarationFixerDescriptor = VariableDeclarationFixerUtils
                        .createVariableDeclarationFixerDescriptor(
                                context.getRequiredTypeString(), cuDescriptor,
                                identifier);
                descriptors.addAll(variableDeclarationFixerDescriptor.asSet());
            }
            Optional<MethodInvocationInfo> methodInvocationInfo = extractMethodInvocationInfo(selectedNode);
            if (methodInvocationInfo.isPresent()) {
                descriptors.addAll(createFixerDescriptors(methodInvocationInfo
                        .get()));
            }
            return descriptors;
        } else if (selectedNode instanceof SingleVariableDeclaration) {
            CompilationUnitDescriptor cuDescriptor = compilationUnitDescriptorFactory
                    .get(cu);
            VariableDeclarationDescriptor variableDeclarationDescriptor = new VariableDeclarationDescriptor(
                    ((SingleVariableDeclaration) selectedNode).resolveBinding()
                            .getKey());
            return newHashSet(new VariableDeclarationFixerDescriptor(
                    cuDescriptor, context.getRequiredTypeString(),
                    variableDeclarationDescriptor));
        }
        return new HashSet<>();
    }

    private Set<VariableDeclarationFixerDescriptor> createFixerDescriptors(
            MethodInvocationInfo methodInvocationInfo) {
        ICompilationUnit cu = methodInvocationInfo.getCompilationUnit();
        if (cu == null) {
            // cu would be null if its source code is not in the project.
            return new HashSet<>();
        }
        CompilationUnitDescriptor cuDescriptor = compilationUnitDescriptorFactory
                .get(cu);
        CompilationUnitFactory cuFactory = new CompilationUnitFactory(
                context.getJavaProject(), cuDescriptor);
        CompilationUnit compilationUnitNode = cuFactory.getASTNode();
        Optional<SingleVariableDeclaration> selectedParameter = methodInvocationInfo
                .getSelectedParameter(compilationUnitNode);
        if (selectedParameter.isPresent()) {
            String parameterBindingKey = selectedParameter.get()
                    .resolveBinding().getKey();
            return newHashSet(new VariableDeclarationFixerDescriptor(
                    cuDescriptor, context.getFoundTypeString(),
                    new VariableDeclarationDescriptor(parameterBindingKey)));
        }
        return new HashSet<>();
    }

}
