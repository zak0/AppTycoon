package jaakko.jaaska.softwaretycoon.engine.core;

import java.util.concurrent.atomic.AtomicBoolean;

import jaakko.jaaska.softwaretycoon.engine.time.GameTime;

/**
 * Created by jaakko on 7.3.2017.
 */

public class Ticker implements Runnable {

    private static final String TAG = "Ticker";
    private static final long TICK_SLEEP = 100; // How long the thread sleeps after each tick.

    private AtomicBoolean mRunning;
    private long mLastTick;

    public Ticker() {
        mRunning = new AtomicBoolean(false);
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

        while(mRunning.get()) {
            doTick();
        }
    }

    private void doTick() {
        //Log.d(TAG, "doTick() - called");

        long now = System.currentTimeMillis();
        long delta = now - mLastTick; // How long since previous tick.

        advanceGameTime(delta);

        mLastTick = now;

        try {
            Thread.sleep(TICK_SLEEP);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void advanceGameTime(long howMuch) {
        GameState gs = GameEngine.getInstance().getGameState();
        GameTime gt = gs.getGameTime();
        gt.add(howMuch * gs.getTimeFactor(), 0, 0, 0, 0);
        //Log.d(TAG, "advanceGameTime() - GameTime after addition: " + gt);
    }
}
