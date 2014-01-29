package checker.framework.quickfixes.methodparameterfixer;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.jdt.core.IJavaProject;

import checker.framework.quickfixes.descriptors.BindingBasedMethodDescriptor;
import checker.framework.quickfixes.descriptors.CompilationUnitDescriptor;
import checker.framework.quickfixes.descriptors.FixerDescriptor;
import checker.framework.quickfixes.descriptors.FixerFactory;

@Deprecated
public class MethodParameterFixerDescriptor extends FixerDescriptor {

	private final CompilationUnitDescriptor compilationUnitDescriptor;

	private final BindingBasedMethodDescriptor methodDescriptor;

	private final int selectedArgumentPosition;

	private final String newTypeString;

	public MethodParameterFixerDescriptor(
			CompilationUnitDescriptor compilationUnitDescriptor,
			BindingBasedMethodDescriptor methodDescriptor,
			int selectedArgumentPosition, String newTypeString) {
		this.compilationUnitDescriptor = compilationUnitDescriptor;
		this.methodDescriptor = methodDescriptor;
		this.selectedArgumentPosition = selectedArgumentPosition;
		this.newTypeString = newTypeString;
	}

	@Override
	public FixerFactory createFixerFactory(IJavaProject javaProject) {
		return new MethodParameterFixerFactory(this, javaProject);
	}

	CompilationUnitDescriptor getCompilationUnitDescriptor() {
		return compilationUnitDescriptor;
	}

	BindingBasedMethodDescriptor getMethodDescriptor() {
		return methodDescriptor;
	}

	int getSelectedArgumentPosition() {
		return selectedArgumentPosition;
	}

	String getNewTypeString() {
		return newTypeString;
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
		return "Change parameter " + selectedArgumentPosition + " of "
				+ methodDescriptor.toString() + " to " + newTypeString;
	}

}
