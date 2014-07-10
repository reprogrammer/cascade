package checker.framework.quickfixes;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import static com.google.common.collect.Sets.newHashSet;

public class RequiredImportStatementGuesser {

    Set<String> getTypeQualifiers(String typeName) {
        Set<String> typeQualifiers = newHashSet();
        Pattern regex = Pattern.compile("@([\\p{Alpha}_][\\p{Alnum}_]*)");
        Matcher regexMatcher = regex.matcher(typeName);
        while (regexMatcher.find()) {
            typeQualifiers.add(regexMatcher.group(1));
        }
        return typeQualifiers;
    }

    private Set<InferredQualifier> guessQualifiers(String typeName) {
        Set<InferredQualifier> inferredQualifierLocations = newHashSet();
        Set<String> typeQualifiers = getTypeQualifiers(typeName);
        for (String typeQualifier : typeQualifiers) {
            inferredQualifierLocations.add(InferredQualifier
                    .infer(typeQualifier));
        }
        return inferredQualifierLocations;
    }

    public Iterable<String> guessImportStatementsRequiredBy(String typeName) {
        return Iterables.transform(guessQualifiers(typeName),
                new Function<InferredQualifier, String>() {
                    @Override
                    public String apply(InferredQualifier qualifier) {
                        return qualifier.getFullyQualifiedName();
                    }
                });
    }

}
