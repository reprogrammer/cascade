package checker.framework.quickfixes.descriptors;

import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodDescriptorFactory {

	@SuppressWarnings("restriction")
	public MethodDescriptor get(MethodDeclaration methodDeclaration) {
		MethodDeclarationFlattener visitor = new MethodDeclarationFlattener();
		methodDeclaration.accept(visitor);
		return new MethodDescriptor(visitor.getResult());
	}

}
