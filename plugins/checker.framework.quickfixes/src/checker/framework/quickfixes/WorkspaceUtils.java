package checker.framework.quickfixes;

import java.util.ArrayList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import checkers.eclipse.marker.MarkerReporter;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class WorkspaceUtils {

    private static final String JAVA_PROJECT_NATURE = "org.eclipse.jdt.core.javanature";

    // Adapted from http://stackoverflow.com/a/252168
    public static IProject createProject(String name) {
        IProgressMonitor progressMonitor = new NullProgressMonitor();
        IProject project = getProject(name);
        try {
            if (project.exists()) {
                project.delete(true, new NullProgressMonitor());
            }
            project.create(progressMonitor);
            project.open(progressMonitor);
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
        return project;
    }

    private static IProject getProject(String name) {
        return getWorkspaceRoot().getProject(name);
    }

    public static boolean isJavaProject(IProject project) {
        try {
            return project.getDescription().hasNature(JAVA_PROJECT_NATURE);
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    public static IJavaProject getJavaProject(String name) {
        IProject project = getProject(name);
        if (isJavaProject(project)) {
            return createJavaProject(project);
        } else {
            throw new RuntimeException(name + " is not a Java project.");
        }
    }

    public static Iterable<IJavaProject> getAllJavaProjects() {
        ArrayList<IProject> allProjects = Lists.newArrayList(getWorkspaceRoot()
                .getWorkspace().getRoot().getProjects());
        Iterable<IProject> javaProjects = Iterables.filter(allProjects,
                new Predicate<IProject>() {
                    @Override
                    public boolean apply(IProject project) {
                        return isJavaProject(project);
                    }
                });
        return Iterables.transform(javaProjects,
                new Function<IProject, IJavaProject>() {
                    @Override
                    public IJavaProject apply(IProject project) {
                        return createJavaProject(project);
                    }
                });
    }

    private static IWorkspaceRoot getWorkspaceRoot() {
        return ResourcesPlugin.getWorkspace().getRoot();
    }

    public static void copyResource(IResource source, IResource target) {
        if (target.exists()) {
            deleteResource(target);
        }
        try {
            source.copy(target.getFullPath(), true, null);
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    private static void deleteResource(IResource resource) {
        try {
            resource.delete(true, null);
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    public static IJavaProject createJavaProject(IProject project) {
        return JavaCore.create(project);
    }

    // Adapted from
    // http://help.eclipse.org/helios/index.jsp?topic=/org.eclipse.platform.doc.isv/guide/resAdv_markers.htm
    public static IMarker[] getMarkers(IResource resource) {
        IMarker[] problems = null;
        int depth = IResource.DEPTH_INFINITE;
        try {
            problems = resource.findMarkers(MarkerReporter.NAME, true, depth);
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
        return problems;
    }

    // Adapted from http://stackoverflow.com/a/5879306
    public static void saveAllEditors() {
        PlatformUI.getWorkbench().saveAllEditors(false);
    }

    public static void closeActiveEditor() {
        IWorkbenchWindow window = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow();
        IWorkbenchPage activePage = window.getActivePage();
        activePage.closeEditor(activePage.getActiveEditor(), true);
    }

}
