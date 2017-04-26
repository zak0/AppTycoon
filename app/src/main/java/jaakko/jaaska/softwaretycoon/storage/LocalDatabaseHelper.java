package jaakko.jaaska.softwaretycoon.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jaakko on 23.4.2017.
 */

public class LocalDatabaseHelper extends SQLiteOpenHelper {

    public static int DB_VERSION = 1;
    public static String DB_FILE = "swtycoon_dev.db";

    private SQLiteDatabase mDb;

    public LocalDatabaseHelper(Context context) {
        super(context, DB_FILE, null, DB_VERSION);

        mDb = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
