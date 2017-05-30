package jaakko.jaaska.softwaretycoon.engine.project;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import jaakko.jaaska.softwaretycoon.engine.product.Product;

/**
 * Different types of actual projects need to extend this class and provide
 * project type specific functionality into the abstract methods.
 *
 * Created by jaakko on 26.3.2017.
 */

public abstract class Project {

    private static final String TAG = "Project";

    private String mName;
    private String mDescription;

    /** Amount of work needed to implement all features */
    private long mWorkAmount;

    /** Amount of work done for the project. */
    private long mWorkProgress;

    /** Time spent working on the project. In milliseconds */
    private long mTimeSpent;

    /** Work is applied to the project in somewhat random intervals
     * which means that the work done within an interval is not always
     * integral. Fractional progress is used to track this portion of the work
     * that exceeds the integer part of work done. It should be more than one
     * only momentarily.
      */
    private double mFractionalProgress;

    /**
     * List of project tasks.
     */
    private List<ProjectTask> mTasks;

    /**
     * Default constructor
     * @param name
     * @param description
     */
    public Project(String name, String description) {
        mName = name;
        mDescription = description;
        mWorkAmount = -1;
        mWorkProgress = 0;
        mFractionalProgress = 0.0f;
        mTimeSpent = 0;
        mTasks = new ArrayList<>();
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public boolean isFinished() {
        return mWorkProgress >= mWorkAmount;
    }

    public List<ProjectTask> getTasks() {
        return mTasks;
    }

    public void addTask(ProjectTask task) {
        mTasks.add(task);
    }

    /**
     * Progresses the project by the given amount.
     * @param amount Amount of work done for the project.
     * @param time Time passed in milliseconds.
     */
    public void progress(double amount, long time) {
        mFractionalProgress += amount;
        int intPart = (int) mFractionalProgress;
        mFractionalProgress -= intPart;
        mWorkProgress += intPart;
        mTimeSpent += time;

        /*
        Log.d(TAG, "progress() amount=" + amount
                + ", fractProg=" + mFractionalProgress
                + ", intPart=" + intPart
                + ", mWorkProgress=" + mWorkProgress);
                */
    }

    public long getWorkAmount() {
        // Calculate the needed work amount first time the work amount is requested.
        if (mWorkAmount < 0) {
            updateWorkAmount();
        }

        return mWorkAmount;
    }

    public long getTimeSpent() {
        return mTimeSpent;
    }

    public long getWorkProgress() {
        return mWorkProgress;
    }

    /** Updates the total work amount requirement. Call this when conditions affecting the work
     * amount change.
     */
    public void updateWorkAmount() {
        mWorkAmount = calculateWorkAmount();
    }

    /**
     * Calculates the total amount of work required in order to finish the project.
     * @return Calculated work amount
     */
    protected abstract long calculateWorkAmount();

    /**
     * Tells if the project is ready to be acknowledge as being finished.
     * @return True when project is ready to be acknowledge as being finished.
     */
    public abstract boolean isReady();

}
