package checker.framework.quickfixes;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.internal.ui.text.correction.CorrectionMarkerResolutionGenerator.CorrectionMarkerResolution;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;

@SuppressWarnings("restriction")
public class CheckerMarkerResolution extends CorrectionMarkerResolution {

	/**
	 * Duplicate of the private field
	 * {@code org.eclipse.jdt.internal.ui.text.correction.CorrectionMarkerResolutionGenerator.CorrectionMarkerResolution.fProposal}
	 * .
	 */
	private final IJavaCompletionProposal proposal;

	public CheckerMarkerResolution(ICompilationUnit cu, int offset, int length,
			IJavaCompletionProposal proposal, IMarker marker) {
		super(cu, offset, length, proposal, marker);
		this.proposal = proposal;
	}

	public IJavaCompletionProposal getProposal() {
		return proposal;
	}

}
