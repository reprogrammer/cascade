package checker.framework.separated.view.views;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.ITextEditor;

public class CodeSnippetRevealer {

    public void reveal(ICompilationUnit compilationUnit, int offset, int length) {
        try {
            IEditorPart part = JavaUI
                    .openInEditor(compilationUnit, true, false);
            if (part instanceof ITextEditor) {
                ((ITextEditor) part).selectAndReveal(offset, length);
            }
        } catch (JavaModelException | PartInitException e) {
            throw new RuntimeException(e);
        }
    }

}
