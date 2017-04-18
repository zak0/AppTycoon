package jaakko.jaaska.softwaretycoon.engine.core;

import jaakko.jaaska.softwaretycoon.engine.Company;
import jaakko.jaaska.softwaretycoon.engine.time.GameTime;

/**
 * Created by jaakko on 7.3.2017.
 *
 * Container for the player data and state of the current game.
 *
 */

public class GameState {

    private int mTimeFactor;
    private GameTime mGameTime;
    private Company mCompany;

    public GameState() {
        mTimeFactor = 1;
        mGameTime = new GameTime(7, 1, 1, 1);
    }

    public void setTimeFactor(int timeFactor) {
        this.mTimeFactor = timeFactor;
    }

    public int getTimeFactor() {
        return mTimeFactor;
    }

    public GameTime getGameTime() {
        return mGameTime;
    }

    public Company getCompany() {
        return mCompany;
    }

    public void setCompany(Company company) {
        mCompany = company;
    }
}
