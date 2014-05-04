package checker.framework.change.propagator;

/**
 * Represents the substring [beginIndex, endIndex) of string.
 *
 */
public class Substring {

    private final String enclosingString;

    private final int beginIndex;

    private final int endIndex;

    private Substring(String string, int beginIndex, int endIndex) {
        this.enclosingString = string;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
    }

    public static Substring indexBasedSubstring(String string, int beginIndex,
            int endIndex) {
        return new Substring(string, beginIndex, endIndex);
    }

    public String getEnclosingString() {
        return enclosingString;
    }

    public int getBeginIndex() {
        return beginIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public int getLength() {
        return endIndex - beginIndex;
    }

    @Override
    public String toString() {
        return enclosingString.substring(beginIndex, endIndex);
    }

}
