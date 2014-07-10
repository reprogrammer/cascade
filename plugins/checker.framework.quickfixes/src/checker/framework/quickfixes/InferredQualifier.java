package checker.framework.quickfixes;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class InferredQualifier {

    public static void initialize(IJavaProject javaProject) {
        qualifiers = CacheBuilder.newBuilder().build(
                new CacheLoader<String, InferredQualifier>() {
                    @Override
                    public InferredQualifier load(String typeName)
                            throws Exception {
                        String fullyQualifiedName = InferredQualifier
                                .getFullyQualifiedName(javaProject, typeName);
                        return new InferredQualifier(fullyQualifiedName,
                                QualifierLocation.TYPE);
                    }
                });
    }

    private static LoadingCache<String, InferredQualifier> qualifiers;

    private final String fullyQualifiedName;

    private final QualifierLocation location;

    private InferredQualifier(String fullyQualifiedName,
            QualifierLocation location) {
        this.fullyQualifiedName = fullyQualifiedName;
        this.location = location;
    }

    public static InferredQualifier infer(String shortName) {
        try {
            return qualifiers.get(shortName);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public QualifierLocation getLocation() {
        return location;
    }

    static List<String> getPackages(IJavaProject javaProject) {
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

    private static List<IClasspathEntry> getClassPathEntries(
            IJavaProject javaProject) {
        try {
            return Lists.newArrayList(javaProject.getResolvedClasspath(true));
        } catch (JavaModelException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFullyQualifiedName(IJavaProject javaProject,
            String typeQualifierName) throws ClassNotFoundException {
        JarClassLoader jarClassLoader = createJarClassLoader(javaProject);
        List<String> packages = getPackages(javaProject);
        try {
            return findTypeQualifierClass(typeQualifierName, jarClassLoader,
                    packages).getName();
        } catch (ClassNotFoundException e) {
            throw e;
        }
    }

    private static Class<?> findTypeQualifierClass(String typeQualifierName,
            JarClassLoader jarClassLoader, List<String> packages)
            throws ClassNotFoundException {
        try {
            Class<?> foundClass = jarClassLoader.loadClass(typeQualifierName);
            return foundClass;
        } catch (ClassNotFoundException e1) {
            Iterable<String> filteredPackages = Iterables.filter(packages,
                    aPackage -> !aPackage.startsWith("com.sun"));
            for (String aPackage : filteredPackages) {
                String fullName = aPackage + "." + typeQualifierName;
                try {
                    Class<?> foundClass = jarClassLoader.loadClass(fullName);
                    return foundClass;
                } catch (ClassNotFoundException e2) {
                }
            }
        } finally {
            try {
                jarClassLoader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        throw new ClassNotFoundException(typeQualifierName);
    }

    private static JarClassLoader createJarClassLoader(IJavaProject javaProject) {
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
                                URI uri = WorkspaceUtils.getWorkspaceRoot()
                                        .getFile(classPathEntry.getPath())
                                        .getLocationURI();
                                return uri.toURL();
                            }
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
        JarClassLoader jarClassLoader = new JarClassLoader(
                urls.toArray(new URL[urls.size()]));
        return jarClassLoader;
    }

}
