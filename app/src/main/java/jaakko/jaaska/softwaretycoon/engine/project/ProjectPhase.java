package jaakko.jaaska.softwaretycoon.engine.project;

/**
 * Development phase of a project.
 *
 * This class tracks the progress of a phase in a project. The phase is one phase in the lifecycle
 * that is defined by the selected development model.
 *
 * Created by jaakko on 15.4.2017.
 */

public class ProjectPhase {

    private DevPhase mDevPhase; // Which phase of the selected model this is.
    private double mWorkNeeded; // How much work needs to be applied in order to complete this phase.
    private double mWorkDone; // How much work is done for this phase.

    private ProjectPhase mNextPhase; // The phase that comes after this one. This is null for the last phase.

    public ProjectPhase(DevPhase devPhase, double workNeeded) {
        mDevPhase = devPhase;
        mWorkNeeded = workNeeded;
        mWorkDone = 0.0f;
    }

    public ProjectPhase setNextPhase(ProjectPhase nextPhase) {
        mNextPhase = nextPhase;
        return this;
    }
}
