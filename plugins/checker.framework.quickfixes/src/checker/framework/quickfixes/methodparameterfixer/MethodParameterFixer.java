package checker.framework.quickfixes.methodparameterfixer;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.internal.ui.text.correction.IProposalRelevance;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.correction.ASTRewriteCorrectionProposal;

import checker.framework.quickfixes.ASTParsingUtils;
import checker.framework.quickfixes.ImportRewriter;
import checker.framework.quickfixes.descriptors.Fixer;

@Deprecated
@SuppressWarnings("restriction")
public class MethodParameterFixer implements Fixer {

	private final CompilationUnit compilationUnit;

	private final MethodDeclaration methodDeclarationNode;

	private int selectedArgumentPosition;

	private final String newTypeString;

	private final ImportRewriter importRewriter = new ImportRewriter();

	public MethodParameterFixer(CompilationUnit compilationUnit,
			MethodDeclaration methodDeclNode, int selectedArgumentPosition,
			String newTypeString) {
		this.compilationUnit = compilationUnit;
		this.methodDeclarationNode = methodDeclNode;
		this.selectedArgumentPosition = selectedArgumentPosition;
		this.newTypeString = newTypeString;
	}

	@Override
	public ICompilationUnit getCompilationUnit() {
		return (ICompilationUnit) compilationUnit.getJavaElement();
	}

	@Override
	public int getOffset() {
		return getVariableDeclaration().getStartPosition();
	}

	@Override
	public int getLength() {
		return getVariableDeclaration().getLength();
	}

	@Override
	public IJavaCompletionProposal getProposal() {
		ASTRewrite rewrite = ASTRewrite.create(compilationUnit.getAST());
		String label = String.format("Change parameter %s of %s to %s",
				getVariableDeclaration().getName().getIdentifier(),
				methodDeclarationNode.getName().getIdentifier(),
				ASTParsingUtils
						.typeStringExcludingInternalQualifiers(newTypeString));
		ASTRewriteCorrectionProposal proposal = new ASTRewriteCorrectionProposal(
				label, getCompilationUnit(), rewrite,
				IProposalRelevance.CHANGE_METHOD_SIGNATURE);
		SingleVariableDeclaration selectedParameterDeclaration = getVariableDeclaration();
		rewrite.replace(selectedParameterDeclaration.getType(), ASTParsingUtils
				.parseTypeStringExcludingInternalQualifiers(newTypeString),
				null);
		importRewriter.addRequiredImports(proposal, compilationUnit,
				methodDeclarationNode, newTypeString);
		return proposal;
	}

	private SingleVariableDeclaration getVariableDeclaration() {
		SingleVariableDeclaration selectedParameterDeclaration = (SingleVariableDeclaration) methodDeclarationNode
				.parameters().get(selectedArgumentPosition);
		return selectedParameterDeclaration;
	}

}
