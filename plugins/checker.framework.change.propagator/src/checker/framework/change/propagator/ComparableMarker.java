package checker.framework.change.propagator;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import checkers.eclipse.marker.MarkerReporter;

import com.google.common.base.Optional;

public class ComparableMarker {

    private final IMarker marker;

    @SuppressWarnings("unused")
    private final IPath fullPath;

    @SuppressWarnings("unused")
    private final String type;

    private String message;

    private MarkerLocationDescriptor markerLocationDescriptor;

    private ShadowProject shadowProject;

    private ComparableMarker(ShadowProject shadowProject, IMarker marker,
            MarkerLocationDescriptor markerLocationDescriptor, IPath fullPath,
            String type, String message) {
        this.shadowProject = shadowProject;
        this.marker = marker;
        this.markerLocationDescriptor = markerLocationDescriptor;
        this.fullPath = fullPath;
        this.type = type;
        this.message = message;
    }

    public static ComparableMarker create(ShadowProject shadowProject,
            IMarker marker) {
        try {
            MarkerLocationDescriptorFactory factory = new MarkerLocationDescriptorFactory(
                    marker);
            return new ComparableMarker(shadowProject, marker, factory.get(),
                    marker.getResource().getFullPath(), marker.getType(),
                    (String) marker.getAttribute(IMarker.MESSAGE));
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    public IMarker getMarker() {
        return marker;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorKey() {
        return getMarker().getAttribute(MarkerReporter.ERROR_KEY, null);
    }

    public Optional<MarkerLocation> createMarkerLocation() {
        return new MarkerLocationFactory(markerLocationDescriptor)
                .createMarkerLocation(shadowProject.getPrimaryProject());
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "marker",
                "markerLocationDescriptor", "shadowProject");
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj, "marker",
                "markerLocationDescriptor", "shadowProject");
    }

}
