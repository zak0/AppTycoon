package jaakko.jaaska.softwaretycoon.engine.time;

/**
 * Container work time specs.
 *
 * TODO: Fix case when work time passes a day change (e.g. day starts at 22 and lasts 6 hours).
 *
 * Created by jaakko on 9.4.2017.
 */

public class WorkTime {

    private static final int[] DEFAULT_DAILY_HOURS = {8, 8, 8, 8, 8, 0, 0};
    private static final int[] DEFAULT_DAILY_STARTING_TIMES = {9, 9, 9, 9, 9, -1, -1};


    /** Work time amounts for each day of the week.
     * Outer indexes are days from 0 to 6 (Mon to Sun). */
    private int[] mDailyHours;

    /** Work day starting times for each day of the week.
     * Outer indexes are days from 0 to 6 (Mon to Sun). */
    private int[] mDailyStartingTimes;


    public WorkTime() {
        mDailyHours = new int[7];
        mDailyStartingTimes = new int[7];

        // Use hardcoded defaults at init.
        resetToHardCodedDefaults();
    }

    /**
     * Resets working times to set defaults.
     */
    public void resetToHardCodedDefaults() {
        mDailyHours = DEFAULT_DAILY_HOURS;
        mDailyStartingTimes = DEFAULT_DAILY_STARTING_TIMES;
    }

    /**
     * Calculates total weekly work time.
     * @return Number of weekly working hours.
     */
    public int getWeeklyHours() {
        int ret = 0;
        for (int hours : mDailyHours) {
            ret += hours;
        }
        return ret;
    }

    /**
     * Checks if given GameTime is during work day.
     * @param gameTime Time to check against.
     * @return True if given GameTime is during work day. False otherwise.
     */
    public boolean atWork(GameTime gameTime) {
        int day = gameTime.getDayOfWeekIndex();

        if (mDailyHours[day] <= 0) {
            return false;
        }

        int workDayStart = mDailyStartingTimes[day];
        int workDayEnd = workDayStart + mDailyHours[day];

        return gameTime.getHour() >= workDayStart && gameTime.getHour() < workDayEnd;
    }

    /**
     * Set working time for a given day.
     * @param day Index of the to set. (Mon = 0, Sun = 6)
     * @param dayStart Hour when the day starts.
     * @param hours Number of working hours.
     */
    public void setDailyWorkTime(int day, int dayStart, int hours) {
        mDailyHours[day] = hours;
        mDailyStartingTimes[day] = dayStart;
    }

}
