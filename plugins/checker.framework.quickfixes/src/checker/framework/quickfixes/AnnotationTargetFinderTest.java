package checker.framework.quickfixes;

import org.junit.Test;

import static com.google.common.collect.Sets.newHashSet;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static org.junit.Assert.assertEquals;

public class AnnotationTargetFinderTest {

    @Test
    public void test() {
        assertEquals(
                newHashSet(TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR,
                        LOCAL_VARIABLE),
                InferredQualifier.getAnnotationTargets(SuppressWarnings.class));
    }

}
