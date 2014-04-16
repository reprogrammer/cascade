package checker.framework.errorcentric.view.views;

import java.util.Collection;

import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import checker.framework.errorcentric.view.Activator;

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
                            new NoOpTreeUpdater(), false);
            if (nodesFixedByMarkerNode.contains(object)) {
                return Activator
                        .getImageDescriptor("icons/green_checkmark.gif")
                        .createImage();
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
