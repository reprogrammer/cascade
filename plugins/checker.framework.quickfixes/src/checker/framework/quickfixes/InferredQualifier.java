package checker.framework.quickfixes;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

import static com.google.common.collect.Sets.intersection;
import static com.google.common.collect.Sets.newHashSet;

public class InferredQualifier {

    static void initialize(IJavaProject javaProject) {
        qualifiers = CacheBuilder.newBuilder().build(
                new CacheLoader<String, InferredQualifier>() {
                    @Override
                    public InferredQualifier load(String typeName)
                            throws Exception {
                        return new InferredQualifier(InferredQualifier
                                .findTypeQualifierClass(javaProject, typeName));
                    }
                });
    }

    private static LoadingCache<String, InferredQualifier> qualifiers;

    private final Class<?> theClass;

    private InferredQualifier(Class<?> theClass) {
        this.theClass = theClass;
    }

    public static InferredQualifier infer(String shortName) {
        try {
            return qualifiers.get(shortName);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFullyQualifiedName() {
        return theClass.getName();
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

    private static Class<?> findTypeQualifierClass(IJavaProject javaProject,
            String typeQualifierName) throws ClassNotFoundException {
        JarClassLoader jarClassLoader = createJarClassLoader(javaProject);
        List<String> packages = getPackages(javaProject);
        try {
            return findTypeQualifierClass(typeQualifierName, jarClassLoader,
                    packages);
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

    public static Set<ElementType> getAnnotationTargets(Class<?> annotation) {
        Target targetAnnotation = annotation
                .getDeclaredAnnotation(Target.class);
        return newHashSet(targetAnnotation.value());
    }

    public boolean isMethodAnnotation() {
        Set<ElementType> methodAnnotations = newHashSet(ElementType.METHOD,
                ElementType.CONSTRUCTOR);
        return !intersection(methodAnnotations, getAnnotationTargets(theClass))
                .isEmpty();
    }

}
