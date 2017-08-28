package jaakko.jaaska.apptycoon.engine.core;

import java.util.concurrent.atomic.AtomicBoolean;

import jaakko.jaaska.apptycoon.engine.Company;
import jaakko.jaaska.apptycoon.ui.UiUpdateHandler;

/**
 * Created by jaakko on 7.3.2017.
 */

public class Ticker implements Runnable {

    private static final String TAG = "Ticker";
    private static final long TICK_SLEEP = 100; // How long the thread sleeps after each tick.
    private static final long UI_REFRESH_DELAY = 125;

    private AtomicBoolean mRunning;
    private long mLastTick;
    private long mLastUiRefresh;

    private GameEngine mEngine;
    private GameState mGameState;
    private Company mCompany;

    public Ticker() {
        mRunning = new AtomicBoolean(false);
        mEngine = GameEngine.getInstance();
        mGameState = mEngine.getGameState();
        mCompany = mGameState.getCompany();
    }

    /**
     * After calling stop, the ticker loop will break after the currently running iteration.
     */
    public void stop() {
        mRunning.set(false);
    }

    @Override
    public void run() {
        mRunning.set(true);

        mLastTick = System.currentTimeMillis();

        while(mRunning.get()) {
            doTick();
        }
    }

    private void doTick() {
        //Log.d(TAG, "doTick() - called");

        long now = System.currentTimeMillis();
        long delta = now - mLastTick; // How long since previous tick.

        progressProjects(delta);
        handlePayments(delta);

        mLastTick = now;

        handleUiRefresh(now);

        try {
            Thread.sleep(TICK_SLEEP);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles sending the UI update message to MainActivity at wanted intervals.
     * @param now Time now in milliseconds.
     */
    private void handleUiRefresh(long now) {
        if (mLastUiRefresh + UI_REFRESH_DELAY <= now) {
            UiUpdateHandler.getInstance().obtainMessage(UiUpdateHandler.ACTION_REFRESH_UI).sendToTarget();
            mLastUiRefresh = now;
        }
    }

    /**
     * Progresses projects in project slots.
     * @param delta Milliseconds since last tick.
     */
    private void progressProjects(long delta) {
        double workAmount = (double) mCompany.getCps() / 1000.0f * (double) delta;
        mCompany.doWork(workAmount, delta);
    }

    /**
     * Pays the payments that are due. Both ways - income/costs.
     * @param delta Milliseconds since last tick.
     */
    private void handlePayments(long delta) {
        mCompany.raiseIncome(delta);
        mCompany.payRunningCosts(delta);
    }

}
