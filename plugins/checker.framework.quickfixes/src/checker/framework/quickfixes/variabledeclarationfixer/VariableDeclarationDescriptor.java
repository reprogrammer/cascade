package checker.framework.quickfixes.variabledeclarationfixer;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class VariableDeclarationDescriptor {

	private final String variableDeclartionBindingKey;

	public VariableDeclarationDescriptor(String variableDeclartionBindingKey) {
		this.variableDeclartionBindingKey = variableDeclartionBindingKey;
	}

	public String getBindingKey() {
		return variableDeclartionBindingKey;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return variableDeclartionBindingKey;
	}

}
