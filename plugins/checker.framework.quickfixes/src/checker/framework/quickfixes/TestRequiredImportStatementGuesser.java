package checker.framework.quickfixes;

import java.util.Set;

import org.junit.Test;

import static com.google.common.collect.Sets.newHashSet;

import static org.junit.Assert.assertEquals;

public class TestRequiredImportStatementGuesser {

    @Test
    public void test() {
        Set<String> typeQualifiers = new RequiredImportStatementGuesser()
                .getTypeQualifiers("_a _ab @_ab @a12345 ab @a");
        Set<String> expectedTypeQualifiers = newHashSet("_ab", "a12345", "a");
        assertEquals(expectedTypeQualifiers, typeQualifiers);
    }

}
