package checker.framework.quickfixes.descriptors;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;

import checker.framework.quickfixes.CheckerMarkerResolution;
import checker.framework.quickfixes.MarkerContext;

public class FixerProposalFactory {

    private final MarkerContext context;

    private final FixerDescriptor fixerDescriptor;

    public FixerProposalFactory(MarkerContext context,
            FixerDescriptor fixerDescriptor) {
        this.context = context;
        this.fixerDescriptor = fixerDescriptor;
    }

    public IJavaCompletionProposal createProposal() {
        return fixerDescriptor.createFixerFactory(context.getJavaProject())
                .get().getProposal();
    }

    public CheckerMarkerResolution createResolution(IMarker marker) {
        return new CheckerMarkerResolution((ICompilationUnit) context
                .getCompilationUnitNode().getJavaElement(), context
                .getProblemLocation().getOffset(), context.getProblemLocation()
                .getLength(), createProposal(), marker);
    }

}
