package checker.framework.quickfixes.variabledeclarationfixer;

import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.union;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;

import checker.framework.quickfixes.ErrorKey;
import checker.framework.quickfixes.Flags;
import checker.framework.quickfixes.MarkerContext;
import checker.framework.quickfixes.descriptors.CompilationUnitDescriptor;
import checker.framework.quickfixes.descriptors.CompilationUnitDescriptorFactory;
import checker.framework.quickfixes.descriptors.DescriptorUtils;
import checker.framework.quickfixes.descriptors.FixerDescriptorFactory;

import com.google.common.base.Optional;

public class VariableDeclarationFixerDescriptorFactory extends
        FixerDescriptorFactory<VariableDeclarationFixerDescriptor> {

    private final CompilationUnitDescriptorFactory compilationUnitDescriptorFactory = new CompilationUnitDescriptorFactory();

    public VariableDeclarationFixerDescriptorFactory(MarkerContext context) {
        super(context);
    }

    @Override
    protected Set<ErrorKey> getSupportedErrorKeys() {
        return newHashSet(ErrorKey.AssignmentTypeIncompatible,
                ErrorKey.CompoundAssignmentTypeIncompatible);
    }

    @Override
    public Set<VariableDeclarationFixerDescriptor> doGet() {
        ASTNode selectedNode = context.getCoveredNode();
        if (!(selectedNode instanceof Expression)) {
            return new HashSet<>();
        }
        ICompilationUnit compilationUnit = getCompilationUnit(selectedNode);
        Optional<VariableDeclarationFixerDescriptor> leftHandSideFixer = Optional
                .absent();
        Optional<VariableDeclarationFixerDescriptor> rightHandSideFixer = Optional
                .absent();
        CompilationUnitDescriptor cuDescriptor = compilationUnitDescriptorFactory
                .get(compilationUnit);
        if (context.getFoundTypeString() != null) {
            Optional<Expression> leftHandSideNode = DescriptorUtils
                    .findLeftHandSideNode(selectedNode);
            leftHandSideFixer = VariableDeclarationFixerUtils
                    .createVariableDeclarationFixerDescriptor(
                            context.getFoundTypeString(), cuDescriptor,
                            DescriptorUtils.extractIdentifier(leftHandSideNode));
        }
        if (Flags.propagateQualifiersFromLeftToRight) {
            if (context.getRequiredTypeString() != null) {
                Optional<Expression> rightHandSideNode = DescriptorUtils
                        .findRightHandSideNode(selectedNode);
                rightHandSideFixer = VariableDeclarationFixerUtils
                        .createVariableDeclarationFixerDescriptor(context
                                .getRequiredTypeString(), cuDescriptor,
                                DescriptorUtils
                                        .extractIdentifier(rightHandSideNode));
            }
        }
        return union(leftHandSideFixer.asSet(), rightHandSideFixer.asSet());
    }

    private static ICompilationUnit getCompilationUnit(ASTNode astNode) {
        return (ICompilationUnit) ((CompilationUnit) astNode.getRoot())
                .getTypeRoot();
    }

}
