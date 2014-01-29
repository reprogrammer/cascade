package checker.framework.quickfixes.methodparameterfixer;

import static com.google.common.collect.Sets.newHashSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.internal.corext.dom.ScopeAnalyzer;
import org.eclipse.jdt.internal.ui.text.correction.ASTResolving;

import checker.framework.quickfixes.ErrorKey;
import checker.framework.quickfixes.Flags;
import checker.framework.quickfixes.MarkerContext;
import checker.framework.quickfixes.descriptors.BindingBasedMethodDescriptorFactory;
import checker.framework.quickfixes.descriptors.CompilationUnitDescriptor;
import checker.framework.quickfixes.descriptors.CompilationUnitDescriptorFactory;
import checker.framework.quickfixes.descriptors.CompilationUnitFactory;
import checker.framework.quickfixes.descriptors.DescriptorUtils;
import checker.framework.quickfixes.descriptors.FixerDescriptor;
import checker.framework.quickfixes.descriptors.FixerDescriptorFactory;
import checker.framework.quickfixes.variabledeclarationfixer.VariableDeclarationDescriptor;
import checker.framework.quickfixes.variabledeclarationfixer.VariableDeclarationFixerDescriptor;
import checker.framework.quickfixes.variabledeclarationfixer.VariableDeclarationFixerUtils;

import com.google.common.base.Function;
import com.google.common.base.Optional;

@SuppressWarnings("restriction")
public class MethodParameterFixerDescriptorFactory extends
        FixerDescriptorFactory<VariableDeclarationFixerDescriptor> {

    private final CompilationUnitDescriptorFactory compilationUnitDescriptorFactory = new CompilationUnitDescriptorFactory();

    private final BindingBasedMethodDescriptorFactory methodDescriptorFactory = new BindingBasedMethodDescriptorFactory();

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

    @Deprecated
    private Set<FixerDescriptor> createFixerDescriptorForSingleVariableDeclaration(
            ASTNode selectedNode) {
        final MethodDeclaration methodDeclaration = (MethodDeclaration) selectedNode
                .getParent();
        @SuppressWarnings("unchecked")
        Optional<Integer> optionalSelectedArgumentPosition = MethodInvocationInfo
                .getNodePositionInList(selectedNode,
                        methodDeclaration.parameters());
        CompilationUnit compilationUnitNode = ASTResolving
                .findParentCompilationUnit(methodDeclaration);
        final ICompilationUnit compilationUnit = (ICompilationUnit) compilationUnitNode
                .getJavaElement();
        return optionalSelectedArgumentPosition.transform(
                new Function<Integer, FixerDescriptor>() {
                    @Override
                    public FixerDescriptor apply(
                            Integer selectedArgumentPosition) {
                        return new MethodParameterFixerDescriptor(
                                compilationUnitDescriptorFactory
                                        .get(compilationUnit),
                                methodDescriptorFactory.get(methodDeclaration
                                        .resolveBinding()),
                                selectedArgumentPosition, context
                                        .getRequiredTypeString());
                    }
                }).asSet();
    }

    @Deprecated
    private Set<FixerDescriptor> createFixerDescriptorForMethodInvocation(
            ASTNode selectedNode, ASTNode invocationNode,
            MethodInvocation methodInvocation) {
        List<Expression> arguments = methodInvocation.arguments();
        Optional<Integer> optionalSelectedArgumentPosition = MethodInvocationInfo
                .getNodePositionInList(selectedNode, arguments);
        if (optionalSelectedArgumentPosition.isPresent()) {
            SimpleName methodNameNode = ((MethodInvocation) invocationNode)
                    .getName();
            ArrayList<IMethodBinding> matchingMethods = getMatchingMethodBindings(methodNameNode);
            return addParameterMissmatchProposals(context, matchingMethods,
                    invocationNode, arguments,
                    optionalSelectedArgumentPosition.get());
        }
        return new HashSet<>();
    }

    @Deprecated
    private ArrayList<IMethodBinding> getMatchingMethodBindings(
            SimpleName methodNameNode) {
        String methodName = methodNameNode.getIdentifier();
        // TODO(reprogrammer): Does the following call return only similar
        // methods that are declared in the same compilation unit? How can we
        // get the methods declared elsewhere?
        IBinding[] bindings = (new ScopeAnalyzer(
                context.getCompilationUnitNode())).getDeclarationsInScope(
                methodNameNode, ScopeAnalyzer.METHODS);
        ArrayList<IMethodBinding> matchingMethods = new ArrayList<>();
        for (int i = 0; i < bindings.length; i++) {
            IMethodBinding binding = (IMethodBinding) bindings[i];
            if (binding.getName().equals(methodName)) {
                matchingMethods.add(binding);
            }
        }
        return matchingMethods;
    }

    @Deprecated
    private Set<FixerDescriptor> addParameterMissmatchProposals(
            MarkerContext context, List<IMethodBinding> similarElements,
            ASTNode invocationNode, List<Expression> arguments,
            int selectedArgumentPosition) {
        int nSimilarElements = similarElements.size();
        if (nSimilarElements == 0) {
            return new HashSet<>();
        }
        for (int i = 0; i < nSimilarElements; i++) {
            IMethodBinding elem = similarElements.get(i);
            int diff = elem.getParameterTypes().length - arguments.size();
            if (diff == 0) {
                Optional<FixerDescriptor> descriptor = doEqualNumberOfParameters(
                        context, invocationNode, arguments,
                        selectedArgumentPosition, elem);
                return descriptor.asSet();
            }
        }
        return new HashSet<>();
    }

    @Deprecated
    private Optional<FixerDescriptor> doEqualNumberOfParameters(
            MarkerContext context, ASTNode invocationNode,
            List<Expression> arguments, int selectedArgumentPosition,
            IMethodBinding methodBinding) {
        ITypeBinding declaringTypeDecl = methodBinding.getDeclaringClass()
                .getTypeDeclaration();

        CompilationUnit astRoot = context.getCompilationUnitNode();

        ASTNode nameNode = context.getProblemLocation()
                .getCoveringNode(astRoot);
        if (nameNode == null) {
            return Optional.absent();
        }

        if (declaringTypeDecl.isFromSource()) {
            ICompilationUnit targetCU = findCompilationUnitForTypeBinding(
                    declaringTypeDecl, astRoot);

            if (targetCU != null) {
                // TODO(reprogrammer): Remove the following cast.
                return Optional
                        .of((FixerDescriptor) new MethodParameterFixerDescriptor(
                                compilationUnitDescriptorFactory.get(targetCU),
                                methodDescriptorFactory.get(methodBinding),
                                selectedArgumentPosition, context
                                        .getFoundTypeString()));
            }
        }
        return Optional.absent();
    }

    @Deprecated
    private ICompilationUnit findCompilationUnitForTypeBinding(
            ITypeBinding declaringTypeDecl, CompilationUnit astRoot) {
        try {
            return ASTResolving.findCompilationUnitForBinding(
                    (ICompilationUnit) astRoot.getJavaElement(), astRoot,
                    declaringTypeDecl);
        } catch (JavaModelException e) {
            throw new RuntimeException(e);
        }
    }

}
