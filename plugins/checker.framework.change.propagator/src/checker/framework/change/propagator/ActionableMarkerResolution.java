package checker.framework.change.propagator;

import java.util.Set;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.ui.IMarkerResolution;

import checker.framework.quickfixes.descriptors.Fixer;
import checker.framework.quickfixes.descriptors.FixerDescriptor;
import checker.framework.quickfixes.descriptors.FixerResolutionFactory;

@SuppressWarnings("restriction")
public class ActionableMarkerResolution {

	private final ShadowProject shadowProject;

	private final IMarkerResolution resolution;

	private final Set<ComparableMarker> markers;

	private final FixerDescriptor fixerDescriptor;

	private final Set<ComparableMarker> allMarkers;

	public ActionableMarkerResolution(ShadowProject shadowProject,
			IMarkerResolution resolution, Set<ComparableMarker> markers,
			FixerDescriptor fixerDescriptor, Set<ComparableMarker> allMarkers) {
		this.shadowProject = shadowProject;
		this.resolution = resolution;
		this.markers = markers;
		this.fixerDescriptor = fixerDescriptor;
		this.allMarkers = allMarkers;
	}

	// TODO(reprogrammer): Compute the label without relying on marker
	// resolution.
	public String getLabel() {
		return resolution.getLabel();
	}

	public FixerDescriptor getFixerDescriptor() {
		return fixerDescriptor;
	}

	public ShadowProject getShadowProject() {
		return shadowProject;
	}

	public Set<ComparableMarker> getAllMarkers() {
		return allMarkers;
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
		new FixerResolutionFactory(
				createFixer(shadowProject.getPrimaryProject())).get().run(null);
	}

	@Override
	public String toString() {
		return fixerDescriptor.toString();
	}

}
