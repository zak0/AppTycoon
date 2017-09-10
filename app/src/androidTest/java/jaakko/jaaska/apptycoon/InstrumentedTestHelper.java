package jaakko.jaaska.apptycoon;

import jaakko.jaaska.apptycoon.engine.Company;
import jaakko.jaaska.apptycoon.engine.asset.PremisesAsset;
import jaakko.jaaska.apptycoon.engine.core.GameEngine;
import jaakko.jaaska.apptycoon.engine.core.GameState;
import jaakko.jaaska.apptycoon.engine.people.EmployeeType;

/**
 * A utility class for helping setting up the instrumented tests.
 *
 * This class does not implemented any tests!
 */

class InstrumentedTestHelper {

    /**
     * Resets the current state of the game into a starting point for test cases.
     * Modify this state to suit each test case.
     *
     * This method also sets the GameState to the GameEngine. So, to access the GameState,
     * do it through GameEngine. This also simulates the real-life scenario better than just
     * directly accessing the reset GameState.
     *
     */
    static void resetGameStateForTesting() {
        GameState gameState = new GameState();
        Company company = new Company("Company Name Inc.", 10, 25000000, 100000);

        // Add premises asset
        company.addAsset(PremisesAsset.getPremisesAssetById(PremisesAsset.SMALL_OFFICE_SPACE));

        gameState.setCompany(company);

        // Set this GameState as the state in the GameEngine.
        GameEngine.getInstance().setGameState(gameState);
    }

    /**
     * EmployeeTypes are stored statically. This method resets the types back to the starting point.
     *
     * TODO: EmployeeType should really be refactored to not rely on storing the data statically...
     */
    static void resetStaticEmployeeTypes() {
        for (EmployeeType type : EmployeeType.getAllTypes()) {
            type.fire(Integer.MAX_VALUE);
        }
    }
}
