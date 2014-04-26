package checker.framework.quickfixes.variabledeclarationfixer;

import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.union;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Name;

import checker.framework.quickfixes.ErrorKey;
import checker.framework.quickfixes.Flags;
import checker.framework.quickfixes.MarkerContext;
import checker.framework.quickfixes.descriptors.CompilationUnitDescriptor;
import checker.framework.quickfixes.descriptors.CompilationUnitDescriptorFactory;
import checker.framework.quickfixes.descriptors.DescriptorUtils;
import checker.framework.quickfixes.descriptors.FixerDescriptorFactory;
import checker.framework.quickfixes.descriptors.Side;

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
        Optional<VariableDeclarationFixerDescriptor> leftHandSideFixer = createFixer(
                Side.LEFT, selectedNode, context.getFoundTypeString());
        Optional<VariableDeclarationFixerDescriptor> rightHandSideFixer = Optional
                .absent();
        if (Flags.propagateQualifiersFromLeftToRight) {
            rightHandSideFixer = createFixer(Side.RIGHT, selectedNode,
                    context.getRequiredTypeString());
        }
        return union(leftHandSideFixer.asSet(), rightHandSideFixer.asSet());
    }

    private Optional<VariableDeclarationFixerDescriptor> createFixer(Side side,
            ASTNode selectedNode, String newTypeString) {
        Optional<VariableDeclarationFixerDescriptor> fixer = Optional.absent();
        if (newTypeString != null) {
            Optional<Expression> node = DescriptorUtils.findSideNode(
                    selectedNode, side);
            Optional<Name> identifier = DescriptorUtils.extractIdentifier(node);
            Optional<ICompilationUnit> optionalCompilationUnit = VariableDeclarationFixerUtils
                    .getCompilationUnit(identifier);
            if (optionalCompilationUnit.isPresent()) {
                CompilationUnitDescriptor cuDescriptor = compilationUnitDescriptorFactory
                        .get(optionalCompilationUnit.get());
                fixer = VariableDeclarationFixerUtils
                        .createVariableDeclarationFixerDescriptor(
                                newTypeString, cuDescriptor, identifier);
            }
        }
        return fixer;
    }

}
