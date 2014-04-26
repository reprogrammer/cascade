package checker.framework.quickfixes.methodreceiverfixer;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.text.correction.IProposalRelevance;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.correction.ASTRewriteCorrectionProposal;
import org.eclipse.swt.graphics.Image;

import checker.framework.quickfixes.ASTParsingUtils;
import checker.framework.quickfixes.ImportRewriter;
import checker.framework.quickfixes.WorkspaceUtils;
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
        // TODO(reprogrammer): Externalize the following string.
        String label = String.format("Change receiver parameter of %s to %s",
                methodDeclarationNode.getName().getIdentifier(),
                ASTParsingUtils
                        .typeStringExcludingInternalQualifiers(newTypeString));
        Image image = WorkspaceUtils
                .loadProposalImage(JavaPluginImages.IMG_CORRECTION_CHANGE);
        ASTRewriteCorrectionProposal proposal = new ASTRewriteCorrectionProposal(
                label, getCompilationUnit(), rewrite,
                IProposalRelevance.CHANGE_METHOD_SIGNATURE, image);
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
