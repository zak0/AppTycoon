package jaakko.jaaska.apptycoon.engine.unlocks;

import jaakko.jaaska.apptycoon.engine.people.EmployeeType;

/**
 * An unlock condition that requires having employee(s) of certain type.
 */

public class EmployeeUnlockCondition implements UnlockCondition {

    private int mEmployeeType;
    private int mRequiredCount;

    /**
     * Constructor
     *
     * @param employeeType The type of employee that is needed
     * @param requiredCount How many employees of this type are needed
     */
    public EmployeeUnlockCondition(int employeeType, int requiredCount) {
        mEmployeeType = employeeType;
        mRequiredCount = requiredCount;
    }

    @Override
    public String getDescription() {
        EmployeeType type = EmployeeType.getTypeForId(mEmployeeType);

        return "Hire " + mRequiredCount + "x " + type.getTitle();
    }

    @Override
    public boolean isFulfilled() {
        return EmployeeType.getTypeForId(mEmployeeType).getCount() >= mRequiredCount;
    }
}
