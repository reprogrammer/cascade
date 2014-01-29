package checker.framework.quickfixes;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

// Adapted from checkers/src/checkers/basetype/messages.properties
public enum ErrorKey {

    AssignabilityInvalid("assignability.invalid"),

    ReceiverInvalid("receiver.invalid"),

    ArrayInitializerTypeIncompatible("array.initializer.type.incompatible"),

    AssignmentTypeIncompatible("assignment.type.incompatible"),

    CompoundAssignmentTypeIncompatible("compound.assignment.type.incompatible"),

    EnhancedforTypeIncompatible("enhancedfor.type.incompatible"),

    VectorCopyintoTypeIncompatible("vector.copyinto.type.incompatible"),

    ReturnTypeIncompatible("return.type.incompatible"),

    AnnotationTypeIncompatible("annotation.type.incompatible"),

    ConditionalTypeIncompatible("conditional.type.incompatible"),

    TypeArgumentTypeIncompatible("type.argument.type.incompatible"),

    ArgumentTypeIncompatible("argument.type.incompatible"),

    TypeIncompatible("type.incompatible"),

    MonotonicTypeIncompatible("monotonic.type.incompatible"),

    TypeInvalid("type.invalid"),

    CastUnsafe("cast.unsafe"),

    OverrideReturnInvalid("override.return.invalid"),

    OverrideParamInvalid("override.param.invalid"),

    OverrideReceiverInvalid("override.receiver.invalid"),

    MethodInvocationInvalid("method.invocation.invalid"),

    ConstructorInvocationInvalid("constructor.invocation.invalid"),

    OTHER("other");

    private final String errorKey;

    private ErrorKey(String errorKey) {
        this.errorKey = errorKey;
    }

    String getErrorKey() {
        return errorKey;
    }

    public static ErrorKey createErrorKey(final String errorKey) {
        Optional<ErrorKey> matchingErrorKey = Iterables.tryFind(errorKeys(),
                new Predicate<ErrorKey>() {
                    @Override
                    public boolean apply(ErrorKey key) {
                        return key.getErrorKey().equals(errorKey);
                    }
                });
        if (matchingErrorKey.isPresent()) {
            return matchingErrorKey.get();
        } else {
            return OTHER;
        }
    }

    public static Set<ErrorKey> errorKeys() {
        return newHashSet(values());
    }

}
