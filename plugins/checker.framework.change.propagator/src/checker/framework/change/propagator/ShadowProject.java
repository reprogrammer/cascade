package checker.framework.change.propagator;

import static com.google.common.collect.Iterables.getFirst;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;

import checker.framework.quickfixes.CheckerMarkerResolution;
import checker.framework.quickfixes.CheckerResolutionGenerator;
import checker.framework.quickfixes.MarkerContext;
import checker.framework.quickfixes.MarkerContextFactory;
import checker.framework.quickfixes.WorkspaceUtils;
import checker.framework.quickfixes.descriptors.FixerDescriptor;
import checker.framework.quickfixes.descriptors.FixerProposalFactory;
import checkers.eclipse.util.ResourceUtils;

import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class ShadowProject {

    private final IJavaProject shadowProject;

    private final CheckerResolutionGenerator checkerResolutionGenerator = new CheckerResolutionGenerator();

    public ShadowProject(IJavaProject shadowProject) {
        this.shadowProject = shadowProject;
    }

    public IJavaProject getProject() {
        return shadowProject;
    }

    public IJavaProject getPrimaryProject() {
        return WorkspaceUtils
                .getJavaProject(shadowProject
                        .getProject()
                        .getName()
                        .substring(
                                ShadowProjectFactory.SHADOW_PROJECT_PREFIX
                                        .length()));
    }

    public Set<String> getSourceFiles() {
        try {
            return ResourceUtils.sourceFilesOf(shadowProject);
        } catch (CoreException e) {
            throw new RuntimeException();
        }
    }

    public void runChecker(CheckerID checkerID) {
        ComputeQuickFixesJob computeQuickFixesJob = new ComputeQuickFixesJob(
                "ComputeQuickFixes of " + getProject().getElementName(), this,
                checkerID);
        computeQuickFixesJob.setUser(true);
        computeQuickFixesJob.setPriority(Job.BUILD);
        computeQuickFixesJob.schedule();
        try {
            computeQuickFixesJob.join();
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }

    private Set<ComparableMarker> toComparableMarkers(Set<IMarker> markers) {
        Set<ComparableMarker> comparableMarkers = new HashSet<>();
        for (IMarker marker : markers) {
            comparableMarkers.add(ComparableMarker.create(this, marker));
        }
        return comparableMarkers;
    }

    public Set<ComparableMarker> getMarkers() {
        return toComparableMarkers(new HashSet<>(Arrays.asList(WorkspaceUtils
                .getMarkers(shadowProject.getProject()))));
    }

    public Set<ActionableMarkerResolution> getResolutions() {
        Set<ComparableMarker> baseMarkers = getMarkers();
        return getResolutions(baseMarkers, baseMarkers);
    }

    public Set<ActionableMarkerResolution> getResolutions(
            Set<ComparableMarker> baseMarkers, Set<ComparableMarker> markers) {
        SetMultimap<FixerDescriptor, ComparableMarker> fixersMap = createFixerDescriptors(markers);
        Set<ActionableMarkerResolution> resolutions = new HashSet<>();
        for (FixerDescriptor fixerDescriptor : fixersMap.keySet()) {
            resolutions.addAll(createActionableResolutions(fixerDescriptor,
                    fixersMap.get(fixerDescriptor), baseMarkers).asSet());
        }
        return resolutions;
    }

    private Set<FixerDescriptor> createFixerDescriptor(ComparableMarker marker) {
        return checkerResolutionGenerator.getFixerDescriptors(marker
                .getMarker());
    }

    private SetMultimap<FixerDescriptor, ComparableMarker> createFixerDescriptors(
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

    private Optional<ActionableMarkerResolution> createActionableResolutions(
            FixerDescriptor fixerDescriptor, Set<ComparableMarker> markers,
            Set<ComparableMarker> baseMarkers) {
        Optional<ActionableMarkerResolution> optionalResolution = Optional
                .absent();
        ComparableMarker marker = getFirst(markers, null);
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
                    this, resolution, markers, fixerDescriptor, baseMarkers));
        }
        return optionalResolution;
    }

    public void updateToPrimaryProjectWithChanges(
            List<FixerDescriptor> fixerDescriptors) {
        WorkspaceUtils.copyResource(getPrimaryProject().getProject(),
                shadowProject.getProject());
        for (FixerDescriptor fixerDescriptor : fixerDescriptors) {
            fixerDescriptor.createFixerDescriptorApplier(getProject()).apply();
        }
    }

}
