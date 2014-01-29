package checker.framework.quickfixes.descriptors;

import static com.google.common.collect.Sets.newHashSet;

import java.util.HashSet;
import java.util.Set;

import checker.framework.quickfixes.ErrorKey;
import checker.framework.quickfixes.MarkerContext;

public abstract class FixerDescriptorFactory<T extends FixerDescriptor> {

    protected final MarkerContext context;

    public FixerDescriptorFactory(MarkerContext context) {
        this.context = context;
    }

    protected Set<ErrorKey> getSupportedErrorKeys() {
        return newHashSet(ErrorKey.errorKeys());
    }

    public abstract Set<T> doGet();

    public Set<T> get() {
        if (getSupportedErrorKeys().contains(context.getErrorKey())) {
            return doGet();
        } else {
            return new HashSet<>();
        }
    }

}
