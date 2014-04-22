package checker.framework.change.propagator;

public enum CheckerID {

    NULLNESS("org.checkerframework.checker.nullness.NullnessChecker"), JAVARI(
            "org.checkerframework.checker.javari.JavariChecker");

    private final String id;

    CheckerID(String id) {
        this.id = id;
    }

    String getId() {
        return id;
    }

}
