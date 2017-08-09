package jaakko.jaaska.softwaretycoon.storage;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import jaakko.jaaska.softwaretycoon.SoftwareTycoonApp;
import jaakko.jaaska.softwaretycoon.engine.core.GameState;

/**
 * Class for accessing local (and cloud) storage of game data.
 *
 * All storage actions outside from .storage package should go through this class.
 */

public class StorageManager {

    private static final String TAG = "StorageManager";

    private static StorageManager mInstance;
    private LocalDatabaseHelper mDbHelper;

    private StorageManager() {
        mDbHelper = new LocalDatabaseHelper(SoftwareTycoonApp.getContext());
    }

    public static StorageManager getInstance() {
        if (mInstance == null) {
            Log.d(TAG, "getInstance() - new instance created");
            mInstance = new StorageManager();
        }

        return mInstance;
    }


    /**
     * Load the GameState from local database.
     *
     * @return GameState object with loaded data.
     */
    public GameState loadFromDb() {
        Log.d(TAG, "loadFromDb()");

        mDbHelper.openReadable();
        GameState loadedState = mDbHelper.loadGameState();
        mDbHelper.close();

        return loadedState;
    }

    /**
     * Saves GameState into the local database.
     *
     * All game activity needs to be stopped to ensure an intact
     * and a 100% valid save.
     *
     * @param gs GameState to save.
     */
    public void saveToDb(GameState gs) {
        Log.d(TAG, "saveToDb()");
        mDbHelper.openWritable();
        mDbHelper.storeGameState(gs);
        mDbHelper.close();
    }

}
