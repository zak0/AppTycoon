package jaakko.jaaska.softwaretycoon.engine.people;

import android.util.SparseArray;

import java.util.Random;

import jaakko.jaaska.softwaretycoon.engine.Job;
import jaakko.jaaska.softwaretycoon.engine.Language;
import jaakko.jaaska.softwaretycoon.engine.time.GameTime;
import jaakko.jaaska.softwaretycoon.utils.Utils;

/**
 * Created by jaakko on 26.3.2017.
 */

public class Employee {

    private static final int MAX_AGE = 110;

    private String mName;
    private int mAge;
    private GameTime mBirthday;

    // Skills
    private int mTechSkill;
    private int mDesignSkill;
    private int mBusinessSkill;
    private int mPeopleSkill;

    // Known programming languages and respective proficiencies
    private SparseArray<Integer> mKnownLangs; // Pair<[language],[proficiency]>

    private Job mJob;
    private long mSalary;
    private double mMood;

    // Statistics
    private long mTotalPaid;
    private GameTime mHireDate;

    public Employee() {
        mName = "NoName";
        mAge = -1;
        mBirthday = new GameTime(0, 1, 1, 0); // Day 1, Month 1
        mTechSkill = 0;
        mDesignSkill = 0;
        mBusinessSkill = 0;
        mPeopleSkill = 0;
        mKnownLangs = new SparseArray<>();
    }

    /**
     * Constructor for a new Employee object.
     *
     * @param name
     * @param age
     * @param skills an int[] array with skills {tech, design, business, people}
     */
    public Employee(String name, int age, int[] skills) {
        mName = name;
        mAge = age;
        mTechSkill = skills[0];
        mDesignSkill = skills[1];
        mBusinessSkill = skills[2];
        mPeopleSkill = skills[3];
        mKnownLangs = new SparseArray<>();
    }

    /**
     * Sets a language proficiency level of given language to the given level.
     * If the person does not yet know the language, the new language is added
     * to the list.
     *
     * @param language
     * @param proficiency
     */
    public void setLanguage(int language, int proficiency) {
        mKnownLangs.put(language, proficiency);
    }

    /**
     * Returns a list of employee skill points as an array.
     * Array always has four entries in this specific order:
     * [tech skill], [design skill], [business skill], [people skill].
     * @return The list of skills.
     */
    public int[] getSkills() {
        int[] ret = {mTechSkill, mDesignSkill, mBusinessSkill, mPeopleSkill};
        return ret;
    }

    /**
     * Returns a SparseArray of all the programming
     * languages the person knows. List indices are language IDs,
     * list values are proficiencies.
     * @return The list
     */
    public SparseArray<Integer> getLanguageSkills() {
        return mKnownLangs;
    }

    /**
     * Generates a person with randomized attributes.
     *
     * @param skillMax upper limit for skill values
     * @return a random person
     */
    public static Employee generateRandomPerson(int skillMax) {
        final int minAge = 18;
        final int maxAge = 70;

        Random r = new Random(System.currentTimeMillis());

        int age = 18 + r.nextInt() % (maxAge - minAge);
        int[] skills = new int[4];

        for (int i = 0; i < 4; i++) {
            skills[i] = r.nextInt() % (skillMax + 1);
        }

        Employee ret = new Employee(Utils.getRandomName(), age, skills);

        // Add 0 to 4 programming languages the person know.
        int languagesCount = r.nextInt(5);
        for (int i = 0; i < languagesCount; i++) {
            ret.mKnownLangs.put(Language.getRandomLanguage(), Language.getRandomProficiency());
        }

        // Get a random birthday.
        ret.mBirthday = GameTime.getRandomGameTime(false, false, true, true, false);

        return ret;
    }

    public GameTime getHireDate() {
        return mHireDate;
    }

    public void setHireDate(GameTime hireDate) {
        mHireDate = hireDate;
    }

    public Job getJob() {
        return mJob;
    }

    /**
     * Sets the current job for the employee.
     * Salary of the employee is set to new job's base salary.
     * @param job
     */
    public void setJob(Job job) {
        // Set the current salary to new job's base salary.
        mSalary = job.getBaseSalary();
        mJob = job;
        calculateBaseMood();
    }

    /**
     * Sets the current job for the employee with a given salary.
     * Use this method when changing a job and setting a custom salary
     * instead of setting only the job and then calling setSalary().
     * (Doing it this way prevents the inappropriate salary change -mood effect.)
     * @param job
     * @param salary
     */
    public void setJob(Job job, long salary) {
        mJob = job;
        mSalary = salary;
        calculateBaseMood();
    }

    public long getSalary() {
        return mSalary;
    }

    public void setSalary(long salary) {
        mSalary = salary;
        // Calculate new base mood after the salary has changed
        calculateBaseMood();
        // TODO: Trigger a mood effect after a salary change
    }

    /**
     * Calculates the base mood based on the salary.
     */
    private void calculateBaseMood() {
        if (mJob == null) {
            mMood = 3.0;
            return;
        }

        mMood = (double) mSalary / (double) mJob.getBaseSalary() * 3.0;
    }

    /**
     * Checks if a date is the employee's birthday.
     * @param date Date to compare the birthday to.
     * @return true if yes, false if no
     */
    public boolean isBirthday(GameTime date) {
        return GameTime.sameMonthAndDay(date, mBirthday);
    }

    public GameTime getBirthday() {
        return mBirthday;
    }

}
