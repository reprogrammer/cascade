package checker.framework.quickfixes.methodreturnfixer;

import static com.google.common.collect.Sets.newHashSet;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.internal.ui.text.correction.ASTResolving;

import checker.framework.quickfixes.ErrorKey;
import checker.framework.quickfixes.Flags;
import checker.framework.quickfixes.MarkerContext;
import checker.framework.quickfixes.descriptors.BindingBasedMethodDescriptorFactory;
import checker.framework.quickfixes.descriptors.CompilationUnitDescriptorFactory;
import checker.framework.quickfixes.descriptors.DescriptorUtils;
import checker.framework.quickfixes.descriptors.FixerDescriptorFactory;

import com.google.common.base.Optional;

@SuppressWarnings("restriction")
public class MethodReturnFixerDescriptorFactory extends
        FixerDescriptorFactory<MethodReturnFixerDescriptor> {

    private final CompilationUnitDescriptorFactory compilationUnitDescriptorFactory = new CompilationUnitDescriptorFactory();

    private final BindingBasedMethodDescriptorFactory methodDescriptorFactory = new BindingBasedMethodDescriptorFactory();

    public MethodReturnFixerDescriptorFactory(MarkerContext context) {
        super(context);
    }

    @Override
    protected Set<ErrorKey> getSupportedErrorKeys() {
        return newHashSet(ErrorKey.ArgumentTypeIncompatible,
                ErrorKey.OverrideParamInvalid, ErrorKey.ReturnTypeIncompatible,
                ErrorKey.OverrideReturnInvalid,
                ErrorKey.AssignmentTypeIncompatible,
                ErrorKey.CompoundAssignmentTypeIncompatible);
    }

    @Override
    public Set<MethodReturnFixerDescriptor> doGet() {
        CompilationUnit cu = context.getCompilationUnitNode();
        ASTNode selectedNode = context.getCoveredNode();
        Set<MethodReturnFixerDescriptor> fixerDescriptors = new HashSet<>();
        if (selectedNode instanceof Expression) {
            ASTNode parentNode = selectedNode.getParent();
            if (parentNode instanceof ReturnStatement) {
                BodyDeclaration bodyDeclaration = ASTResolving
                        .findParentBodyDeclaration(selectedNode);
                if (bodyDeclaration instanceof MethodDeclaration) {
                    MethodDeclaration methodDeclaration = (MethodDeclaration) bodyDeclaration;
                    fixerDescriptors
                            .add(new MethodReturnFixerDescriptor(
                                    methodDescriptorFactory
                                            .get(methodDeclaration
                                                    .resolveBinding()
                                                    .getMethodDeclaration()),
                                    compilationUnitDescriptorFactory
                                            .get((ICompilationUnit) cu
                                                    .getJavaElement()), context
                                            .getFoundTypeString()));
                }
                if (Flags.propagateQualifiersFromLeftToRight) {
                    if (selectedNode instanceof MethodInvocation
                            && context.getRequiredTypeString() != null) {
                        fixerDescriptors.addAll(getFixForInvokedMethod(
                                (MethodInvocation) selectedNode).asSet());
                    }
                }
            } else if (selectedNode instanceof MethodInvocation
                    && parentNode instanceof MethodInvocation
                    && context.getRequiredTypeString() != null) {
                // This case is for nested method invocations, e.g. m1(m2())
                if (Flags.propagateQualifiersFromLeftToRight) {
                    fixerDescriptors.addAll(getFixForInvokedMethod(
                            (MethodInvocation) selectedNode).asSet());
                }
            }
        }
        if (Flags.propagateQualifiersFromLeftToRight) {
            if (context.getRequiredTypeString() != null) {
                Optional<Expression> optionalRightHandSideNode = DescriptorUtils
                        .findRightHandSideNode(selectedNode);
                if (optionalRightHandSideNode.isPresent()) {
                    Expression rightHandSideNode = optionalRightHandSideNode
                            .get();
                    if (rightHandSideNode instanceof MethodInvocation) {
                        fixerDescriptors.addAll(getFixForInvokedMethod(
                                (MethodInvocation) rightHandSideNode).asSet());
                    }
                }
            }
        }

        return fixerDescriptors;
    }

    private Optional<MethodReturnFixerDescriptor> getFixForInvokedMethod(
            MethodInvocation methodInvocationNode) {
        IMethodBinding methodDeclaration = ((MethodInvocation) methodInvocationNode)
                .resolveMethodBinding().getMethodDeclaration();
        ICompilationUnit methodCU = (ICompilationUnit) methodDeclaration
                .getJavaElement().getAncestor(IJavaElement.COMPILATION_UNIT);
        if (methodCU != null) {
            return Optional.of(new MethodReturnFixerDescriptor(
                    methodDescriptorFactory.get(methodDeclaration),
                    compilationUnitDescriptorFactory.get(methodCU), context
                            .getRequiredTypeString()));
        } else {
            return Optional.absent();
        }
    }
}
