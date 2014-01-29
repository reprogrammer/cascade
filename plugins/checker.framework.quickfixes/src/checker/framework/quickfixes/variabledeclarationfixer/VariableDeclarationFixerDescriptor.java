package checker.framework.quickfixes.variabledeclarationfixer;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.jdt.core.IJavaProject;

import checker.framework.quickfixes.descriptors.CompilationUnitDescriptor;
import checker.framework.quickfixes.descriptors.FixerDescriptor;
import checker.framework.quickfixes.descriptors.FixerFactory;

public class VariableDeclarationFixerDescriptor extends FixerDescriptor {

	private final CompilationUnitDescriptor compilationUnitDescriptor;

	private final String newTypeString;

	private final VariableDeclarationDescriptor variableDeclarationDescriptor;

	public VariableDeclarationFixerDescriptor(
			CompilationUnitDescriptor compilationUnitDescriptor,
			String newTypeString,
			VariableDeclarationDescriptor variableDeclarationDescriptor) {
		this.compilationUnitDescriptor = compilationUnitDescriptor;
		this.newTypeString = newTypeString;
		this.variableDeclarationDescriptor = variableDeclarationDescriptor;
	}

	CompilationUnitDescriptor getCompilationUnitDescriptor() {
		return compilationUnitDescriptor;
	}

	String getNewTypeString() {
		return newTypeString;
	}

	VariableDeclarationDescriptor getVariableDeclarationDescriptor() {
		return variableDeclarationDescriptor;
	}

	@Override
	public FixerFactory createFixerFactory(IJavaProject javaProject) {
		return new VariableDeclarationFixerFactory(this, javaProject);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return "Change type of " + variableDeclarationDescriptor.toString()
				+ " to " + newTypeString;
	}

}
