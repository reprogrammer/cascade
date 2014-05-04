package checker.framework.change.propagator;

public class StringUtils {

    /**
     * 
     * @param text
     * @param beginIndex
     * @param endIndex
     * @return the largest substring of text that contains [beginIndex,
     *         endIndex) and doesn't include any new line characters.
     */
    static Substring enclosingLine(Substring substring) {
        String separator = System.getProperty("line.separator");
        String adjustedText = '\n' + substring.getEnclosingString() + '\n';
        int adjustedBeginIndex = substring.getBeginIndex() + 1;
        int adjustedEndIndex = substring.getEndIndex() + 1;
        int indexOfPreviousSeparator = adjustedText.substring(0,
                adjustedBeginIndex).lastIndexOf(separator);
        int indexOfNextSeparator = adjustedText.indexOf(separator,
                adjustedEndIndex);
        return Substring.indexBasedSubstring(substring.getEnclosingString(),
                indexOfPreviousSeparator, indexOfNextSeparator - 1);
    }

}
