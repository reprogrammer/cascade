package checker.framework.errorcentric.view.views;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Color;

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

}
