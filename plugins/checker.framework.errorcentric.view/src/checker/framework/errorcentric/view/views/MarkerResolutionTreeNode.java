package checker.framework.errorcentric.view.views;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Sets.difference;
import static com.google.common.collect.Sets.newHashSet;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import checker.framework.change.propagator.ActionableMarkerResolution;
import checker.framework.change.propagator.ComparableMarker;
import checker.framework.change.propagator.ShadowProject;
import checker.framework.errorcentric.propagator.commands.InferCommandHandler;
import checker.framework.quickfixes.WorkspaceUtils;
import checker.framework.quickfixes.descriptors.FixerDescriptor;

import com.google.common.base.Predicate;

public class MarkerResolutionTreeNode extends TreeObject {

    private ActionableMarkerResolution resolution;

    public MarkerResolutionTreeNode(ActionableMarkerResolution resolution) {
        super(resolution.getLabel());
        this.resolution = resolution;
    }

    public ActionableMarkerResolution getResolution() {
        return resolution;
    }

    public boolean hasChildren() {
        return true;
    }

    private List<FixerDescriptor> getParentFixerDescriptors() {
        LinkedList<FixerDescriptor> fixerDescriptors = new LinkedList<>();
        TreeObject parent = getParent();
        while (parent != null) {
            if (parent instanceof MarkerResolutionTreeNode) {
                fixerDescriptors.addLast(((MarkerResolutionTreeNode) parent)
                        .getResolution().getFixerDescriptor());
            }
            parent = parent.getParent();
        }
        return fixerDescriptors;
    }

    public TreeObject[] getChildren() {
        final List<FixerDescriptor> parentFixerDescriptors = getParentFixerDescriptors();
        resolution.getShadowProject().updateToPrimaryProjectWithChanges(
                parentFixerDescriptors);
        resolution.apply();
        WorkspaceUtils.saveAllEditors();
        ShadowProject shadowProject = resolution.getShadowProject();
        shadowProject.runChecker(InferCommandHandler.checkerID);

        Set<ComparableMarker> allMarkersAfterResolution = shadowProject
                .getMarkers();
        Set<ComparableMarker> addedMarkers = difference(
                allMarkersAfterResolution,
                resolution.getAllMarkersBeforeResolution());
        Set<ActionableMarkerResolution> newResolutions = shadowProject
                .getResolutions(allMarkersAfterResolution, addedMarkers);
        HashSet<ActionableMarkerResolution> historicallyNewResolutions = newHashSet(filter(
                newResolutions, new Predicate<ActionableMarkerResolution>() {
                    @Override
                    public boolean apply(
                            ActionableMarkerResolution newResolution) {
                        FixerDescriptor fixerDescriptor = newResolution
                                .getFixerDescriptor();
                        return !resolution.getFixerDescriptor().equals(
                                fixerDescriptor)
                                && !parentFixerDescriptors
                                        .contains(fixerDescriptor);
                    }
                }));
        addChildren(AddedErrorTreeNode
                .createTreeNodesFrom(historicallyNewResolutions));
        return super.getChildren();
    }

}
