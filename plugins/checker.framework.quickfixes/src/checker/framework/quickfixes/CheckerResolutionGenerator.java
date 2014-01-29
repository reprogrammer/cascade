package checker.framework.quickfixes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.text.java.CompletionProposalComparator;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;

import checker.framework.quickfixes.descriptors.FixerDescriptor;

import com.google.common.base.Optional;

/**
 * 
 * @see org.eclipse.jdt.internal.ui.text.correction.CorrectionMarkerResolutionGenerator
 * 
 */
@SuppressWarnings("restriction")
public class CheckerResolutionGenerator implements IMarkerResolutionGenerator2 {

	private static final IMarkerResolution[] NO_RESOLUTIONS = new IMarkerResolution[0];

	@Override
	public boolean hasResolutions(IMarker marker) {
		// TODO(reprogrammer): What's a better implementation?
		return true;
	}

	public IMarkerResolution[] getResolutions(IMarker marker) {
		Optional<MarkerContext> optionalMarkerContext = new MarkerContextFactory(
				marker).get();
		if (optionalMarkerContext.isPresent()) {
			ArrayList<IJavaCompletionProposal> proposals = new ArrayList<>();
			MarkerContext context = optionalMarkerContext.get();
			CheckerCorrectionProcessor.collectCorrections(context, proposals);
			Collections.sort(proposals, new CompletionProposalComparator());
			int nProposals = proposals.size();
			IMarkerResolution[] resolutions = new IMarkerResolution[nProposals];
			for (int i = 0; i < nProposals; i++) {
				resolutions[i] = new CheckerMarkerResolution(
						(ICompilationUnit) context.getCompilationUnitNode()
								.getJavaElement(), context.getProblemLocation()
								.getOffset(), context.getProblemLocation()
								.getLength(), proposals.get(i), marker);
			}
			return resolutions;
		}
		return NO_RESOLUTIONS;
	}

	public Set<FixerDescriptor> getFixerDescriptors(IMarker marker) {
		Set<FixerDescriptor> fixerDescriptors = new HashSet<>();
		Optional<MarkerContext> optionalMarkerContext = new MarkerContextFactory(
				marker).get();
		if (optionalMarkerContext.isPresent()) {
			CheckerCorrectionProcessor.collectFixerDescriptors(
					optionalMarkerContext.get(), fixerDescriptors);
		}
		return fixerDescriptors;
	}

}
