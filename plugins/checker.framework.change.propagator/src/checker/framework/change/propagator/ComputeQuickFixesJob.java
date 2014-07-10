package checker.framework.change.propagator;

import org.checkerframework.eclipse.actions.CheckerWorker;
import org.checkerframework.eclipse.util.MutexSchedulingRule;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class ComputeQuickFixesJob extends Job {

    private ShadowProject shadowProject;

    private String checkerID;

    public ComputeQuickFixesJob(String name, ShadowProject shadowProject,
            String checkerID) {
        super(name);
        this.shadowProject = shadowProject;
        this.checkerID = checkerID;
    }

    private String[] getShadowSourceFiles() {
        return shadowProject.getSourceFiles().toArray(new String[] {});
    }

    private CheckerWorker getCheckerWorker() {
        CheckerWorker checkerJob = new CheckerWorker(
                shadowProject.getProject(), getShadowSourceFiles(), checkerID);
        checkerJob.setUser(true);
        checkerJob.setPriority(Job.BUILD);
        checkerJob.setRule(new MutexSchedulingRule());
        return checkerJob;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        CheckerWorker checkerWorker = getCheckerWorker();
        checkerWorker.schedule();
        try {
            checkerWorker.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Status.OK_STATUS;
    }

}
