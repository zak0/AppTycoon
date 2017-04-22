package jaakko.jaaska.softwaretycoon.engine.time;

import java.util.Random;

/**
 * Created by jaakko on 7.3.2017.
 *
 * Represents time in the game.
 */

public class GameTime {

    public static final String[] DAYS_OF_WEEK_LONG = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    public static final String[] DAYS_OF_WEEK_SHORT = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    public static final int MONDAY = 0;
    public static final int TUESDAY = 1;
    public static final int WEDNESDAY = 2;
    public static final int THURSDAY = 3;
    public static final int FRIDAYDAY = 4;
    public static final int SATURDAY = 5;
    public static final int SUNDAY = 6;

    private long mFraction; // Fraction is the progress of the current hour in milliseconds.
                           // I.e. a value within range [0, 3 599 999[

    private int mHour; // The lowest unit visible to the player. Hour is also the "base" unit.
    private int mDay; // 1 day = 24 hours
    private int mMonth; // 1 month = 30 days
    private int mYear; // 1 year = 12 months


    public GameTime(long fraction, int hour, int day, int month, int year) {
        this.mFraction = fraction;
        this.mHour = hour;
        this.mDay = day;
        this.mMonth = month;
        this.mYear = year;

        cleanUp();
    }

    public GameTime(int hour, int day, int month, int year) {
        this.mFraction = 0l;
        this.mHour = hour;
        this.mDay = day;
        this.mMonth = month;
        this.mYear = year;

        cleanUp();
    }

    public long getFraction() {
        return mFraction;
    }

    public int getHour() {
        return mHour;
    }

    public int getDay() {
        return mDay;
    }

    public int getMonth() {
        return mMonth;
    }

    public int getYear() {
        return mYear;
    }

    /**
     * Adds another GameTime to the current one resulting current time to be advanced by the amount
     * of the another GameTiḿe object.
     *
     * @param gt Another GameTime object to add to this one.
     */
    public void add(GameTime gt) {
        this.mFraction += gt.getFraction();
        this.mHour += gt.getHour();
        this.mDay += gt.getDay();
        this.mMonth += gt.getMonth();
        this.mYear += gt.getYear();

        this.cleanUp();
    }

    public void add(long fraction, int hour, int day, int month, int year) {
        this.mFraction += fraction;
        this.mHour += hour;
        this.mDay += day;
        this.mMonth += month;
        this.mYear += year;

        this.cleanUp();
    }

    /**
     * "Cleans" the GameTime after operations that might have left the member variables with
     * impossible values (like 45 days).
     */
    private void cleanUp() {

        while (this.mFraction >= 3600000) {
            this.mFraction -= 3600000;
            this.mHour += 1;
        }

        while (this.mHour > 23) {
            this.mHour -= 24;
            this.mDay += 1;
        }

        while (this.mDay > 30) {
            this.mDay -= 30;
            this.mMonth += 1;
        }

        while (this.mMonth > 12) {
            this.mMonth -= 12;
            this.mYear += 1;
        }
    }

    /**
     * Returns string presentation of current day of the week.
     *
     * @param longForm True returns full textual day (e.g. "Monday"). False returns the short form (e.g. "Mon").
     * @return The day of the week in wanted form.
     */
    public String getDayOfWeekString(boolean longForm) {
        int day = getDayOfWeekIndex();
        return longForm ? DAYS_OF_WEEK_LONG[day] : DAYS_OF_WEEK_SHORT[day];
    }

    /**
     * Returns the index number of current day of the week.
     * Monday is 0, Sunday is 6.
     * @return Index of the day.
     */
    public int getDayOfWeekIndex() {
        return ((mYear - 1) * 12 * 30
                + (mMonth - 1) * 30
                + (mDay - 1)) % 7;
    }

    @Override
    public String toString() {
        return "Y" + mYear + " M" + mMonth + " D" + mDay + " H" + mHour;
    }

    /**
     * Checks if the day and month of the two GameTime objects are equal.
     * Useful for example when checking for birthdays.
     *
     * @param a
     * @param b
     * @return true when a and b have the same month and day
     */
    public static boolean sameMonthAndDay(GameTime a, GameTime b) {
        return (a.getMonth() == b.getMonth()) && (a.getDay() == b.getDay());
    }

    /**
     * Generates GameTime object with randomized time values. Only the selected time parts are randomized,
     * while others are left as zero.
     *
     * @param rFraction fraction is randomized
     * @param rHour hour is randomized
     * @param rDay day is randomized
     * @param rMonth month is randomized
     * @param rYear year is randomized
     * @return a random GameTime
     */
    public static GameTime getRandomGameTime(boolean rFraction, boolean rHour, boolean rDay, boolean rMonth, boolean rYear) {
        Random r = new Random();

        long fraction = rFraction ? r.nextLong() % 3600000: 0;
        int hour = rHour ? r.nextInt(24) : 0;
        int day = rDay ? r.nextInt(31) : 0;
        int month = rMonth ? r.nextInt(13) : 0;
        int year = rYear ? r.nextInt(5000) : 0;

        return new GameTime(fraction, hour, day, month, year);
    }

    /**
     * Duplicates the current GameTime
     * @return Exact duplicate of a current GameTime
     */
    public GameTime duplicate() {
        return new GameTime(mFraction, mHour, mDay, mMonth, mYear);
    }
}
