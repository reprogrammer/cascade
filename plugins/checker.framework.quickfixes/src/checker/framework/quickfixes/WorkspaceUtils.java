package checker.framework.quickfixes;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.checkerframework.eclipse.marker.MarkerReporter;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@SuppressWarnings("restriction")
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

    public static Image loadProposalImage(String imageKey) {
        Image[] images = new Image[] { null };
        Display.getDefault().syncExec(() -> {
            images[0] = JavaPluginImages.get(imageKey);
        });
        Image image = images[0];
        return image;
    }

    private static List<IClasspathEntry> getClassPathEntries(
            IJavaProject javaProject) {
        try {
            return Lists.newArrayList(javaProject.getResolvedClasspath(true));
        } catch (JavaModelException e) {
            throw new RuntimeException(e);
        }
    }

    // private static void getAllChildren(
    // IJavaElement javaElement) {
    // List<IJavaElement> children = new ArrayList<>();
    // children.add(javaElement);
    // for (IJavaElement child : javaElement.getC) {
    //
    // }
    // children.addAll(getAllChildren(javaElement);)
    // }

    public static String getFullyQualifiedName(IJavaProject javaProject,
            String typeName) {
        List<IClasspathEntry> classPathEntries = getClassPathEntries(javaProject);
        List<URL> urls = Lists.transform(classPathEntries,
                new Function<IClasspathEntry, URL>() {
                    @Override
                    public URL apply(IClasspathEntry classPathEntry) {
                        try {
                            File file = classPathEntry.getPath().toFile();
                            if (file.exists()) {
                                return file.toURI().toURL();
                            } else {
                                URI uri = getWorkspaceRoot().getFile(
                                        classPathEntry.getPath())
                                        .getLocationURI();
                                return uri.toURL();
                            }
                            //
                            // (classPathEntry.getPath()).toFile();
                            // File file = classPathEntry.getPath().toFile();
                            // if (!file.exists()) {
                            // System.out.println(file.toString()
                            // + " not found");
                            // }
                            // return file.toURI().toURL();
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
        JarClassLoader jarClassLoader = new JarClassLoader(
                urls.toArray(new URL[urls.size()]));
        try {
            Class<?> foundClass = jarClassLoader.loadClass(typeName);
            return foundClass.getName();
        } catch (ClassNotFoundException e1) {
            // e.printStackTrace();
            // List<Package> packages = jarClassLoader.getPackageList();
            List<String> packages = getPackages(javaProject);
            Iterable<String> filteredPackages = Iterables.filter(packages,
                    aPackage -> !aPackage.startsWith("com.sun"));
            // for (Package aPackage : packages) {
            for (String aPackage : filteredPackages) {
                // String fullName = aPackage.getName() + "." + typeName;
                String fullName = aPackage + "." + typeName;
                // System.out.println(fullName);
                try {
                    Class<?> foundClass = jarClassLoader.loadClass(fullName);
                    Annotation[] declaredAnnotations = foundClass
                            .getDeclaredAnnotations();
                    return foundClass.getName();
                } catch (ClassNotFoundException e2) {
                }
            }
        }
        return "Dummy";
    }

    private static List<String> getPackages(IJavaProject javaProject) {
        try {
            ArrayList<IPackageFragmentRoot> packageFragmentRoots = Lists
                    .newArrayList(javaProject.getAllPackageFragmentRoots());
            List<String> packageNames = new ArrayList<>();
            for (IPackageFragmentRoot packageFragmentRoot : packageFragmentRoots) {
                for (IJavaElement packageFragment : packageFragmentRoot
                        .getChildren()) {
                    packageNames.add(packageFragment.getElementName());
                }
            }
            return packageNames;
        } catch (JavaModelException e) {
            throw new RuntimeException(e);
        }
    }
}
