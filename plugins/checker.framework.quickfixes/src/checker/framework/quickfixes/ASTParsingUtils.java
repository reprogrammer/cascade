package checker.framework.quickfixes;

import static com.google.common.collect.Sets.newHashSet;

import java.util.HashSet;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Type;

public class ASTParsingUtils {

    public static CompilationUnit parse(ICompilationUnit compilationUnit) {
        ASTParser astParser = createASTParser();
        astParser.setKind(ASTParser.K_COMPILATION_UNIT);
        astParser.setSource(compilationUnit);
        return (CompilationUnit) astParser.createAST(null);
    }

    /**
     * @see http
     *      ://help.eclipse.org/juno/index.jsp?topic=/org.eclipse.jdt.doc.isv
     *      /reference/api/org/eclipse/jdt/core/dom/ASTParser.html
     */
    public static Type parseCastString(String typeString) {
        ASTParser astParser = createASTParser();
        astParser.setKind(ASTParser.K_EXPRESSION);
        astParser.setSource(typeString.toCharArray());
        CastExpression astNode = (CastExpression) astParser.createAST(null);
        return astNode.getType();
    }

    public static ASTParser createASTParser() {
        ASTParser astParser = ASTParser.newParser(AST.JLS8);
        Map<?, ?> options = JavaCore.getOptions();
        JavaCore.setComplianceOptions("1.8", options);
        astParser.setCompilerOptions(options);
        astParser.setResolveBindings(true);
        astParser.setBindingsRecovery(true);
        return astParser;
    }

    public static String typeStringExcludingInternalQualifiers(
            String qualifiedTypeString) {
        HashSet<String> internalTypeQualifiers = newHashSet("@Initialized",
                "@FBCBottom", "@UnknownInitialization");
        for (String qualifier : internalTypeQualifiers) {
            qualifiedTypeString = qualifiedTypeString.replace(qualifier, "");
        }
        return qualifiedTypeString.trim();
    }

    public static Type parseTypeStringExcludingInternalQualifiers(
            String typeString) {
        return parseCastString("("
                + typeStringExcludingInternalQualifiers(typeString) + ") null");
    }

}
