package checker.framework.quickfixes.methodreceiverfixer;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.internal.ui.text.correction.IProposalRelevance;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.correction.ASTRewriteCorrectionProposal;

import checker.framework.quickfixes.ASTParsingUtils;
import checker.framework.quickfixes.ImportRewriter;
import checker.framework.quickfixes.descriptors.Fixer;

@SuppressWarnings("restriction")
public class MethodReceiverFixer implements Fixer {

    private final CompilationUnit compilationUnit;

    private final MethodDeclaration methodDeclarationNode;

    private final String newTypeString;

    private final ImportRewriter importRewriter = new ImportRewriter();

    public MethodReceiverFixer(CompilationUnit compilationUnit,
            MethodDeclaration methodDeclNode, String newTypeString) {
        this.compilationUnit = compilationUnit;
        this.methodDeclarationNode = methodDeclNode;
        this.newTypeString = newTypeString;
    }

    @Override
    public ICompilationUnit getCompilationUnit() {
        return (ICompilationUnit) compilationUnit.getJavaElement();
    }

    @Override
    public int getOffset() {
        return methodDeclarationNode.getName().getStartPosition();
    }

    @Override
    public int getLength() {
        return methodDeclarationNode.getName().getLength();
    }

    @Override
    public IJavaCompletionProposal getProposal() {
        ASTRewrite rewrite = ASTRewrite.create(compilationUnit.getAST());
        String label = String.format("Change receiver parameter of %s to %s",
                methodDeclarationNode.getName().getIdentifier(),
                ASTParsingUtils
                        .typeStringExcludingInternalQualifiers(newTypeString));
        ASTRewriteCorrectionProposal proposal = new ASTRewriteCorrectionProposal(
                label, getCompilationUnit(), rewrite,
                IProposalRelevance.CHANGE_METHOD_SIGNATURE);
        rewrite.set(
                methodDeclarationNode,
                MethodDeclaration.RECEIVER_TYPE_PROPERTY,
                ASTParsingUtils
                        .parseTypeStringExcludingInternalQualifiers(newTypeString),
                null);
        importRewriter.addRequiredImports(proposal, compilationUnit,
                methodDeclarationNode, newTypeString);
        return proposal;
    }

}
