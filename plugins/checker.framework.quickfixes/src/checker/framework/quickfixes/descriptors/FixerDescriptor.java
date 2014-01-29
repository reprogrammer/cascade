package checker.framework.quickfixes.descriptors;

import org.eclipse.jdt.core.IJavaProject;

import checker.framework.quickfixes.FixerDescriptorApplier;
import checker.framework.quickfixes.MarkerContext;

public abstract class FixerDescriptor {

    public abstract FixerFactory createFixerFactory(IJavaProject javaProject);

    public FixerDescriptorApplier createFixerDescriptorApplier(
            IJavaProject javaProject) {
        return new FixerDescriptorApplier(createFixerFactory(javaProject));
    }

    public FixerProposalFactory createProposalFactory(MarkerContext context) {
        return new FixerProposalFactory(context, this);
    }

    public abstract int hashCode();

    public abstract boolean equals(Object obj);

}
