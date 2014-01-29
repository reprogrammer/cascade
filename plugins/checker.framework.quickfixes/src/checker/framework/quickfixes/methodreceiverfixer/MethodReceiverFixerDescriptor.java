package checker.framework.quickfixes.methodreceiverfixer;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.jdt.core.IJavaProject;

import checker.framework.quickfixes.descriptors.BindingBasedMethodDescriptor;
import checker.framework.quickfixes.descriptors.CompilationUnitDescriptor;
import checker.framework.quickfixes.descriptors.FixerDescriptor;
import checker.framework.quickfixes.descriptors.FixerFactory;

public class MethodReceiverFixerDescriptor extends FixerDescriptor {

    private final CompilationUnitDescriptor compilationUnitDescriptor;

    private final BindingBasedMethodDescriptor methodDescriptor;

    private final String newTypeString;

    public MethodReceiverFixerDescriptor(
            CompilationUnitDescriptor compilationUnitDescriptor,
            BindingBasedMethodDescriptor methodDescriptor, String newTypeString) {
        this.compilationUnitDescriptor = compilationUnitDescriptor;
        this.methodDescriptor = methodDescriptor;
        this.newTypeString = newTypeString;
    }

    @Override
    public FixerFactory createFixerFactory(IJavaProject javaProject) {
        return new MethodReceiverFixerFactory(this, javaProject);
    }

    CompilationUnitDescriptor getCompilationUnitDescriptor() {
        return compilationUnitDescriptor;
    }

    BindingBasedMethodDescriptor getMethodDescriptor() {
        return methodDescriptor;
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

}
