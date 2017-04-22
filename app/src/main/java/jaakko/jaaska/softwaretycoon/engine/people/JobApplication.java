package jaakko.jaaska.softwaretycoon.engine.people;

import jaakko.jaaska.softwaretycoon.engine.Job;
import jaakko.jaaska.softwaretycoon.engine.core.GameEngine;
import jaakko.jaaska.softwaretycoon.engine.time.GameTime;

/**
 * Created by jaakko on 17.4.2017.
 */

public class JobApplication {

    private Employee mEmployee; // The person who is applying.
    private Job mJob; // The job that is being applied. Null for open applications.
    private long mSalaryRequest; // -1 for open applications.
    private GameTime mExpireTime; // When the application expires (and disappears from the system).

    /**
     * Instances are accessed through static getters.
     */
    private JobApplication() {

    }

    public boolean isOpenApplication() {
        return mJob == null;
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

        // TODO: New applications now expire in two months.
        ret.mExpireTime = GameEngine.getInstance().getGameState().getGameTime().duplicate();
        GameTime twoMonths = new GameTime(0, 0, 2, 0);
        ret.mExpireTime.add(twoMonths);

        return ret;
    }
}
