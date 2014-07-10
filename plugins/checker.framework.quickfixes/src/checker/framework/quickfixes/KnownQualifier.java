package checker.framework.quickfixes;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

@Deprecated
public enum KnownQualifier {

    Initialized("org.checkerframework.checker.initialization.qual",
            QualifierLocation.TYPE),

    UnknownInitialization("org.checkerframework.checker.initialization.qual",
            QualifierLocation.TYPE),

    UnderInitialization("org.checkerframework.checker.initialization.qual",
            QualifierLocation.TYPE),

    AssertNonNullIfNonNull("org.checkerframework.checker.nullness.qual",
            QualifierLocation.METHOD),

    EnsuresNonNullIfNonNull("org.checkerframework.checker.nullness.qual",
            QualifierLocation.METHOD),

    Covariant("org.checkerframework.checker.nullness.qual",
            QualifierLocation.TYPE),

    EnsuresNonNull("org.checkerframework.checker.nullness.qual",
            QualifierLocation.METHOD),

    KeyFor("org.checkerframework.checker.nullness.qual", QualifierLocation.TYPE),

    KeyForBottom("org.checkerframework.checker.nullness.qual",
            QualifierLocation.TYPE),

    LazyNonNull("org.checkerframework.checker.nullness.qual",
            QualifierLocation.TYPE),

    MonotonicNonNull("org.checkerframework.checker.nullness.qual",
            QualifierLocation.TYPE),

    NonNull("org.checkerframework.checker.nullness.qual",
            QualifierLocation.TYPE),

    NonRaw("org.checkerframework.checker.nullness.qual", QualifierLocation.TYPE),

    Nullable("org.checkerframework.checker.nullness.qual",
            QualifierLocation.TYPE),

    PolyNull("org.checkerframework.checker.nullness.qual",
            QualifierLocation.TYPE),

    PolyRaw("org.checkerframework.checker.nullness.qual",
            QualifierLocation.TYPE),

    Raw("org.checkerframework.checker.nullness.qual", QualifierLocation.TYPE),

    RequiresNonNull("org.checkerframework.checker.nullness.qual",
            QualifierLocation.METHOD),

    Assignable("org.checkerframework.checker.javari.qual",
            QualifierLocation.TYPE),

    Mutable("org.checkerframework.checker.javari.qual", QualifierLocation.TYPE),

    PolyRead("org.checkerframework.checker.javari.qual", QualifierLocation.TYPE),

    QReadOnly("org.checkerframework.checker.javari.qual",
            QualifierLocation.TYPE),

    ReadOnly("org.checkerframework.checker.javari.qual", QualifierLocation.TYPE),

    ThisMutable("org.checkerframework.checker.javari.qual",
            QualifierLocation.TYPE);

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
