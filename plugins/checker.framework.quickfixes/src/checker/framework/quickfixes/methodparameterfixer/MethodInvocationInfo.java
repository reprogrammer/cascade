package checker.framework.quickfixes.methodparameterfixer;

import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import com.google.common.base.Optional;

public class MethodInvocationInfo {

	private IMethodBinding methodBinding;

	private List arguments;

	private ASTNode selectedASTNode;

	public MethodInvocationInfo(IMethodBinding methodBinding, List arguments,
			ASTNode selectedASTNode) {
		this.methodBinding = methodBinding;
		this.arguments = arguments;
		this.selectedASTNode = selectedASTNode;
	}

	public ICompilationUnit getCompilationUnit() {
		return (ICompilationUnit) methodBinding.getJavaElement().getAncestor(
				IJavaElement.COMPILATION_UNIT);
	}

	public Optional<SingleVariableDeclaration> getSelectedParameter(
			CompilationUnit compilationUnitNode) {
		MethodDeclaration methodDeclarationNode = (MethodDeclaration) compilationUnitNode
				.findDeclaringNode(methodBinding.getKey());
		Optional<Integer> optionalSelectedArgumentPosition = getNodePositionInList(
				selectedASTNode, arguments);
		if (optionalSelectedArgumentPosition.isPresent()) {
			Object parameter = methodDeclarationNode.parameters().get(
					optionalSelectedArgumentPosition.get());
			if (parameter instanceof SingleVariableDeclaration) {
				return Optional.of((SingleVariableDeclaration) parameter);
			}
		}
		return Optional.absent();
	}

	static Optional<Integer> getNodePositionInList(ASTNode selectedNode,
			List<? extends ASTNode> arguments) {
		for (int i = 0; i < arguments.size(); ++i) {
			if (arguments.get(i).equals(selectedNode)) {
				return Optional.of(i);
			}
		}
		return Optional.absent();
	}
}
