package checker.framework.quickfixes.methodreturnfixer;

import org.eclipse.jdt.core.IJavaProject;

import checker.framework.quickfixes.descriptors.BindingBasedMethodFactory;
import checker.framework.quickfixes.descriptors.CompilationUnitDescriptor;
import checker.framework.quickfixes.descriptors.CompilationUnitFactory;
import checker.framework.quickfixes.descriptors.FixerFactory;
import checker.framework.quickfixes.descriptors.MethodFactory;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public class MethodReturnFixerFactory extends FixerFactory {

	private final MethodReturnFixerDescriptor descriptor;

	private final Supplier<CompilationUnitFactory> compilationUnitFactorySupplier = Suppliers
			.memoize(new Supplier<CompilationUnitFactory>() {
				@Override
				public CompilationUnitFactory get() {
					return new CompilationUnitFactory(javaProject, descriptor
							.getCompilationUnitDescriptor());
				}
			});

	private final Supplier<MethodFactory> methodFactorySupplier = Suppliers
			.memoize(new Supplier<MethodFactory>() {
				@Override
				public MethodFactory get() {
					return new MethodFactory(compilationUnitFactorySupplier
							.get(), descriptor.getOldMethodDescriptor());
				}
			});

	private CompilationUnitDescriptor compilationUnitDescriptor;

	private CompilationUnitFactory compilationUnitFactory;

	private BindingBasedMethodFactory methodFactory;

	public MethodReturnFixerFactory(MethodReturnFixerDescriptor descriptor,
			IJavaProject javaProject) {
		super(javaProject);
		this.descriptor = descriptor;
		this.compilationUnitDescriptor = descriptor
				.getCompilationUnitDescriptor();
		this.compilationUnitFactory = new CompilationUnitFactory(javaProject,
				compilationUnitDescriptor);
		this.methodFactory = new BindingBasedMethodFactory(
				compilationUnitFactory, descriptor.getMethodDescriptor());
	}

	@Override
	public MethodReturnFixer get() {
		return new MethodReturnFixer(compilationUnitFactory.getASTNode(),
				methodFactory.getASTNode(), descriptor.getNewReturnTypeString());
	}

	@Deprecated
	public MethodReturnFixer oldGet() {
		return new MethodReturnFixer(compilationUnitFactorySupplier.get()
				.getASTNode(), methodFactorySupplier.get().getASTNode(),
				descriptor.getNewReturnTypeString());
	}

}
