package jaakko.jaaska.softwaretycoon.storage;

import jaakko.jaaska.softwaretycoon.engine.core.GameState;

/**
 * Created by jaakko on 1.4.2017.
 *
 * Class for accessing local (and cloud) storage of game data.
 */

public class StorageManager {

    /**
     * Load the GameState from local database.
     *
     * @return GameState object with loaded data.
     */
    public static GameState loadFromDb() {
        return null;
    }

    /**
     * Saves GameState into the local database.
     *
     * All game activity needs to be stopped to ensure an intact
     * and a 100% valid save.
     *
     * @param gs GameState to save.
     */
    public static void saveToDb(GameState gs) {

    }

}
