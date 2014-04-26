package checker.framework.quickfixes.methodreturnfixer;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.jdt.core.IJavaProject;

import checker.framework.quickfixes.descriptors.BindingBasedMethodDescriptor;
import checker.framework.quickfixes.descriptors.CompilationUnitDescriptor;
import checker.framework.quickfixes.descriptors.FixerDescriptor;
import checker.framework.quickfixes.descriptors.FixerFactory;

public class MethodReturnFixerDescriptor extends FixerDescriptor {

    private final BindingBasedMethodDescriptor methodDescriptor;

    private final CompilationUnitDescriptor compilationUnitDescriptor;

    private final String newReturnTypeString;

    public MethodReturnFixerDescriptor(
            BindingBasedMethodDescriptor methodDescriptor,
            CompilationUnitDescriptor compilationUnitDescriptor,
            String newReturnTypeString) {
        this.methodDescriptor = methodDescriptor;
        this.compilationUnitDescriptor = compilationUnitDescriptor;
        this.newReturnTypeString = newReturnTypeString;
    }

    BindingBasedMethodDescriptor getMethodDescriptor() {
        return methodDescriptor;
    }

    CompilationUnitDescriptor getCompilationUnitDescriptor() {
        return compilationUnitDescriptor;
    }

    String getNewReturnTypeString() {
        return newReturnTypeString;
    }

    @Override
    public FixerFactory createFixerFactory(IJavaProject javaProject) {
        return new MethodReturnFixerFactory(this, javaProject);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

}
