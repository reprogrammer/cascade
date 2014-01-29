package checker.framework.quickfixes;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.correction.ChangeCorrectionProposal;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.PerformChangeOperation;
import org.eclipse.ltk.core.refactoring.RefactoringCore;

import checker.framework.quickfixes.descriptors.FixerFactory;

public class FixerDescriptorApplier {

    private final FixerFactory fixerFactory;

    public FixerDescriptorApplier(FixerFactory fixerFactory) {
        this.fixerFactory = fixerFactory;
    }

    public void apply() {
        IJavaCompletionProposal proposal = fixerFactory.get().getProposal();
        if (proposal instanceof ChangeCorrectionProposal) {
            try {
                Change change = ((ChangeCorrectionProposal) proposal)
                        .getChange();
                String name = ((ChangeCorrectionProposal) proposal).getName();
                performChange(change, name);
            } catch (CoreException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("Unexpected type of proposal: "
                    + proposal.getClass().getCanonicalName());
        }
    }

    // Adapted from
    // http://www.eclipse.org/articles/Article-JavaCodeManipulation_AST/index.html#sec-write-it-down
    private void performChange(Change change, String name) {
        ITextFileBufferManager bufferManager = FileBuffers
                .getTextFileBufferManager();
        Object affectedObject = change.getAffectedObjects()[0];
        if (affectedObject instanceof IJavaElement) {
            IPath path = ((IJavaElement) affectedObject).getPath();
            try {
                bufferManager.connect(path, LocationKind.IFILE, null);
                ITextFileBuffer textFileBuffer = bufferManager
                        .getTextFileBuffer(path, LocationKind.IFILE);
                performChangeOperation(change, name);
                textFileBuffer.commit(null, false);
            } catch (CoreException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    bufferManager.disconnect(path, LocationKind.IFILE, null);
                } catch (CoreException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            throw new RuntimeException("Found unexpected type "
                    + affectedObject.getClass().getCanonicalName());
        }
    }

    private void performChangeOperation(Change change, String name) {
        change.initializeValidationData(new NullProgressMonitor());
        PerformChangeOperation operation = new PerformChangeOperation(change);
        operation.setUndoManager(RefactoringCore.getUndoManager(), name);
        try {
            operation.run(new NullProgressMonitor());
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

}
