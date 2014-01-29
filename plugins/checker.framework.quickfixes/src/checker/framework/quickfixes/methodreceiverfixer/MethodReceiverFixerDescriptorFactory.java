package checker.framework.quickfixes.methodreceiverfixer;

import static com.google.common.collect.Sets.newHashSet;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

import checker.framework.quickfixes.ErrorKey;
import checker.framework.quickfixes.MarkerContext;
import checker.framework.quickfixes.descriptors.BindingBasedMethodDescriptor;
import checker.framework.quickfixes.descriptors.BindingBasedMethodDescriptorFactory;
import checker.framework.quickfixes.descriptors.CompilationUnitDescriptorFactory;
import checker.framework.quickfixes.descriptors.FixerDescriptorFactory;

public class MethodReceiverFixerDescriptorFactory extends
		FixerDescriptorFactory<MethodReceiverFixerDescriptor> {

	private final CompilationUnitDescriptorFactory compilationUnitDescriptorFactory = new CompilationUnitDescriptorFactory();

	private final BindingBasedMethodDescriptorFactory methodDescriptorFactory = new BindingBasedMethodDescriptorFactory();

	public MethodReceiverFixerDescriptorFactory(MarkerContext context) {
		super(context);
	}

	@Override
	protected Set<ErrorKey> getSupportedErrorKeys() {
		return newHashSet(ErrorKey.ReceiverInvalid,
				ErrorKey.OverrideReceiverInvalid,
				ErrorKey.MethodInvocationInvalid);
	}

	@Override
	public Set<MethodReceiverFixerDescriptor> doGet() {
		ASTNode selectedNode = context.getCoveringNode();
		if (selectedNode instanceof MethodInvocation) {
			ASTNode methodInvocationNode = selectedNode;
			IMethodBinding methodBinding = ((MethodInvocation) methodInvocationNode)
					.resolveMethodBinding().getMethodDeclaration();
			ICompilationUnit compilationUnit = (ICompilationUnit) methodBinding
					.getMethodDeclaration().getJavaElement()
					.getAncestor(IJavaElement.COMPILATION_UNIT);
			BindingBasedMethodDescriptor methodDescriptor = methodDescriptorFactory
					.get(methodBinding);
			return newHashSet(new MethodReceiverFixerDescriptor(
					compilationUnitDescriptorFactory.get(compilationUnit),
					methodDescriptor, context.getFoundTypeString()));
		}
		return new HashSet<>();
	}

}
