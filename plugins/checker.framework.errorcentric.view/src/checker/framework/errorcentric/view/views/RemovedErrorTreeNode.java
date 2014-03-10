package checker.framework.errorcentric.view.views;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import checker.framework.change.propagator.ComparableMarker;

import com.google.common.base.Function;

public class RemovedErrorTreeNode extends ErrorTreeNode {

    public RemovedErrorTreeNode(ComparableMarker marker) {
        super(marker);
    }

    public static Set<RemovedErrorTreeNode> createTreeNodesFrom(
            Set<ComparableMarker> markers) {
        return newHashSet(transform(markers,
                new Function<ComparableMarker, RemovedErrorTreeNode>() {
                    @Override
                    public RemovedErrorTreeNode apply(ComparableMarker marker) {
                        return new RemovedErrorTreeNode(marker);
                    }
                }));
    }

}
