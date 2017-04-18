package jaakko.jaaska.softwaretycoon.engine.project;

/**
 * Created by jaakko on 15.4.2017.
 */

public class ProjectMilestone {

    /** The project this milestone is part of. */
    private Project mProject;

    /** How much the milestone pays. */
    private long mPayment;

    /** Percentage of the entire project that needs to be completed in order to reach this milestone. */
    private double mGoal;

    private String mDescription;

    /**
     * Constructor.
     *
     * @param project The project this milestone is part of.
     * @param payment How much the milestone pays.
     * @param goal Percentage of the entire project that needs to be completed in order to reach this milestone.
     * @param description A textual description of the milestone.
     */
    public ProjectMilestone(Project project, long payment, double goal, String description) {
        mProject = project;
        mPayment = payment;
        mGoal = goal;
        mDescription = description;
    }

}
