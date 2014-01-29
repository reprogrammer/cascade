package checker.framework.quickfixes.descriptors;

import org.eclipse.jdt.core.ICompilationUnit;

public class CompilationUnitDescriptorFactory {

	public CompilationUnitDescriptor get(ICompilationUnit compilationUnit) {
		return new CompilationUnitDescriptor(compilationUnit.getPath()
				.makeRelativeTo(
						compilationUnit.getParent().getParent().getPath()));
	}

}
