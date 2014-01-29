package checker.framework.change.propagator;

public enum CheckerID {

    NULLNESS("checkers.nullness.NullnessChecker"), JAVARI(
            "checkers.javari.JavariChecker");

    private final String id;

    CheckerID(String id) {
        this.id = id;
    }

    String getId() {
        return id;
    }

}
