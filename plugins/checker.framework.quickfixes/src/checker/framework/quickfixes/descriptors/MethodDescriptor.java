package checker.framework.quickfixes.descriptors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class MethodDescriptor {

    private final String bindingKey;

    public MethodDescriptor(String bindingKey) {
        this.bindingKey = bindingKey;
    }

    String getBindingKey() {
        return bindingKey;
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
        return bindingKey;
    }

}
