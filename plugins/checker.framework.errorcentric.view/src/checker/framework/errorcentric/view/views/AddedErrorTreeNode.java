package checker.framework.errorcentric.view.views;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import checker.framework.change.propagator.ComparableMarker;

import com.google.common.base.Function;

public class AddedErrorTreeNode extends ErrorTreeNode {

    public AddedErrorTreeNode(ComparableMarker marker) {
        super(marker);
    }

    public static Set<AddedErrorTreeNode> createTreeNodesFrom(
            Set<ComparableMarker> markers) {
        return newHashSet(transform(markers,
                new Function<ComparableMarker, AddedErrorTreeNode>() {
                    @Override
                    public AddedErrorTreeNode apply(ComparableMarker marker) {
                        return new AddedErrorTreeNode(marker);
                    }
                }));
    }

}
