package checker.framework.quickfixes;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

public enum KnownQualifier {

	Initialized("checkers.initialization.quals", QualifierLocation.TYPE),

	UnknownInitialization("checkers.initialization.quals",
			QualifierLocation.TYPE),

	UnderInitialization("checkers.initialization.quals", QualifierLocation.TYPE),

	AssertNonNullIfNonNull("checkers.nullness.quals", QualifierLocation.METHOD),

	EnsuresNonNullIfNonNull("checkers.nullness.quals", QualifierLocation.METHOD),

	Covariant("checkers.nullness.quals", QualifierLocation.TYPE),

	EnsuresNonNull("checkers.nullness.quals", QualifierLocation.METHOD),

	KeyFor("checkers.nullness.quals", QualifierLocation.TYPE),

	KeyForBottom("checkers.nullness.quals", QualifierLocation.TYPE),

	LazyNonNull("checkers.nullness.quals", QualifierLocation.TYPE),

	MonotonicNonNull("checkers.nullness.quals", QualifierLocation.TYPE),

	NonNull("checkers.nullness.quals", QualifierLocation.TYPE),

	NonRaw("checkers.nullness.quals", QualifierLocation.TYPE),

	Nullable("checkers.nullness.quals", QualifierLocation.TYPE),

	PolyNull("checkers.nullness.quals", QualifierLocation.TYPE),

	PolyRaw("checkers.nullness.quals", QualifierLocation.TYPE),

	Raw("checkers.nullness.quals", QualifierLocation.TYPE),

	RequiresNonNull("checkers.nullness.quals", QualifierLocation.METHOD),

	Assignable("checkers.javari.quals", QualifierLocation.TYPE),

	Mutable("checkers.javari.quals", QualifierLocation.TYPE),

	PolyRead("checkers.javari.quals", QualifierLocation.TYPE),

	QReadOnly("checkers.javari.quals", QualifierLocation.TYPE),

	ReadOnly("checkers.javari.quals", QualifierLocation.TYPE),

	ThisMutable("checkers.javari.quals", QualifierLocation.TYPE);

	private final String declaredPackage;

	private final QualifierLocation location;

	private KnownQualifier(String declaredPackage, QualifierLocation location) {
		this.declaredPackage = declaredPackage;
		this.location = location;
	}

	public String getFullyQualifiedName() {
		return declaredPackage + "." + name();
	}

	public QualifierLocation getLocation() {
		return location;
	}

	public static Set<KnownQualifier> qualifiers() {
		return newHashSet(values());
	}

}
