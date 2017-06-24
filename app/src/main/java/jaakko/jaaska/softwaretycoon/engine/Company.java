package jaakko.jaaska.softwaretycoon.engine;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jaakko.jaaska.softwaretycoon.engine.core.GameEngine;
import jaakko.jaaska.softwaretycoon.engine.people.EmployeeType;
import jaakko.jaaska.softwaretycoon.engine.product.Product;
import jaakko.jaaska.softwaretycoon.engine.project.ContractingProject;
import jaakko.jaaska.softwaretycoon.engine.project.Project;
import jaakko.jaaska.softwaretycoon.engine.project.ProjectSlot;

/**
 * Created by jaakko on 7.3.2017.
 */

public class Company {

    private static final String TAG = "Company";

    private String mName;
    private int mReputation;
    private long mValue;
    private long mFunds;

    private double mSalaryCosts; // Cumulative salary costs (per second)

    private List<EmployeeType> mEmployees;
    private long mCps = 0; // Cumulative cps of all employees and company assets
    private long mQuality = 0; // Cumulative quality score of all employees and assets

    /** All products of the company. */
    private List<Product> mProducts;

    /** Project slots */
    private List<ProjectSlot> mProjectSlots;

    public Company(String name, int reputation, long value, long funds) {
        mName = name;
        mReputation = reputation;
        mValue = value;
        mFunds = funds;

        mEmployees = new ArrayList<>();
        mProducts = new ArrayList<>();
        mProjectSlots = new ArrayList<>();
    }

    public String getName() {
        return mName;
    }

    public int getReputation() {
        return mReputation;
    }

    public long getValue() {
        return mValue;
    }

    public void addFunds(long amount) {
        mFunds += amount;
    }

    public long getFunds() {
        return mFunds;
    }

    public long getCps() {
        return mCps;
    }

    public double getQualityRatio() {
        return (double) mQuality / (double) mCps;
    }

    /**
     * Applies work to project slots. The amount of work is divided between the slots
     * according to the division set by the player.
     * @param amount Amount of work done
     * @param time Time passed
     */
    public void doWork(double amount, long time) {
        //Log.d(TAG, "doWork() - workAmount = " + amount);

        for (ProjectSlot slot : mProjectSlots) {
            if (slot.getProject() == null) {
                continue;
            }

            Project project = slot.getProject();

            // Progress contracting projects only if they have time left.
            // I.e. they are marked ready to be finished.
            if (project instanceof ContractingProject && !project.isReady()) {
                project.progress(amount * slot.getWorkFraction(), // workAmount
                        amount * slot.getWorkFraction() * getQualityRatio(), // qualityAmount
                        time); // time
            }

        }
    }

    /**
     * Adds a new employee into the company. Takes care of handling changes in periodic expenses
     * as well as changes in company productivity. So this is what to call when adding a new
     * employee to the company.
     *
     * @param type Type of the employee to add.
     * @param count How many employees of this type to add.
     */
    public void addEmployee(int type, int count) {

        // The employee data is stored in an EmployeeType.
        // Get the type from the list if employees of this type are already hired before.
        EmployeeType employeeType = null;

        for (EmployeeType existingType : mEmployees) {
            if (existingType.getType() == type) {
                employeeType = existingType;
            }
        }

        if (employeeType == null) {
            Log.d(TAG, "addEmployee() - adding first employee of type " + type + ".");
            employeeType = EmployeeType.getTypeForId(type);
            mEmployees.add(employeeType);
        }

        mCps += employeeType.getCpsGain() * count;
        mQuality += employeeType.getQualityGain() * count;
        mSalaryCosts += employeeType.getSalary() * count;
        employeeType.hire(count);
    }

    /**
     * Calculates the headcount of all employees.
     * @return Number of employees.
     */
    public long getEmployeeCount() {
        long count = 0;
        for (EmployeeType type : mEmployees) {
            count += type.getCount();
        }
        return count;
    }

    public void addProjectSlot() {
        ProjectSlot newSlot = new ProjectSlot(null, 1);

        // The initial work fraction for a new slot 1 / number of slots.
        int slotsCount = mProjectSlots.size();
        newSlot.setWorkFraction(1.0f / (double) slotsCount);

        // TODO: Take the work amount needed for the new slot equally from other slots.
        // As a temporary solution just reset all slots to equal fractions.
        double equalFraction = 1.0f / (double) slotsCount;

        for (ProjectSlot slot : mProjectSlots) {
            slot.setWorkFraction(equalFraction);
        }


        mProjectSlots.add(newSlot);

    }

    /**
     *
     * @return Running costs per second.
     */
    public double getRunningCosts() {
        return  mSalaryCosts;
    }

    public List<ProjectSlot> getProjectSlots() {
        return mProjectSlots;
    }
}
