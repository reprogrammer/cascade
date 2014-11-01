package checker.framework.change.propagator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;

import checker.framework.quickfixes.WorkspaceUtils;
import checker.framework.quickfixes.descriptors.FixerDescriptor;

public class ShadowOfShadowProject extends ShadowProject {

    public ShadowOfShadowProject(IJavaProject project) {
        super(project);
    }

    public void runChecker(String checkerID) {
        ComputeQuickFixesJob computeQuickFixesJob = new ComputeQuickFixesJob(
                "ComputeQuickFixes of " + getProject().getElementName(), this,
                checkerID);
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
                .getMarkers(project.getProject()))));
    }

    public Set<ActionableMarkerResolution> getResolutions() {
        Set<ComparableMarker> baseMarkers = getMarkers();
        return getResolutions(baseMarkers, baseMarkers);
    }

    public Set<ActionableMarkerResolution> getResolutions(
            Set<ComparableMarker> baseMarkers, Set<ComparableMarker> markers) {
        return ResolutionHelper.getResolutions(this, baseMarkers, markers);
    }

    public void updateToPrimaryProjectWithChanges(
            List<FixerDescriptor> fixerDescriptors) {
        WorkspaceUtils.copyResource(getPrimaryProject().getProject(),
                project.getProject());
        for (FixerDescriptor fixerDescriptor : fixerDescriptors) {
            fixerDescriptor.createFixerDescriptorApplier(getProject()).apply();
        }
    }

    public ShadowProject getShadowProject() {
        return new ShadowProject(getPrimaryProject());
    }
}
