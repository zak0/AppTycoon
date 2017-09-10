package jaakko.jaaska.apptycoon;

import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import jaakko.jaaska.apptycoon.engine.Company;
import jaakko.jaaska.apptycoon.engine.core.GameEngine;
import jaakko.jaaska.apptycoon.engine.people.EmployeeType;

import static org.junit.Assert.assertEquals;

/**
 * Tests EmployeeType related cases.
 *
 * EmployeeType relies heavily on SparseArrays, so tests must be implemented as instrumented tests.
 */

@RunWith(AndroidJUnit4.class)
public class EmployeeTests {

    private double mDelta = 0.0001; // Error margin for asserting doubles.

    /**
     * Resets the current state of the game into a starting point for test cases.
     * Modify this state to suit each test case.
     *
     * "@Before" annotated, so this runs automatically before each test case.
     */
    @Before
    public void resetGameStateForTesting() {
        InstrumentedTestHelper.resetGameStateForTesting();
    }

    @After
    public void resetStaticEmployeeTypes() {
        InstrumentedTestHelper.resetStaticEmployeeTypes();
    }

    @Test
    public void countStaysIntactWhenHiringAndFiring() {
        EmployeeType type = EmployeeType.getTypeForId(EmployeeType.TYPE_DEVELOPER);
        type.hire(7);
        assertEquals(7, type.getCount());
        type.fire(4);
        assertEquals(3, type.getCount());
        type.fire(12);
        assertEquals(0, type.getCount());
        type.hire(1);
        assertEquals(1, type.getCount());
    }

    /**
     * Tests that new EmployeeTypes are unlocked when conditions for unlocking are met.
     */
    @Test
    public void employeeUnlockingWorks() {
        Company company = GameEngine.getInstance().getGameState().getCompany();

        // This should be locked now.
        EmployeeType typeSeniorDev = EmployeeType.getTypeForId(EmployeeType.TYPE_SENIOR_DEVELOPER);
        assertEquals(false, typeSeniorDev.isUnlocked());

        // Fill the conditions for its unlock gradually and check in-between.
        company.addEmployee(EmployeeType.TYPE_DEVELOPER, 4); // One still missing
        assertEquals(false, typeSeniorDev.isUnlocked());
        company.addEmployee(EmployeeType.TYPE_DEVELOPER, 1); // Now it should be unlocked
        assertEquals(true, typeSeniorDev.isUnlocked());

        // Now a case with multiple unlock conditions.
        EmployeeType typeTestEngineer = EmployeeType.getTypeForId(EmployeeType.TYPE_TEST_ENGINEER);
        assertEquals(false, typeTestEngineer.isUnlocked());
        company.addEmployee(EmployeeType.TYPE_SENIOR_DEVELOPER, 2);
        assertEquals(false, typeTestEngineer.isUnlocked());
        company.addEmployee(EmployeeType.TYPE_DEVELOPER, 6);
        assertEquals(true, typeTestEngineer.isUnlocked());
    }

    @Test
    public void hiringAndFiringAffectSalaryCosts() {
        Company company = GameEngine.getInstance().getGameState().getCompany();

        // At this point we should have no employees --> Salary costs should be 0.00.
        assertEquals(0.00, company.getSalaryCosts(), mDelta);

        // Hire and fire different amounts and test in-between.
        EmployeeType type = EmployeeType.getTypeForId(EmployeeType.TYPE_DEVELOPER);
        double costOfOne = type.getSalary();
        company.addEmployee(type.getType(), 1);
        assertEquals(costOfOne, company.getSalaryCosts(), mDelta);
        company.removeEmployee(type.getType(), 1);
        assertEquals(0.00, company.getSalaryCosts(), mDelta);
        company.addEmployee(type.getType(), 13);
        assertEquals(costOfOne * 13.0d, company.getSalaryCosts(), mDelta);
        company.removeEmployee(type.getType(), 2);
        assertEquals(costOfOne * 11.0d, company.getSalaryCosts(), mDelta);
    }

}
