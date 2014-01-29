package checker.framework.change.propagator;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.text.IDocument;

public class CompilationUnitTextExtractor {

    private final ICompilationUnit compilationUnit;

    public CompilationUnitTextExtractor(ICompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;
    }

    public String getText() {
        ITextFileBufferManager bufferManager = FileBuffers
                .getTextFileBufferManager();
        IPath path = compilationUnit.getPath();
        String text;
        try {
            bufferManager.connect(path, LocationKind.IFILE, null);
            ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(
                    path, LocationKind.IFILE);
            IDocument document = textFileBuffer.getDocument();
            text = document.get();
        } catch (CoreException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                bufferManager.disconnect(path, LocationKind.IFILE, null);
            } catch (CoreException e) {
                throw new RuntimeException(e);
            }
        }
        return text;
    }

}
