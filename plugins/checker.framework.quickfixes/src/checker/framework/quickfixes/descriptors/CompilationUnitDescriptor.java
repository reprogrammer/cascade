package checker.framework.quickfixes.descriptors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

public class CompilationUnitDescriptor {

	private final IPath pathRelativeToClasspath;

	CompilationUnitDescriptor(IPath pathRelativeToClassPath) {
		this.pathRelativeToClasspath = pathRelativeToClassPath;
	}

	IPath getPathRelativeToClasspath() {
		return pathRelativeToClasspath;
	}

	public ICompilationUnit get(IJavaProject project) {
		try {
			return (ICompilationUnit) project
					.findElement(pathRelativeToClasspath);
		} catch (JavaModelException e) {
			throw new RuntimeException(e);
		}
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
