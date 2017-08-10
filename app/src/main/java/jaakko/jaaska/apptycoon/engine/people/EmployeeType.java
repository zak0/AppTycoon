package jaakko.jaaska.apptycoon.engine.people;

import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import jaakko.jaaska.apptycoon.utils.Utils;

/**
 * Class that defines the employees.
 *
 * The class also acts as a container for static employee data (different types, base salaries, ...)
 * and thus no new instances are created outside of this class. Instead, an instance of a wanted
 * employee type is acquired through the static getTypeForId() method.
 */

public class EmployeeType {

    private static final String TAG = "EmployeeType";

    // Type IDs
    public static final int TYPE_DEVELOPER = 1;
    public static final int TYPE_SENIOR_DEVELOPER = 2;

    public static final int TYPE_TEST_ENGINEER = 50;
    public static final int TYPE_SENIOR_TEST_ENGINEER = 51;

    public static final int TYPE_CEO = 1234;

    private String mTitle = ""; // Title of the job position that this type represents
    private String mDescription = ""; // A short textual description of the employee type.
    private int mType = Integer.MIN_VALUE; // ID of the type
    private double mSalary = 0; // Salary for one employee of this type (money per second).
    private int mCount = 0; // Number of employees of this type
    private boolean mIsUnique = false; // True if there can only be one employee of this type
    private int mCpsGain = 0; // Cps gain one employee of this type brings
    private int mQualityGain = 0; // Quality score gain one employee of this type brings
    private double mMorale = 1.0; // TODO: Possible future development
    private long mHireCost = 0; // TODO: Possible future development
    private List<String> mNames = new ArrayList<>(); // Names of the employees of this type

    /**
     * Use this constructor when initializing the static data.
     *
     * @param typeId A numeric constant ID of the employee type.
     * @param title A textual human-readable title for this job.
     * @param salary Salary per second.
     * @param isUnique Flag for telling if there can be only one employee of this type.
     * @param cpsGain Amount of code per second one employee of this type produces.
     * @param qualityGain Amount of quality one employee of this type produces.
     */
    private EmployeeType(int typeId, String title, double salary, boolean isUnique, int cpsGain, int qualityGain, String description) {
        mTitle = title;
        mType = typeId;
        mSalary = salary;
        mIsUnique = isUnique;
        mCpsGain = cpsGain;
        mQualityGain = qualityGain;
        mDescription = description;
    }

    /**
     * This calls the other constructor first but then converts the given yearly salary to salary per second
     * as defined in in the mSalary field. This is just to help inputting the static game data.
     *
     * @param typeId A numeric constant ID of the employee type.
     * @param title A textual human-readable title for this job.
     * @param yearlySalary Salary per year (in real life, not in game time).
     * @param isUnique Flag for telling if there can be only one employee of this type.
     * @param cpsGain Amount of code per second one employee of this type produces.
     * @param qualityGain Amount of quality one employee of this type produces.
     */
    private EmployeeType(int typeId, String title, int yearlySalary, boolean isUnique, int cpsGain, int qualityGain, String description) {
        this(typeId, title, 0.0f, isUnique, cpsGain, qualityGain, description);
        mSalary = ((double) yearlySalary) / 12.0f / 158.0f / 3600.0f;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getType() {
        return mType;
    }

    public double getSalary() {
        return mSalary;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getCount() {
        return mCount;
    }

    /**
     * Sets the count of employees of this type.
     * Do not use this when adding or removing employees via a game activity (i.e. hiring/firing).
     * Use only when restoring data from storage.
     *
     * @param count Count of employees of this type.
     */
    public void setCount(int count) {
        mCount = count;
    }

    public boolean isUnique() {
        return mIsUnique;
    }

    public int getCpsGain() {
        return mCpsGain;
    }

    public int getQualityGain() {
        return mQualityGain;
    }

    public void hire(int count) {
        mCount += count;

        // Add the names of employees into the list.
        for (int i = 0; i < count; i++) {
            String name = Utils.getRandomName();
            Log.d(TAG, "hire()- hired '" + name + "' as a '" + mTitle + "'");
            mNames.add(name);
        }
    }

    public void fire(int count) {
        mCount -= count;
        mCount = mCount < 0 ? 0 : mCount;
    }

    /** Static employee type data. I.e. the default values. */
    private static SparseArray<EmployeeType> sStaticTypes;

    private static void initStaticData() {
        // Init only once.
        if (sStaticTypes != null) {
            Log.e(TAG, "Tried to init statics again.");
            return;
        }

        sStaticTypes = new SparseArray<>();

        sStaticTypes.append(TYPE_DEVELOPER, new EmployeeType(TYPE_DEVELOPER, "Software Developer", 44000, false, 2, 1, "An all-round developer grunt."));
        sStaticTypes.append(TYPE_SENIOR_DEVELOPER, new EmployeeType(TYPE_SENIOR_DEVELOPER, "Senior Software Developer", 55000, false, 3, 2, "A developer with excellent skills and years of experience."));
        sStaticTypes.append(TYPE_TEST_ENGINEER, new EmployeeType(TYPE_TEST_ENGINEER, "Software Test Engineer", 39000, false, 0, 2, "Purpose of a test engineer is to break everything."));
    }

    public static EmployeeType getTypeForId(int employeeTypeId) {
        initStaticData();
        return sStaticTypes.get(employeeTypeId);
    }




}
