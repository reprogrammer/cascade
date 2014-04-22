package checker.framework.errorcentric.view.views;

import org.eclipse.jdt.core.IJavaProject;

import checker.framework.quickfixes.FixerDescriptorApplier;
import checker.framework.quickfixes.descriptors.FixerDescriptor;
import checker.framework.quickfixes.descriptors.FixerFactory;

public class IdentityFixerDescriptor extends FixerDescriptor {

    @Override
    public FixerDescriptorApplier createFixerDescriptorApplier(
            IJavaProject javaProject) {
        return new IdentityFixerDescriptorApplier();
    }

    @Override
    public FixerFactory createFixerFactory(IJavaProject javaProject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        return 97;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

}
