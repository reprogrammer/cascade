package checker.framework.errorcentric.view.views;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

/**
 * 
 * This class creates a composite image, given a base image and an overlay
 * image.
 * 
 */
public class OverlayImageIcon extends CompositeImageDescriptor {

    private Image baseImage;
    private Image overlayImage;
    private Point size;

    public OverlayImageIcon(Image baseImage, Image overlayImage) {
        this.baseImage = baseImage;
        this.overlayImage = overlayImage;
        this.size = new Point(baseImage.getBounds().width,
                baseImage.getBounds().height);
    }

    @Override
    protected void drawCompositeImage(int width, int height) {
        drawImage(baseImage.getImageData(), 0, 0);
        drawImage(overlayImage.getImageData(), 0, 0);
    }

    @Override
    protected Point getSize() {
        return size;
    }
}
