package checker.framework.errorcentric.view.views;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.Job;

import checker.framework.change.propagator.ActionableMarkerResolution;
import checker.framework.change.propagator.ComparableMarker;
import checker.framework.quickfixes.descriptors.FixerDescriptor;

public class MarkerResolutionTreeNode extends TreeObject {

    private ActionableMarkerResolution resolution;

    private Job job;
    private volatile int errorsFixed;

    public void setErrorsFixed(int errorsFixed) {
        this.errorsFixed = errorsFixed;
    }

    private Set<ComparableMarker> unresolvableMarkers;

    public MarkerResolutionTreeNode(ActionableMarkerResolution resolution) {
        super(resolution.getLabel());
        this.resolution = resolution;
        this.unresolvableMarkers = new HashSet<>();
    }

    public ActionableMarkerResolution getResolution() {
        return resolution;
    }

    public List<FixerDescriptor> getParentFixerDescriptors() {
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

    public void computeChangeEffect() {
        String progressBarLabel = String.format("Computing the effect of: %s",
                resolution.getLabel());
        job = new ChangeComputationJob(progressBarLabel, this);
        job.setRule(ResourcesPlugin.getWorkspace().getRoot());
        job.setPriority(Job.LONG);
        job.schedule();
    }

    public TreeObject[] getChildren() {
        try {
            job.join();
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
        return super.getChildren();
    }

    @Override
    public int getRank() {
        return getErrorsFixed();
    }

    public int getErrorsFixed() {
        return errorsFixed;
    }

    public Set<ComparableMarker> getUnresolvableMarkers() {
        return unresolvableMarkers;
    }

    @Override
    public int hashCode() {
        return resolution.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MarkerResolutionTreeNode)) {
            return false;
        }
        ErrorTreeNode myParent = (ErrorTreeNode) getParent();
        MarkerResolutionTreeNode theirNode = (MarkerResolutionTreeNode) obj;
        ErrorTreeNode theirParent = (ErrorTreeNode) theirNode.getParent();
        return resolution.equals(theirNode.resolution)
                && ((myParent == null && theirParent == null) || myParent
                        .equals(theirParent));
    }
}
