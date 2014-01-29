package checker.framework.quickfixes;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class RequiredImportStatementGuesser {

	public Set<KnownQualifier> guessQualifiers(String typeName) {
		Set<KnownQualifier> qualifierLocations = new HashSet<>();
		for (KnownQualifier qualifier : KnownQualifier.values()) {
			if (typeName.contains(qualifier.name())) {
				qualifierLocations.add(qualifier);
			}
		}
		return qualifierLocations;
	}

	public Iterable<String> guessImportStatementsRequiredBy(String typeName) {
		return Iterables.transform(guessQualifiers(typeName),
				new Function<KnownQualifier, String>() {
					@Override
					public String apply(KnownQualifier qualifier) {
						return qualifier.getFullyQualifiedName();
					}
				});
	}

}
