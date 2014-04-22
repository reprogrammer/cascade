package checker.framework.errorcentric.view.views;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;

public class IdentityMarkerResolution implements IMarkerResolution {

    @Override
    public String getLabel() {
        return "";
    }

    @Override
    public void run(IMarker marker) {
    }

}
