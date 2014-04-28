package checker.framework.errorcentric.view.views;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IAdaptable;

public class TreeObject implements IAdaptable {

    private String name;

    private TreeObject parent;

    private ArrayList<TreeObject> children;

    private TreeUpdater treeUpdater;

    public TreeObject(String name, TreeUpdater treeUpdater) {
        this.children = new ArrayList<>();
        this.name = name;
        this.treeUpdater = treeUpdater;
    }

    public TreeObject(String name) {
        this(name, new NoOpTreeUpdater());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        treeUpdater.update(this);
    }

    public void setParent(TreeObject parent) {
        this.parent = parent;
    }

    public TreeObject getParent() {
        return parent;
    }

    public String toString() {
        return getName();
    }

    public Object getAdapter(Class key) {
        return null;
    }

    public void addChild(TreeObject child) {
        children.add(child);
        child.setParent(this);
    }

    public void addChildren(Collection<? extends TreeObject> children) {
        for (TreeObject child : children) {
            addChild(child);
        }
    }

    public void removeChild(TreeObject child) {
        children.remove(child);
        child.setParent(null);
    }

    public TreeObject[] getChildren() {
        return (TreeObject[]) children.toArray(new TreeObject[children.size()]);
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }

    public TreeUpdater getTreeUpdater() {
        return treeUpdater;
    }

    public void setLabelUpdateListener(TreeUpdater treeUpdater) {
        this.treeUpdater = treeUpdater;
    }

    public int getRank() {
        return 0;
    }
}