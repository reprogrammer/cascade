package checker.framework.quickfixes.variabledeclarationfixer;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;

public class TypeDeclarationASTNodeInfo {

	private ChildListPropertyDescriptor modifiersProperty;

	private ASTNode typeDeclarationNode;

	private List<?> modifiers;

	private String identifierName;

	public TypeDeclarationASTNodeInfo(
			ChildListPropertyDescriptor modifiersProperty,
			ASTNode typeDeclarationNode, List<?> modifiers,
			String identifierName) {
		this.modifiersProperty = modifiersProperty;
		this.typeDeclarationNode = typeDeclarationNode;
		this.modifiers = modifiers;
		this.identifierName = identifierName;
	}

	ChildListPropertyDescriptor getModifiersProperty() {
		return modifiersProperty;
	}

	ASTNode getTypeDeclarationNode() {
		return typeDeclarationNode;
	}

	List<?> getModifiers() {
		return modifiers;
	}

	String getIdentifierName() {
		return identifierName;
	}

}
