package checker.framework.quickfixes.descriptors;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;

import checker.framework.quickfixes.ASTParsingUtils;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public class CompilationUnitFactory {

	private final IJavaProject javaProject;

	private final CompilationUnitDescriptor descriptor;

	private final Supplier<ICompilationUnit> javaElementSupplier = Suppliers
			.memoize(new Supplier<ICompilationUnit>() {
				@Override
				public ICompilationUnit get() {
					try {
						return (ICompilationUnit) javaProject
								.findElement(descriptor
										.getPathRelativeToClasspath());
					} catch (JavaModelException e) {
						throw new RuntimeException(e);
					}
				}
			});

	private final Supplier<CompilationUnit> astNodeSupplier = Suppliers
			.memoize(new Supplier<CompilationUnit>() {
				@Override
				public CompilationUnit get() {
					return ASTParsingUtils.parse(getJavaElement());
				}
			});

	public CompilationUnitFactory(IJavaProject javaProject,
			CompilationUnitDescriptor compilationUnitDescriptor) {
		this.javaProject = javaProject;
		this.descriptor = compilationUnitDescriptor;
	}

	public ICompilationUnit getJavaElement() {
		return javaElementSupplier.get();
	}

	public CompilationUnit getASTNode() {
		return astNodeSupplier.get();
	}

}
