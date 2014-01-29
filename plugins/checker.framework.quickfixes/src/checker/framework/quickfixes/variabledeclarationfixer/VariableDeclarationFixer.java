package checker.framework.quickfixes.variabledeclarationfixer;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.text.correction.IProposalRelevance;
import org.eclipse.jdt.internal.ui.text.correction.proposals.LinkedCorrectionProposal;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.swt.graphics.Image;

import checker.framework.quickfixes.ASTParsingUtils;
import checker.framework.quickfixes.ImportRewriter;
import checker.framework.quickfixes.descriptors.Fixer;

@SuppressWarnings("restriction")
public class VariableDeclarationFixer implements Fixer {

    private final CompilationUnit compilationUnit;

    private final ASTNode variableDeclarationNode;

    private final String newTypeString;

    private final ImportRewriter importRewriter = new ImportRewriter();

    public VariableDeclarationFixer(CompilationUnit compilationUnit,
            ASTNode variableDeclarationNode, String newTypeString) {
        this.compilationUnit = compilationUnit;
        this.variableDeclarationNode = variableDeclarationNode;
        this.newTypeString = newTypeString;
    }

    public int getOffset() {
        return variableDeclarationNode.getStartPosition();
    }

    public int getLength() {
        return variableDeclarationNode.getLength();
    }

    public ICompilationUnit getCompilationUnit() {
        return (ICompilationUnit) compilationUnit.getJavaElement();
    }

    private String getVariableKind(ASTNode variableDeclarationNode) {
        if (variableDeclarationNode.getParent() instanceof MethodDeclaration) {
            return "parameter";
        } else if (variableDeclarationNode.getParent() instanceof FieldDeclaration) {
            return "field";
        } else {
            return "variable";
        }
    }

    @Override
    public IJavaCompletionProposal getProposal() {
        AST ast = variableDeclarationNode.getAST();
        ASTRewrite rewrite = ASTRewrite.create(ast);
        Type foundType = ASTParsingUtils
                .parseTypeStringExcludingInternalQualifiers(newTypeString);
        String rewrittenTypeString = rewriteType(rewrite,
                variableDeclarationNode, foundType);
        TypeDeclarationASTNodeInfo typeDeclarationASTNodeInfo = computeTypeDeclarationASTNodeInfo(variableDeclarationNode);
        removeAnnotationModifiers(rewrite, typeDeclarationASTNodeInfo);
        LinkedCorrectionProposal proposal = createProposal(compilationUnit,
                rewrite, typeDeclarationASTNodeInfo.getIdentifierName(),
                rewrittenTypeString, getVariableKind(variableDeclarationNode));
        importRewriter.addRequiredImports(proposal, compilationUnit,
                variableDeclarationNode, ASTParsingUtils
                        .typeStringExcludingInternalQualifiers(newTypeString));
        return proposal;
    }

    private static LinkedCorrectionProposal createProposal(
            CompilationUnit astRoot, ASTRewrite rewrite, String identifier,
            String newType, String variableKind) {
        Image image = JavaPluginImages
                .get(JavaPluginImages.IMG_CORRECTION_CHANGE);
        LinkedCorrectionProposal proposal = new LinkedCorrectionProposal(
                String.format("Change " + variableKind + " %s to %s",
                        identifier, newType),
                (ICompilationUnit) astRoot.getJavaElement(), rewrite,
                IProposalRelevance.CHANGE_TYPE_OF_RECEIVER_NODE, image);
        return proposal;
    }

    @SuppressWarnings("unchecked")
    private static String rewriteType(ASTRewrite rewrite,
            ASTNode receiverNodeDeclaration, Type foundType) {
        if (!(receiverNodeDeclaration instanceof VariableDeclarationFragment || receiverNodeDeclaration instanceof SingleVariableDeclaration)) {
            throw new RuntimeException(
                    "expected VariableDeclarationFragment or SingleVariableDeclaration, found "
                            + receiverNodeDeclaration.getClass());
        }
        Type type = getTypeASTNode(receiverNodeDeclaration);
        if (type instanceof ArrayType) {
            if (foundType instanceof ArrayType) {
                StringBuilder newTypeString = new StringBuilder();
                ArrayType arrayType = (ArrayType) type;
                ArrayType foundArrayType = (ArrayType) foundType;
                rewrite.replace(arrayType.getElementType(),
                        foundArrayType.getElementType(), null);
                newTypeString
                        .append(foundArrayType.getElementType().toString());
                Iterator<ASTNode> typeDimensionIterator = (arrayType)
                        .dimensions().iterator();
                Iterator<ASTNode> foundTypeDimensionIterator = (foundArrayType)
                        .dimensions().iterator();
                while (foundTypeDimensionIterator.hasNext()) {
                    ASTNode nextFoundTypeDimension = foundTypeDimensionIterator
                            .next();
                    rewrite.replace(typeDimensionIterator.next(),
                            nextFoundTypeDimension, null);
                    newTypeString.append(nextFoundTypeDimension.toString());
                }
                return newTypeString.toString();
            } else {
                rewrite.replace(((ArrayType) type).getElementType(), foundType,
                        null);
                return newArrayTypeString(foundType, (ArrayType) type);
            }
        } else {
            rewrite.replace(type, foundType, null);
            return foundType.toString();
        }
    }

    private static String newArrayTypeString(Type newElementType,
            ArrayType arrayType) {
        return newElementType.toString()
                + arrayType.toString().substring(
                        arrayType.getElementType().toString().length());
    }

    private static Type getTypeASTNode(ASTNode variableDeclarationNode) {
        if (variableDeclarationNode instanceof SingleVariableDeclaration) {
            return ((SingleVariableDeclaration) variableDeclarationNode)
                    .getType();
        }
        ASTNode parent = variableDeclarationNode.getParent();
        if (parent instanceof VariableDeclarationStatement) {
            VariableDeclarationStatement statement = (VariableDeclarationStatement) parent;
            return statement.getType();
        } else if (parent instanceof FieldDeclaration) {
            FieldDeclaration statement = (FieldDeclaration) parent;
            return statement.getType();
        }
        return null;
    }

    private static void removeAnnotationModifiers(ASTRewrite rewrite,
            TypeDeclarationASTNodeInfo typeDeclarationASTNodeInfo) {
        ListRewrite listRewrite = null;
        listRewrite = rewrite.getListRewrite(
                typeDeclarationASTNodeInfo.getTypeDeclarationNode(),
                typeDeclarationASTNodeInfo.getModifiersProperty());
        for (Object modifier : typeDeclarationASTNodeInfo.getModifiers()) {
            if (modifier instanceof Annotation) {
                listRewrite.remove((Annotation) modifier, null);
            }
        }
    }

    private static TypeDeclarationASTNodeInfo computeTypeDeclarationASTNodeInfo(
            ASTNode receiverNodeDeclaration) {
        ChildListPropertyDescriptor modifiersProperty = null;
        ASTNode typeDeclarationNode = null;
        List<?> modifiers = null;
        String identifierName = null;
        if (receiverNodeDeclaration instanceof SingleVariableDeclaration) {
            typeDeclarationNode = receiverNodeDeclaration;
            modifiersProperty = SingleVariableDeclaration.MODIFIERS2_PROPERTY;
            modifiers = ((SingleVariableDeclaration) typeDeclarationNode)
                    .modifiers();
            identifierName = ((SingleVariableDeclaration) typeDeclarationNode)
                    .getName().getIdentifier();
        } else if (receiverNodeDeclaration instanceof VariableDeclarationFragment) {
            identifierName = ((VariableDeclarationFragment) receiverNodeDeclaration)
                    .getName().getIdentifier();
            typeDeclarationNode = receiverNodeDeclaration.getParent();
            if (typeDeclarationNode instanceof VariableDeclarationStatement) {
                modifiersProperty = VariableDeclarationStatement.MODIFIERS2_PROPERTY;
                modifiers = ((VariableDeclarationStatement) typeDeclarationNode)
                        .modifiers();
            } else if (typeDeclarationNode instanceof FieldDeclaration) {
                modifiersProperty = FieldDeclaration.MODIFIERS2_PROPERTY;
                modifiers = ((FieldDeclaration) typeDeclarationNode)
                        .modifiers();
            }
        }
        return new TypeDeclarationASTNodeInfo(modifiersProperty,
                typeDeclarationNode, modifiers, identifierName);
    }

}
