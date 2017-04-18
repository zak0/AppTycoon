package jaakko.jaaska.softwaretycoon.engine.people;

import jaakko.jaaska.softwaretycoon.engine.Job;

/**
 * Created by jaakko on 17.4.2017.
 */

public class JobApplication {

    private Employee mEmployee; // The person who is applying.
    private Job mJob; // The job that is being applied. Null for open applications.
    private long mSalaryRequest; // -1 for open applications.

    /**
     * Instances are accessed through static getters.
     */
    private JobApplication() {

    }

    /**
     * Generates a random open application.
     * @return
     */
    public static JobApplication getRandomOpenApplication() {
        JobApplication ret = new JobApplication();
        ret.mEmployee = Employee.generateRandomPerson(20);
        ret.mJob = null;
        ret.mSalaryRequest = -1;

        return ret;
    }
}
