package checker.framework.quickfixes.descriptors;

import org.eclipse.jdt.core.dom.IMethodBinding;

public class BindingBasedMethodDescriptorFactory {
	
	public BindingBasedMethodDescriptor get(IMethodBinding methodBinding) {
		return new BindingBasedMethodDescriptor(methodBinding.getKey());
	}

}
