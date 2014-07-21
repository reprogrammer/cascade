package checker.framework.changes.view.views;

import java.util.Set;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import checker.framework.change.propagator.ActionableMarkerResolution;
import checker.framework.change.propagator.ShadowOfShadowProject;
import checker.framework.change.propagator.ShadowOfShadowProjectFactory;
import checker.framework.change.propagator.ShadowProject;
import checker.framework.change.propagator.ShadowProjectFactory;
import checker.framework.change.propagator.commands.InferCommandHandler;

/**
 * The content provider class is responsible for providing objects to the view.
 * It can wrap existing objects in adapters or simply return objects as-is.
 * These objects may be sensitive to the current input of the view, or ignore it
 * and always show the same content (like Task List, for example).
 */
public class ViewContentProvider implements IStructuredContentProvider,
        ITreeContentProvider {

    private TreeObject invisibleRoot;

    private IJavaProject javaProject;

    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
    }

    public void dispose() {
    }

    public Object[] getElements(Object parent) {
        if (invisibleRoot == null) {
            initialize();
            return getChildren(invisibleRoot);
        }
        return getChildren(parent);
    }

    public Object getParent(Object child) {
        return ((TreeObject) child).getParent();
    }

    public Object[] getChildren(Object parent) {
        return ((TreeObject) parent).getChildren();
    }

    public boolean hasChildren(Object parent) {
        return ((TreeObject) parent).hasChildren();
    }

    private void initialize() {
        invisibleRoot = new TreeObject("");
        if (InferCommandHandler.checkerID == null) {
            return;
        }
        if (!InferCommandHandler.selectedJavaProject.isPresent()) {
            return;
        }
        javaProject = InferCommandHandler.selectedJavaProject.get();
        ShadowProject shadowProject = new ShadowProjectFactory(javaProject)
                .get();
        ShadowOfShadowProject shadowOfShadowProject = new ShadowOfShadowProjectFactory(
                shadowProject.getProject()).get();
        shadowOfShadowProject.runChecker(InferCommandHandler.checkerID);
        Set<ActionableMarkerResolution> resolutions = shadowOfShadowProject
                .getResolutions();
        for (ActionableMarkerResolution resolution : resolutions) {
            TreeObject node = new MarkerResolutionTreeNode(resolution);
            invisibleRoot.addChild(node);
        }
    }

}