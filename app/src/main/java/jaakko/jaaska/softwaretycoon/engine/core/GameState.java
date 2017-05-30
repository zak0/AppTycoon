package jaakko.jaaska.softwaretycoon.engine.core;

import jaakko.jaaska.softwaretycoon.engine.Company;

/**
 * Created by jaakko on 7.3.2017.
 *
 * Container for the player data and state of the current game.
 *
 */

public class GameState {

    private int mTimeFactor;
    private Company mCompany;

    public GameState() {
        mTimeFactor = 1;
    }

    public void setTimeFactor(int timeFactor) {
        this.mTimeFactor = timeFactor;
    }

    public int getTimeFactor() {
        return mTimeFactor;
    }

    public Company getCompany() {
        return mCompany;
    }

    public void setCompany(Company company) {
        mCompany = company;
    }
}
