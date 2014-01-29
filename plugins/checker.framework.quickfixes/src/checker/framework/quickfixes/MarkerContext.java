package checker.framework.quickfixes;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IProblemLocation;

public class MarkerContext {

	private final IInvocationContext invocationContext;

	private final IProblemLocation problemLocation;

	private final ErrorKey errorKey;

	public MarkerContext(IInvocationContext invocationContext,
			IProblemLocation problemLocation, ErrorKey errorKey) {
		this.invocationContext = invocationContext;
		this.problemLocation = problemLocation;
		this.errorKey = errorKey;
	}

	public IProblemLocation getProblemLocation() {
		return problemLocation;
	}

	public CompilationUnit getCompilationUnitNode() {
		return invocationContext.getASTRoot();
	}

	public ICompilationUnit getCompilationUnit() {
		return invocationContext.getCompilationUnit();
	}

	public ASTNode getCoveredNode() {
		return problemLocation.getCoveredNode(getCompilationUnitNode());
	}

	public ASTNode getCoveringNode() {
		return problemLocation.getCoveringNode(getCompilationUnitNode());
	}

	public IJavaProject getJavaProject() {
		return invocationContext.getCompilationUnit().getJavaProject();
	}

	public String getFoundTypeString() {
		String foundTypeString = problemLocation.getProblemArguments()[0];
		if (foundTypeString.equals("null")) {
			foundTypeString = getRequiredTypeString().replaceFirst("NonNull",
					"Nullable");
		}
		return foundTypeString;
	}

	public String getRequiredTypeString() {
		return problemLocation.getProblemArguments()[1];
	}

	public ErrorKey getErrorKey() {
		return errorKey;
	}

}
