package checker.framework.errorcentric.view.views;

import java.util.HashSet;
import java.util.Set;

import checker.framework.change.propagator.ActionableMarkerResolution;
import checker.framework.change.propagator.ComparableMarker;
import checker.framework.change.propagator.MarkerLocation;

import com.google.common.base.Optional;

public class ErrorTreeNode extends TreeObject {

    protected ComparableMarker marker;
    protected Set<ActionableMarkerResolution> resolutions = new HashSet<ActionableMarkerResolution>();

    public ErrorTreeNode(ComparableMarker marker) {
        super(marker.getMessage());
        this.marker = marker;
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    public void reveal() {
        Optional<MarkerLocation> optionalMarkerLocation = marker
                .createMarkerLocation();
        if (optionalMarkerLocation.isPresent()) {
            MarkerLocation markerLocation = optionalMarkerLocation.get();
            new CodeSnippetRevealer().reveal(
                    markerLocation.getCompilationUnit(),
                    markerLocation.getOffset(), markerLocation.getLength());
        }
    }

    @Override
    public int hashCode() {
        return marker.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ErrorTreeNode)
                && marker.equals(((ErrorTreeNode) obj).marker);
    }

    public void addResolution(ActionableMarkerResolution resolution) {
        resolutions.add(resolution);
    }
}
