package checker.framework.quickfixes.descriptors;

import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.internal.core.dom.NaiveASTFlattener;

@SuppressWarnings("restriction")
public class MethodDeclarationFlattener extends NaiveASTFlattener {

	@Override
	public boolean visit(Block node) {
		return false;
	}

}
