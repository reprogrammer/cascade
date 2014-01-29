package checker.framework.quickfixes.variabledeclarationfixer;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import checker.framework.quickfixes.descriptors.CompilationUnitFactory;

public class VariableDeclarationFactory {

	private final CompilationUnitFactory compilationUnitFactory;

	private final VariableDeclarationDescriptor variableDeclarationDescriptor;

	public VariableDeclarationFactory(
			CompilationUnitFactory compilationUnitFactory,
			VariableDeclarationDescriptor variableDeclarationDescriptor) {
		this.compilationUnitFactory = compilationUnitFactory;
		this.variableDeclarationDescriptor = variableDeclarationDescriptor;
	}

	// Binding keys of fields that are defined in nested class inside methods
	// have offset values and are thus not usable in different compilation
	// units.
	public ASTNode getASTNode() {
		return (VariableDeclaration) compilationUnitFactory.getASTNode()
				.findDeclaringNode(
						variableDeclarationDescriptor.getBindingKey());
	}

}
