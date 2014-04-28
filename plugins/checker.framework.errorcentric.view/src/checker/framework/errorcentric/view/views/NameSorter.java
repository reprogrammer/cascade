package checker.framework.errorcentric.view.views;

import org.eclipse.jface.viewers.ViewerSorter;

public class NameSorter extends ViewerSorter {

    @Override
    public int category(Object element) {
        if (element instanceof TreeObject) {
            return -((TreeObject) element).getRank();
        }
        return super.category(element);
    }

}