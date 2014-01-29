package checker.framework.quickfixes.descriptors;

import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;

import checker.framework.quickfixes.CheckerMarkerResolution;

public class FixerResolutionFactory {

    private final Fixer fixer;

    public FixerResolutionFactory(Fixer fixer) {
        this.fixer = fixer;
    }

    public CheckerMarkerResolution get() {
        return new CheckerMarkerResolution(fixer.getCompilationUnit(),
                fixer.getOffset(), fixer.getLength(),
                (IJavaCompletionProposal) fixer.getProposal(), null);
    }

}
