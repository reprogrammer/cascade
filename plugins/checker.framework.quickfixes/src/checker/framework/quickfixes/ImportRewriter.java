package checker.framework.quickfixes;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite.ImportRewriteContext;
import org.eclipse.jdt.internal.corext.codemanipulation.ContextSensitiveImportRewriteContext;
import org.eclipse.jdt.ui.text.java.correction.ASTRewriteCorrectionProposal;

@SuppressWarnings("restriction")
public class ImportRewriter {

    private final RequiredImportStatementGuesser requiredImportStatementGuesser = new RequiredImportStatementGuesser();

    public void addRequiredImports(ASTRewriteCorrectionProposal proposal,
            CompilationUnit compilationUnit, ASTNode contextASTNode,
            String newTypeString) {
        ImportRewrite imports = proposal.createImportRewrite(compilationUnit);
        ImportRewriteContext importRewriteContext = new ContextSensitiveImportRewriteContext(
                contextASTNode, imports);
        for (String importStatement : requiredImportStatementGuesser
                .guessImportStatementsRequiredBy(newTypeString)) {
            imports.addImport(importStatement, importRewriteContext);
        }
    }

}
