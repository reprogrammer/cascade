package checker.framework.change.propagator;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;

import checker.framework.quickfixes.descriptors.CompilationUnitFactory;

import com.google.common.base.Optional;
import com.google.common.collect.Ordering;

public class MarkerLocationFactory {

    private final MarkerLocationDescriptor markerLocationDescriptor;

    public MarkerLocationFactory(MarkerLocationDescriptor markerDescriptor) {
        this.markerLocationDescriptor = markerDescriptor;
    }

    public Optional<MarkerLocation> createMarkerLocation(
            IJavaProject javaProject) {
        CompilationUnitFactory compilationUnitFactory = new CompilationUnitFactory(
                javaProject,
                markerLocationDescriptor.getCompilationUnitDescriptor());
        ICompilationUnit compilationUnit = compilationUnitFactory
                .getJavaElement();
        CompilationUnitTextExtractor cuTextExtractor = new CompilationUnitTextExtractor(
                compilationUnit);
        return findBestMatch(compilationUnit, cuTextExtractor.getText());
    }

    private Optional<MarkerLocation> findBestMatch(
            ICompilationUnit compilationUnit, String text) {
        Set<MarkerLocation> matches = new HashSet<>();
        Pattern p = Pattern.compile(Pattern.quote(markerLocationDescriptor
                .getSurroundingCodeSnippet()));
        Matcher m = p.matcher(text);
        while (m.find()) {
            matches.add(new MarkerLocation(compilationUnit, m.start(), m.end()
                    - m.start()));
        }
        if (matches.isEmpty()) {
            return Optional.absent();
        }
        MarkerLocation bestMatch = Ordering
                .natural()
                .onResultOf(
                        (MarkerLocation markerLocation) -> Math
                                .abs(markerLocationDescriptor
                                        .getSurroundingCodeSnippetOffset()
                                        - markerLocation.getOffset()))
                .min(matches);
        return Optional
                .of(new MarkerLocation(
                        compilationUnit,
                        bestMatch.getOffset()
                                + markerLocationDescriptor
                                        .getCodeSnippetOffsetRelativeToSurroundingCodeSnippet(),
                        markerLocationDescriptor.getCodeSnippetLength()));
    }

}
