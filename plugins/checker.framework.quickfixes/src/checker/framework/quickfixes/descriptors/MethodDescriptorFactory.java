package checker.framework.quickfixes.descriptors;

import org.eclipse.jdt.core.dom.IMethodBinding;

public class MethodDescriptorFactory {

    public MethodDescriptor get(IMethodBinding methodBinding) {
        return new MethodDescriptor(methodBinding.getKey());
    }

}
