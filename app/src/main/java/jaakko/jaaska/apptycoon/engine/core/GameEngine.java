package jaakko.jaaska.apptycoon.engine.core;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import jaakko.jaaska.apptycoon.engine.Company;
import jaakko.jaaska.apptycoon.engine.people.EmployeeType;
import jaakko.jaaska.apptycoon.engine.product.Product;
import jaakko.jaaska.apptycoon.engine.product.ProductType;

/**
 * Created by jaakko on 7.3.2017.
 *
 * Singleton for controlling the game engine. Also acts as an accessor for the current game instance.
 */

public class GameEngine {

    private static final String TAG = "GameEngine";

    private AtomicBoolean mRunning; // flag for telling if the engine has been started
    private GameState mGameState;
    private Ticker mTicker;

    private static GameEngine sInstance;

    public static synchronized GameEngine getInstance() {
        if (sInstance == null) {
            sInstance = new GameEngine();
        }
        return sInstance;
    }


    private GameEngine() {
        mRunning = new AtomicBoolean(false);
    }


    /**
     * Loads a set of game data used for testing and development.
     */
    public void loadTestData() {
        mGameState = new GameState();
        Company company = new Company("Company Name Inc.", 10, 10000, 10000);

        // Add project slots
        for (int i = 0; i < 3; i++) {
            company.addProjectSlot();
        }

        // Add employees
        company.addEmployee(EmployeeType.TYPE_DEVELOPER, 3);
        company.addEmployee(EmployeeType.TYPE_SENIOR_DEVELOPER, 2);
        company.addEmployee(EmployeeType.TYPE_TEST_ENGINEER, 0);

        // Add products
        Product mobileApp = new Product("Some Cool App", ProductType.getProductType(ProductType.PRODUCT_TYPE_MOBILE_APP));
        company.addProduct(mobileApp);

        mGameState.setCompany(company);
    }


    /**
     * Starts the game engine and begins simulating the game with loaded GameState.
     */
    public void startEngine() {
        if (mRunning.getAndSet(true)) {
            Log.e(TAG, "startEngine() - Engine is already started.");
            return;
        }

        // Start the ticker
        mTicker = new Ticker();
        new Thread(mTicker).start();
    }


    /**
     * First gracefully prepares the current GameState for an exit. Then stops the game engine.
     */
    public void stopEngine() {
        if (!mRunning.getAndSet(false)) {
            Log.e(TAG, "stopEngine() - Engine is already stopped.");
            return;
        }

        // Stop the ticker
        mTicker.stop();
    }

    public GameState getGameState() {
        return mGameState;
    }

    /**
     * Use this only when setting current GameState to one that is loaded
     * from storage of cloud.
     *
     * @param gameState
     */
    public void setGameState(GameState gameState) {
        mGameState = gameState;
    }
}
