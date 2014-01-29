package checker.framework.quickfixes;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.text.correction.AssistContext;
import org.eclipse.jdt.internal.ui.text.correction.ProblemLocation;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.ui.IEditorInput;

import checkers.eclipse.marker.MarkerReporter;

import com.google.common.base.Optional;

@SuppressWarnings("restriction")
public class MarkerContextFactory {

    private final IMarker marker;

    public MarkerContextFactory(IMarker marker) {
        this.marker = marker;
    }

    public Optional<MarkerContext> get() {
        ICompilationUnit cu = getCompilationUnit(marker);
        if (cu != null) {
            IEditorInput input = EditorUtility.getEditorInput(cu);
            if (input != null) {
                IProblemLocation location = findProblemLocation(marker);
                if (location != null) {
                    IInvocationContext context = new AssistContext(cu,
                            location.getOffset(), location.getLength());
                    return Optional.of(new MarkerContext(context, location,
                            getErrorKey(marker)));
                }
            }
        }
        return Optional.absent();
    }

    private ICompilationUnit getCompilationUnit(IMarker marker) {
        IResource res = marker.getResource();
        if (res instanceof IFile && res.isAccessible()) {
            IJavaElement element = JavaCore.create((IFile) res);
            if (element instanceof ICompilationUnit)
                return (ICompilationUnit) element;
        }
        return null;
    }

    private IProblemLocation findProblemLocation(IMarker marker) {
        return createFromMarker(marker, getCompilationUnit(marker));
    }

    private ErrorKey getErrorKey(IMarker marker) {
        String errorKey = marker.getAttribute(MarkerReporter.ERROR_KEY, null);
        return ErrorKey.createErrorKey(errorKey);
    }

    private IProblemLocation createFromMarker(IMarker marker,
            ICompilationUnit cu) {
        try {
            int id = marker.getAttribute(IJavaModelMarker.ID, -1);
            int start = marker.getAttribute(IMarker.CHAR_START, -1);
            int end = marker.getAttribute(IMarker.CHAR_END, -1);
            int severity = marker.getAttribute(IMarker.SEVERITY,
                    IMarker.SEVERITY_INFO);
            int numberOfArguments = marker.getAttribute(
                    MarkerReporter.NUM_ERROR_ARGUMENTS, -1);
            if (numberOfArguments < 2) {
                return null;
            }
            String[] arguments = new String[] {
                    (String) marker.getAttribute(MarkerReporter.ERROR_ARGUMENTS
                            + String.valueOf(numberOfArguments - 2)),
                    (String) marker.getAttribute(MarkerReporter.ERROR_ARGUMENTS
                            + String.valueOf(numberOfArguments - 1)) };
            String markerType = marker.getType();
            if (cu != null /* && id != -1 */&& start != -1 && end != -1
            /* && arguments != null */) {
                boolean isError = (severity == IMarker.SEVERITY_ERROR);
                return new ProblemLocation(start, end - start, id, arguments,
                        isError, markerType);
            }
        } catch (CoreException e) {
            JavaPlugin.log(e);
        }
        return null;
    }

}
