package checker.framework.quickfixes.variabledeclarationfixer;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import checker.framework.quickfixes.descriptors.CompilationUnitFactory;
import checker.framework.quickfixes.descriptors.FixerFactory;

public class VariableDeclarationFixerFactory extends FixerFactory {

	private final VariableDeclarationFixerDescriptor descriptor;

	public VariableDeclarationFixerFactory(
			VariableDeclarationFixerDescriptor descriptor,
			IJavaProject javaProject) {
		super(javaProject);
		this.descriptor = descriptor;
	}

	@Override
	public VariableDeclarationFixer get() {
		CompilationUnitFactory compilationUnitFactory = new CompilationUnitFactory(
				javaProject, descriptor.getCompilationUnitDescriptor());
		CompilationUnit compilationUnitNode = compilationUnitFactory
				.getASTNode();
		ASTNode variableDeclarationNode = new VariableDeclarationFactory(
				compilationUnitFactory,
				descriptor.getVariableDeclarationDescriptor()).getASTNode();
		return new VariableDeclarationFixer(compilationUnitNode,
				variableDeclarationNode, descriptor.getNewTypeString());
	}

}
