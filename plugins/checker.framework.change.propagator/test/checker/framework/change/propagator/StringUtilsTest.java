package checker.framework.change.propagator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void test1() {
        assertEquals(
                "bb",
                StringUtils.enclosingLine(
                        Substring.indexBasedSubstring("a\nbb\nccc", 2, 3))
                        .toString());
    }

    @Test
    public void test2() {
        assertEquals(
                "bb",
                StringUtils.enclosingLine(
                        Substring.indexBasedSubstring("a\nbb\nccc", 2, 4))
                        .toString());
    }

    @Test
    public void test3() {
        assertEquals(
                "a",
                StringUtils.enclosingLine(
                        Substring.indexBasedSubstring("a\nbb\nccc", 0, 1))
                        .toString());
    }

    @Test
    public void test4() {
        assertEquals(
                "ccc",
                StringUtils.enclosingLine(
                        Substring.indexBasedSubstring("a\nbb\nccc", 6, 7))
                        .toString());
    }

    @Test
    public void test5() {
        assertEquals(
                "bb\nccc",
                StringUtils.enclosingLine(
                        Substring.indexBasedSubstring("a\nbb\nccc", 3, 6))
                        .toString());
    }

}
