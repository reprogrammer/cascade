package checker.framework.changes.view.views;

import checker.framework.change.propagator.ComparableMarker;
import checker.framework.change.propagator.MarkerLocation;

import com.google.common.base.Optional;

public class ErrorTreeNode extends TreeObject {

    protected ComparableMarker marker;

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

}
