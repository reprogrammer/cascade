package checker.framework.separated.view.views.list;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import checker.framework.change.propagator.ComparableMarker;

public class ListLabelProvider extends LabelProvider {

    public String getText(Object obj) {
        return ((ComparableMarker)obj).getMessage();
    }
}