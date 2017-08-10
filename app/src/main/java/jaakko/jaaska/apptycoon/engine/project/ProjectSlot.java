package jaakko.jaaska.apptycoon.engine.project;

/**
 * Representation of a project slot.
 *
 * Created by jaakko on 7.5.2017.
 */

public class ProjectSlot {

    /** Project that is currently occupying the slot. This is null when the slot is empty. */
    private Project mProject;

    /** The fraction of work that is provisioned for this project slot. A value between 0 and 1. */
    private double mWorkFraction;

    public ProjectSlot(Project project, double workFraction) {
        mProject = project;
        mWorkFraction = workFraction;
    }

    public boolean isFree() {
        return mProject == null;
    }

    public double getWorkFraction() {
        return mWorkFraction;
    }

    public void setWorkFraction(double workFraction) {
        mWorkFraction = workFraction;
    }

    public Project getProject() {
        return mProject;
    }

    public void setProject(Project project) {
        mProject = project;
    }

}
