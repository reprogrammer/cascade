package checker.framework.errorcentric.view.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import checker.framework.errorcentric.view.Activator;

public class ViewLabelProvider extends LabelProvider {

    public String getText(Object obj) {
        return obj.toString();
    }

    public Image getImage(Object obj) {
        if (obj instanceof AddedErrorTreeNode) {
            return Activator.getImageDescriptor("icons/quickfix_error_obj.gif")
                    .createImage();
        } else if (obj instanceof RemovedErrorTreeNode) {
            /*
             * Icon copied from:
             * http://www.flickr.com/photos/inspiredhomefitness/8753911733/
             * under CC BY-NC-SA 2.0 license
             * (http://creativecommons.org/licenses/by-nc-sa/2.0/deed.en)
             */
            return Activator.getImageDescriptor("icons/green_checkmark.png")
                    .createImage();
        }
        return PlatformUI.getWorkbench().getSharedImages()
                .getImage(ISharedImages.IMG_OBJ_ELEMENT);
    }

}