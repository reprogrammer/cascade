package checker.framework.quickfixes.descriptors;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;

public interface Fixer {

    ICompilationUnit getCompilationUnit();

    int getOffset();

    int getLength();

    IJavaCompletionProposal getProposal();

}