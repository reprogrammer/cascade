package checker.framework.separated.view.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class ViewLabelProvider extends LabelProvider {

    public String getText(Object obj) {
        return obj.toString();
    }

    public Image getImage(Object obj) {
        return PlatformUI.getWorkbench().getSharedImages()
                .getImage(ISharedImages.IMG_OBJ_ELEMENT);
    }

}