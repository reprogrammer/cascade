package checker.framework.quickfixes.descriptors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class MethodDescriptor {

    private final String representation;

    public MethodDescriptor(String representation) {
        this.representation = representation;
    }

    String getRepresentation() {
        return representation;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

}
