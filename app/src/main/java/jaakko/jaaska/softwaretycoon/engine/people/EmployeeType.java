package jaakko.jaaska.softwaretycoon.engine.people;

import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import jaakko.jaaska.softwaretycoon.utils.Utils;

/**
 * Created by jaakko on 30.4.2017.
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
    private int mType = Integer.MIN_VALUE; // ID of the type
    private double mSalary = 0; // Salary for one employee of this type (money per second).
    private long mCount = 0; // Number of employees of this type
    private boolean mIsUnique = false; // True if there can only be one employee of this type
    private int mCpsGain = 0; // Cps gain one employee of this type brings
    private int mQualityGain = 0; // Quality score gain one employee of this type brings
    private List<String> mNames = new ArrayList<>(); // Names of the employees of this type

    /**
     * @param typeId
     * @param title
     * @param salary Salary per second
     * @param isUnique
     * @param cpsGain
     * @param qualityGain
     */
    private EmployeeType(int typeId, String title, double salary, boolean isUnique, int cpsGain, int qualityGain) {
        mTitle = title;
        mType = typeId;
        mSalary = salary;
        mIsUnique = isUnique;
        mCpsGain = cpsGain;
        mQualityGain = qualityGain;
    }

    /**
     * This calls the other constructor first but then converts the given yearly salary to salary per second
     * as defined in in the mSalary field. This is just to help inputting the static game data.
     * @param typeId
     * @param title
     * @param yearlySalary Salary per year (in real life).
     * @param isUnique
     * @param cpsGain
     * @param qualityGain
     */
    private EmployeeType(int typeId, String title, int yearlySalary, boolean isUnique, int cpsGain, int qualityGain) {
        this(typeId, title, 0.0f, isUnique, cpsGain, qualityGain);
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


    public long getCount() {
        return mCount;
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
        mCount += count;
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

        sStaticTypes.append(TYPE_DEVELOPER, new EmployeeType(TYPE_DEVELOPER, "Software Developer", 44000, false, 2, 1));
        sStaticTypes.append(TYPE_SENIOR_DEVELOPER, new EmployeeType(TYPE_SENIOR_DEVELOPER, "Senior Software Developer", 55000, false, 3, 2));
        sStaticTypes.append(TYPE_TEST_ENGINEER, new EmployeeType(TYPE_TEST_ENGINEER, "Software Test Engineer", 39000, false, 0, 2));
    }

    public static EmployeeType getTypeForId(int employeeTypeId) {
        initStaticData();
        return sStaticTypes.get(employeeTypeId);
    }




}
