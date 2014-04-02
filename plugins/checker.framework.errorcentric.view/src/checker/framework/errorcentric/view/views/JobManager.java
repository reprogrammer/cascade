package checker.framework.errorcentric.view.views;

import java.util.HashSet;
import java.util.Set;

public class JobManager {
    private static Set<TreeObject> finished = new HashSet<TreeObject>();

    public static void done(TreeObject t) {
        finished.add(t);
    }

    public static boolean isDone(Object o) {
        return finished.contains(o);
    }
}
