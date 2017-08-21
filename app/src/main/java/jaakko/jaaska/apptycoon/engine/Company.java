package jaakko.jaaska.apptycoon.engine;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import jaakko.jaaska.apptycoon.engine.people.EmployeeType;
import jaakko.jaaska.apptycoon.engine.product.Product;
import jaakko.jaaska.apptycoon.engine.project.Project;
import jaakko.jaaska.apptycoon.engine.project.ProjectSlot;

/**
 * Class representing a player company.
 */

public class Company {

    private static final String TAG = "Company";

    private String mName;
    private int mReputation;
    private long mValue;

    private long mFunds;

    /** Stores unpaid fractions of costs. Funds are only tracked as integers. One full unit of
     * funds is reduced when sum of current fraction and fractional part of next payment goes
     * above 1. Value stored here should always stay below 1.
     */
    private double mFractionOfCost;


    private double mSalaryCosts; // Cumulative salary costs (per second)

    private List<EmployeeType> mEmployees;
    private long mCodePerSec = 0; // Cumulative cps of all employees and company assets
    private long mQualityPerSec = 0; // Cumulative quality score of all employees and assets

    /** All products of the company. */
    private List<Product> mProducts;

    /** Project slots */
    private List<ProjectSlot> mProjectSlots;

    /**
     * Use this constructor when creating a new Company object for an entirely
     * new game.
     *
     * @param name Name of the company.
     * @param reputation Initial company reputation.
     * @param value Initial company value.
     * @param funds Initial company funds.
     */
    public Company(String name, int reputation, long value, long funds) {
        mName = name;
        mReputation = reputation;
        mValue = value;
        mFunds = funds;
        mFractionOfCost = 0.0f;

        mEmployees = new ArrayList<>();
        mProducts = new ArrayList<>();
        mProjectSlots = new ArrayList<>();
    }

    /**
     * Use this constructor when loading and existing Company object from storage.
     *
     * Be sure to set Employee, Product and ProjectSlot data after this as this
     * only initializes these lists.
     *
     * @param name Name of the company.
     * @param reputation Reputation value of the company.
     * @param value Monetary value of the company.
     * @param funds Funds
     * @param codePerSec Amount of code per second.
     * @param qualityPerSec Amount of quality per second.
     * @param salaryCosts Salary costs.
     */
    public Company(String name, int reputation, long value, long funds,
                   long codePerSec, long qualityPerSec, double salaryCosts) {
        this(name, reputation, value, funds);

        mCodePerSec = codePerSec;
        mQualityPerSec = qualityPerSec;
        mSalaryCosts = salaryCosts;
    }

    public List<Product> getProducts() {
        return mProducts;
    }

    public void addProduct(Product product) {
        mProducts.add(product);
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
        return mCodePerSec;
    }

    public double getQualityRatio() {
        return (double) mQualityPerSec / (double) mCodePerSec;
    }

    /**
     * Applies work to project slots. The amount of work is divided between the slots
     * according to the division set by the player.
     * @param amount Amount of work done
     * @param time Time passed
     */
    public void doWork(double amount, long time) {
        for (ProjectSlot slot : mProjectSlots) {
            if (slot.getProject() == null) {
                continue;
            }

            Project project = slot.getProject();
            project.progress(amount * slot.getWorkFraction(), // workAmount
                    amount * slot.getWorkFraction() * getQualityRatio(), // qualityAmount
                    time); // time
        }
    }

    /**
     * Reduces running costs from funds for the time period that has passed.
     *
     * @param time Length of time period in milliseconds for which the costs are payed for.
     */
    public void payRunningCosts(long time) {

        // Costs for the time period.
        double costs = ((double) time) * getRunningCosts() / 1000.0f;
        long integralPart = (long) costs;

        mFractionOfCost += costs - (double) integralPart;

        if (mFractionOfCost >= 1.00f) {
            integralPart++;
            mFractionOfCost = 0.0f;
        }

        mFunds -= integralPart;
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

        mCodePerSec += employeeType.getCpsGain() * count;
        mQualityPerSec += employeeType.getQualityGain() * count;
        mSalaryCosts += employeeType.getSalary() * count;
        employeeType.hire(count);
    }

    /**
     * Removes an employee from the company. Takes care of handling changes in periodic expenses
     * as well as changes in company productivity. So this is what to call when removing an
     * employee from the company.
     *
     * @param type Type of the employee to add.
     * @param count Number of employees of this type to add.
     */
    public void removeEmployee(int type, int count) {
        EmployeeType employeeType = null;

        for (EmployeeType existingType : mEmployees) {
            if (existingType.getType() == type) {
                employeeType = existingType;
            }
        }

        if (employeeType == null) {
            Log.d(TAG, "removeEmployee() - trying to remove non-existing employee.");
            return;
        }

        // Do not let the employee count get negative.
        int actualRemovedCount = count > employeeType.getCount() ? employeeType.getCount() : count;
        mCodePerSec -= employeeType.getCpsGain() * actualRemovedCount;
        mQualityPerSec -= employeeType.getQualityGain() * actualRemovedCount;
        mSalaryCosts -= employeeType.getSalary() * actualRemovedCount;
        employeeType.fire(actualRemovedCount);
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

    public List<EmployeeType> getEmployees() {
        return mEmployees;
    }

    public void setEmployees(List<EmployeeType> employees) {
        mEmployees = employees;
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
     * Sums together all the running costs per second.
     *
     * @return Running costs per second.
     */
    public double getRunningCosts() {
        return mSalaryCosts;
    }
    public double getSalaryCosts() { return mSalaryCosts; }

    public double getQuality() {
        return mQualityPerSec;
    }

    public List<ProjectSlot> getProjectSlots() {
        return mProjectSlots;
    }

}
