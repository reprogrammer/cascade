package checker.framework.quickfixes.methodreturnfixer;

import static com.google.common.collect.Iterables.getLast;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.text.correction.IProposalRelevance;
import org.eclipse.jdt.internal.ui.text.correction.proposals.LinkedCorrectionProposal;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.swt.graphics.Image;

import checker.framework.quickfixes.ASTParsingUtils;
import checker.framework.quickfixes.ImportRewriter;
import checker.framework.quickfixes.KnownQualifier;
import checker.framework.quickfixes.QualifierLocation;
import checker.framework.quickfixes.descriptors.Fixer;

import com.google.common.base.Splitter;

@SuppressWarnings("restriction")
public class MethodReturnFixer implements Fixer {

    private final CompilationUnit compilationUnit;

    private final MethodDeclaration methodDeclaration;

    private final String newReturnTypeString;

    private final ImportRewriter importRewriter = new ImportRewriter();

    public MethodReturnFixer(CompilationUnit compilationUnit,
            MethodDeclaration methodDeclaration, String newReturnTypeString) {
        this.compilationUnit = compilationUnit;
        this.methodDeclaration = methodDeclaration;
        this.newReturnTypeString = newReturnTypeString;
    }

    public ICompilationUnit getCompilationUnit() {
        return (ICompilationUnit) compilationUnit.getJavaElement();
    }

    public int getOffset() {
        return methodDeclaration.getReturnType2().getStartPosition();
    }

    public int getLength() {
        return methodDeclaration.getReturnType2().getLength();
    }

    @Override
    public IJavaCompletionProposal getProposal() {
        ASTRewrite rewrite = ASTRewrite.create(compilationUnit.getAST());
        String sanitizedTypeString = ASTParsingUtils
                .typeStringExcludingInternalQualifiers(newReturnTypeString);
        String label = String.format("Change return type of %s to %s",
                methodDeclaration.getName().getIdentifier(),
                sanitizedTypeString);
        Image image = JavaPluginImages
                .get(JavaPluginImages.IMG_CORRECTION_CHANGE);
        LinkedCorrectionProposal proposal = new LinkedCorrectionProposal(label,
                (ICompilationUnit) compilationUnit.getJavaElement(), rewrite,
                IProposalRelevance.CHANGE_METHOD_RETURN_TYPE, image);
        importRewriter.addRequiredImports(proposal, compilationUnit,
                methodDeclaration, newReturnTypeString);
        ASTNode newReturnTypeAstNode = ASTParsingUtils
                .parseTypeStringExcludingInternalQualifiers(newReturnTypeString);
        rewrite.replace(methodDeclaration.getReturnType2(),
                newReturnTypeAstNode, null);

        removeMethodAnnotations(rewrite);

        String returnKey = "return"; //$NON-NLS-1$
        proposal.addLinkedPosition(rewrite.track(newReturnTypeAstNode), true,
                returnKey);
        return proposal;
    }

    private void removeMethodAnnotations(ASTRewrite rewrite) {
        ListRewrite listRewrite = rewrite.getListRewrite(methodDeclaration,
                methodDeclaration.getModifiersProperty());
        for (Object object : methodDeclaration.modifiers()) {
            if (object instanceof Annotation) {
                Annotation annotation = (Annotation) object;
                String fullyQualifiedName = annotation.getTypeName()
                        .getFullyQualifiedName();
                String simpleName = getSimpleName(fullyQualifiedName);
                KnownQualifier qualifier = KnownQualifier.valueOf(simpleName);
                if (qualifier != null
                        && qualifier.getLocation() == QualifierLocation.TYPE) {
                    listRewrite.remove(annotation, null);
                }
            }
        }
    }

    private String getSimpleName(String fullyQualifiedName) {
        return getLast(Splitter.on('.').split(fullyQualifiedName));
    }

}
