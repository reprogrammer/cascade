package checker.framework.change.propagator;

import static com.google.common.collect.Iterables.getFirst;

import java.util.HashSet;
import java.util.Set;

import checker.framework.quickfixes.CheckerMarkerResolution;
import checker.framework.quickfixes.CheckerResolutionGenerator;
import checker.framework.quickfixes.MarkerContext;
import checker.framework.quickfixes.MarkerContextFactory;
import checker.framework.quickfixes.descriptors.FixerDescriptor;
import checker.framework.quickfixes.descriptors.FixerProposalFactory;

import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class ResolutionHelper {
    private static final CheckerResolutionGenerator checkerResolutionGenerator = new CheckerResolutionGenerator();

    /**
     * Returns a set of resolutions for markers that have a resolution. Each
     * returned resolution will have a reference to baseMarkers.
     * 
     * @param shadowProject
     * @param baseMarkers
     * @param markers
     * @return
     */
    public static Set<ActionableMarkerResolution> getResolutions(
            ShadowOfShadowProject shadowProject, Set<ComparableMarker> baseMarkers,
            Set<ComparableMarker> markers) {
        SetMultimap<FixerDescriptor, ComparableMarker> fixersMap = createFixerDescriptors(markers);
        Set<ActionableMarkerResolution> resolutions = new HashSet<>();
        for (FixerDescriptor fixerDescriptor : fixersMap.keySet()) {
            resolutions.addAll(createActionableResolutions(shadowProject,
                    fixerDescriptor, fixersMap.get(fixerDescriptor),
                    baseMarkers).asSet());
        }
        return resolutions;
    }

    public static Set<FixerDescriptor> createFixerDescriptor(
            ComparableMarker marker) {
        return checkerResolutionGenerator.getFixerDescriptors(marker
                .getMarker());
    }

    public static SetMultimap<FixerDescriptor, ComparableMarker> createFixerDescriptors(
            Set<ComparableMarker> markers) {
        HashMultimap<FixerDescriptor, ComparableMarker> multimap = HashMultimap
                .create();
        for (ComparableMarker marker : markers) {
            Set<FixerDescriptor> fixerDescriptors = createFixerDescriptor(marker);
            for (FixerDescriptor fixerDescriptor : fixerDescriptors) {
                multimap.put(fixerDescriptor, marker);
            }
        }
        return multimap;
    }

    public static Optional<ActionableMarkerResolution> createActionableResolutions(
            ShadowOfShadowProject shadowProject, FixerDescriptor fixerDescriptor,
            Set<ComparableMarker> markersToBeResolvedByFixer,
            Set<ComparableMarker> baseMarkers) {
        Optional<ActionableMarkerResolution> optionalResolution = Optional
                .absent();
        ComparableMarker marker = getFirst(markersToBeResolvedByFixer, null);
        if (marker == null) {
            return optionalResolution;
        }
        MarkerContextFactory factory = new MarkerContextFactory(
                marker.getMarker());
        Optional<MarkerContext> optionalContext = factory.get();
        if (optionalContext.isPresent()) {
            FixerProposalFactory proposalFactory = fixerDescriptor
                    .createProposalFactory(optionalContext.get());
            CheckerMarkerResolution resolution = proposalFactory
                    .createResolution(marker.getMarker());
            optionalResolution = Optional.of(new ActionableMarkerResolution(
                    shadowProject, resolution, markersToBeResolvedByFixer,
                    fixerDescriptor, baseMarkers));
        }
        return optionalResolution;
    }

}
