package checker.framework.change.propagator;

import java.util.Set;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.ui.IMarkerResolution;

import checker.framework.quickfixes.descriptors.Fixer;
import checker.framework.quickfixes.descriptors.FixerDescriptor;
import checker.framework.quickfixes.descriptors.FixerResolutionFactory;

@SuppressWarnings("restriction")
public class ActionableMarkerResolution {

    private final ShadowOfShadowProject shadowProject;

    private final IMarkerResolution resolution;

    private final Set<ComparableMarker> markersToBeResolvedByFixer;

    private final FixerDescriptor fixerDescriptor;

    private final Set<ComparableMarker> allMarkersBeforeResolution;

    public ActionableMarkerResolution(ShadowOfShadowProject shadowProject,
            IMarkerResolution resolution,
            Set<ComparableMarker> markersToBeResolvedByFixer,
            FixerDescriptor fixerDescriptor,
            Set<ComparableMarker> allMarkersBeforeResolution) {
        this.shadowProject = shadowProject;
        this.resolution = resolution;
        this.markersToBeResolvedByFixer = markersToBeResolvedByFixer;
        this.fixerDescriptor = fixerDescriptor;
        this.allMarkersBeforeResolution = allMarkersBeforeResolution;
    }

    // TODO(reprogrammer): Compute the label without relying on marker
    // resolution.
    public String getLabel() {
        return resolution.getLabel();
    }

    public FixerDescriptor getFixerDescriptor() {
        return fixerDescriptor;
    }

    public ShadowOfShadowProject getShadowProject() {
        return shadowProject;
    }

    public Set<ComparableMarker> getMarkersToBeResolvedByFixer() {
        return markersToBeResolvedByFixer;
    }

    public Set<ComparableMarker> getAllMarkersBeforeResolution() {
        return allMarkersBeforeResolution;
    }

    public Set<ComparableMarker> getMarkersUnresolvedByFixer() {
        return markersToBeResolvedByFixer;
    }

    public Fixer createFixer(IJavaProject javaProject) {
        return fixerDescriptor.createFixerFactory(javaProject).get();
    }

    public void apply() {
        fixerDescriptor
                .createFixerDescriptorApplier(shadowProject.getProject())
                .apply();
    }

    public void run() {
        new FixerResolutionFactory(createFixer(shadowProject.getShadowProject()
                .getPrimaryProject())).get().run(null);
    }

    @Override
    public String toString() {
        return fixerDescriptor.toString();
    }

    @Override
    public int hashCode() {
        return fixerDescriptor.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ActionableMarkerResolution)
                && fixerDescriptor.toString().equals(
                        ((ActionableMarkerResolution) obj).fixerDescriptor
                                .toString());
    }
}
