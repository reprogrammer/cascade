package checker.framework.quickfixes.descriptors;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import com.google.common.base.Optional;

public class DescriptorUtils {

    public static Optional<Name> extractIdentifier(
            Optional<Expression> expression) {
        if (expression.isPresent()) {
            return extractIdentifier(expression.get());
        } else {
            return Optional.absent();
        }
    }

    public static Optional<Name> extractIdentifier(Expression expression) {
        Name identifier = null;
        if (expression instanceof Name) {
            identifier = (Name) expression;
        } else if (expression instanceof FieldAccess) {
            identifier = ((FieldAccess) expression).getName();
        } else if (expression instanceof ArrayAccess) {
            return extractIdentifier(((ArrayAccess) expression).getArray());
        }
        return Optional.fromNullable(identifier);
    }

    public static Optional<Expression> findLeftHandSideNode(ASTNode selectedNode) {
        ASTNode parentNode = selectedNode.getParent();
        if (parentNode instanceof Assignment) {
            Assignment assign = (Assignment) selectedNode.getParent();
            Expression leftHandSide = assign.getLeftHandSide();
            return Optional.of(leftHandSide);
        } else if (parentNode instanceof VariableDeclarationFragment) {
            VariableDeclarationFragment frag = (VariableDeclarationFragment) selectedNode
                    .getParent();
            if (selectedNode.equals(frag.getName())
                    || selectedNode.equals(frag.getInitializer())) {
                return Optional.of((Expression) frag.getName());
            }
        }
        return Optional.absent();
    }

    public static Optional<Expression> findRightHandSideNode(
            ASTNode selectedNode) {
        ASTNode parentNode = selectedNode.getParent();
        if (parentNode instanceof Assignment) {
            Assignment assign = (Assignment) selectedNode.getParent();
            Expression rightHandSide = assign.getRightHandSide();
            return Optional.of(rightHandSide);
        } else if (parentNode instanceof VariableDeclarationFragment) {
            VariableDeclarationFragment frag = (VariableDeclarationFragment) selectedNode
                    .getParent();
            if (selectedNode.equals(frag.getName())
                    || selectedNode.equals(frag.getInitializer())) {
                return Optional.of(frag.getInitializer());
            }
        }
        return Optional.absent();
    }

}
