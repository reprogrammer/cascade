package checker.framework.changes.view.views;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IAdaptable;

public class TreeObject implements IAdaptable {

	private String name;

	private TreeObject parent;

	protected ArrayList<TreeObject> children;

	public TreeObject(String name) {
		this.name = name;
		this.children = new ArrayList<>();
	}

	public String getName() {
		return name;
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

}