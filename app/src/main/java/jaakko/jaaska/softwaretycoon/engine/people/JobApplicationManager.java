package jaakko.jaaska.softwaretycoon.engine.people;

import java.util.ArrayList;
import java.util.List;

/**
 * JobApplicationManager stores and handles job applications.
 *
 * This is a singleton.
 * Created by jaakko on 19.4.2017.
 */

public class JobApplicationManager {

    private List<JobApplication> mOpenApplications; // List of all pending open applications
    private List<JobApplication> mRegularApplications; // List of "regular" (i.e. to a specific position) applications

    private static JobApplicationManager mInstance;

    private JobApplicationManager() {
        mOpenApplications = new ArrayList<>();
        mRegularApplications = new ArrayList<>();
    }

    public static JobApplicationManager getInstance() {
        if (mInstance == null) {
            mInstance = new JobApplicationManager();
        }
        return mInstance;
    }

    /**
     * Adds an open application into the system. If the application param is set to null, a new
     * random open application is generated.
     *
     * @param application Open application to add. If null, a random open application is generated.
     */
    public void newOpenApplication(JobApplication application) {
        JobApplication newApplication = application;

        if (application == null) {
            newApplication = JobApplication.getRandomOpenApplication();
        }

        mOpenApplications.add(newApplication);
    }

}
