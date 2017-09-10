package jaakko.jaaska.apptycoon;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import jaakko.jaaska.apptycoon.engine.Company;
import jaakko.jaaska.apptycoon.engine.asset.PremisesAsset;
import jaakko.jaaska.apptycoon.engine.core.GameEngine;

import static org.junit.Assert.assertEquals;

/**
 * Runs test cases related to assets.
 */
@RunWith(AndroidJUnit4.class)
public class AssetsTests {

    private double mDelta = 0.0001; // error margin for asserting doubles.

    @Before
    public void reset() {
        InstrumentedTestHelper.resetGameStateForTesting();
    }

    @Test
    public void assetsAffectRunningCosts() {
        Company company = GameEngine.getInstance().getGameState().getCompany();

        PremisesAsset currentPremises = company.getPremises();
        PremisesAsset newPremises = PremisesAsset.getPremisesAssetById(PremisesAsset.SKYSCRAPER);

        double costsBefore = company.getRunningCosts();
        double expectedNewCosts = costsBefore - currentPremises.getCostPerSecond() + newPremises.getCostPerSecond();

        company.addAsset(newPremises);
        assertEquals(expectedNewCosts, company.getRunningCosts(), mDelta);

    }

    /**
     * Changing the current premises is done by "adding" a new PremisesAsset.
     * This should then automatically first remove the current premises from company's assets
     * and then add the new one. The new one should also be set to the current premises reference.
     */
    @Test
    public void addingPremisesAssetRemovesPrevious() {
        Company company = GameEngine.getInstance().getGameState().getCompany();

        PremisesAsset first = PremisesAsset.getPremisesAssetById(PremisesAsset.GARAGE);
        company.addAsset(first);
        assertEquals(first.getName(), company.getPremises().getName());

        PremisesAsset second = PremisesAsset.getPremisesAssetById(PremisesAsset.MEDIUM_OFFICE_SPACE);
        company.addAsset(second);
        assertEquals(second.getName(), company.getPremises().getName());
    }

}
