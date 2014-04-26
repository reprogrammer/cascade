package checker.framework.errorcentric.view.views;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import checker.framework.errorcentric.view.Activator;

public class DisabledNodesLabelProvider extends DecoratingLabelProvider {

    ChangeStateViewer changeStateViewer;

    public DisabledNodesLabelProvider(ILabelProvider provider,
            ILabelDecorator decorator, ChangeStateViewer changeStateViewer) {
        super(provider, decorator);
        this.changeStateViewer = changeStateViewer;
    }

    @Override
    public Color getForeground(Object object) {
        if (object instanceof TreeObject
                && changeStateViewer.isDisabled((TreeObject) object)) {
            return Colors.GRAY;
        }
        return null;
    }

    @Override
    public Image getImage(Object element) {
        if (element instanceof TreeObject) {
            TreeObject treeObject = (TreeObject) element;
            if (changeStateViewer.isDisabled(treeObject)) {
                if (treeObject instanceof ErrorTreeNode) {
                    return Activator.getImageDescriptor(
                            "icons/gray_checkmark.gif").createImage();
                } else if (treeObject instanceof MarkerResolutionTreeNode) {
                    return Activator.getImageDescriptor(
                            "icons/gray_annotation_obj.gif").createImage();
                }
            }
        }
        return super.getImage(element);
    }
}
