package jaakko.jaaska.apptycoon.engine.people;

import jaakko.jaaska.apptycoon.utils.Utils;

/**
 * Created by jaakko on 17.4.2017.
 */

public class JobApplication {

    private String mName; // The person who is applying.
    private EmployeeType mJob; // The type of the job that is being applied.
    private long mExpiresIn; // How long until the application expires (and disappears from the system).

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
        ret.mName = Utils.getRandomName();
        ret.mJob = null;;

        // TODO: New applications now expire in two hours.
        ret.mExpiresIn = 2 * 60 * 60 * 1000;

        return ret;
    }
}
