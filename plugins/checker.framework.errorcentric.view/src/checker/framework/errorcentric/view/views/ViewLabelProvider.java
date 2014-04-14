package checker.framework.errorcentric.view.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import checker.framework.errorcentric.view.Activator;

public class ViewLabelProvider extends LabelProvider {

    public String getText(Object obj) {
        if (obj instanceof ErrorTreeNode) {
            return removeLeadingWhiteSpaces(obj.toString());
        } else {
            return obj.toString();
        }
    }

    private String removeLeadingWhiteSpaces(String s) {
        String[] lines = s.split("\n");
        for (int i = 0; i < lines.length; ++i) {
            lines[i] = lines[i].trim();
        }
        return String.join("\n", lines);
    }

    public Image getImage(Object obj) {
        if (obj instanceof AddedErrorTreeNode) {
            return Activator.getImageDescriptor("icons/quickfix_error_obj.gif")
                    .createImage();
        } else if (obj instanceof RemovedErrorTreeNode) {
            /*
             * Original icon (green_checkmark.png) copied from:
             * http://www.flickr.com/photos/inspiredhomefitness/8753911733/
             * under CC BY-NC-SA 2.0 license
             * (http://creativecommons.org/licenses/by-nc-sa/2.0/deed.en)
             * green_checkmark.gif is the resized and transparent-background version
             */
            return Activator.getImageDescriptor("icons/green_checkmark.gif")
                    .createImage();
        }
        return PlatformUI.getWorkbench().getSharedImages()
                .getImage(ISharedImages.IMG_OBJ_ELEMENT);
    }

}