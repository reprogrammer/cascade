package checker.framework.errorcentric.view.views;

import java.util.Collection;

import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import static com.google.common.collect.Sets.newHashSet;

public class FixedErrorDecorator implements ILabelDecorator {

    private MarkerResolutionTreeNode markerResolutionTreeNode;

    public FixedErrorDecorator(MarkerResolutionTreeNode markerResolutionTreeNode) {
        this.markerResolutionTreeNode = markerResolutionTreeNode;
    }

    public Image decorateImage(Image image, Object object) {
        if (object instanceof ErrorTreeNode) {
            Collection<ErrorTreeNode> nodesFixedByMarkerNode = ErrorTreeNode
                    .createTreeNodesFrom(
                            newHashSet(markerResolutionTreeNode.getResolution()),
                            new NoOpTreeLabelUpdater(), false);
            if (nodesFixedByMarkerNode.contains(object)) {
                Image overlayImage = PlatformUI.getWorkbench()
                        .getSharedImages()
                        .getImage(ISharedImages.IMG_DEC_FIELD_ERROR);
                OverlayImageIcon newImage = new OverlayImageIcon(image,
                        overlayImage);
                return newImage.createImage();
            }
        }
        return null;
    }

    public String decorateText(String label, Object object) {
        return null;
    }

    @Override
    public void addListener(ILabelProviderListener listener) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public boolean isLabelProperty(Object element, String property) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {
    }

}
