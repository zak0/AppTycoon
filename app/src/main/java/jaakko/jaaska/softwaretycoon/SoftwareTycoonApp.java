package jaakko.jaaska.softwaretycoon;

import android.app.Application;
import android.content.Context;

/**
 * Created by jaakko on 2.4.2017.
 */

public class SoftwareTycoonApp extends Application {

    private static SoftwareTycoonApp sInstance;

    public static Context getContext() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        sInstance = this;
        super.onCreate();
    }
}
