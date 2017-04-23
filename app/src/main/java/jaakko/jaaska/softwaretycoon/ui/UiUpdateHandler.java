package jaakko.jaaska.softwaretycoon.ui;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler that handles UI updates. UI update actions are triggered
 * by sending a message to this handler with an action ID (action IDs
 * are defined as public static on top of this class).
 *
 * UI classes (activites, fragments, ...) can implement the UiUpdater interface
 * and register themselves as a "handler" for UI update actions triggered from
 * elsewhere in the code.
 *
 * Be sure to unregister UiUpdaters before they go unreachable or else crashes
 * are certain.
 *
 * Created by jaakko on 23.4.2017.
 */

public class UiUpdateHandler extends Handler {

    private static final String TAG = "UiUpdateHandler";

    // Update actions that UiUpdaters implement.
    public static final int ACTION_REPLACE_FRAGMENT = 12;

    // Constants for argument Bundle keys.
    public static final String ARG_TARGET_FRAGMENT = "target_fragment";

    // List of currently registered UiUpdaters and their actions.
    private Map<Integer, UiUpdater> mUpdaters;

    private static UiUpdateHandler sInstance;

    private UiUpdateHandler() {
        super(Looper.getMainLooper());
        mUpdaters = new HashMap<>();
    }

    public static synchronized UiUpdateHandler getInstance() {
        if (sInstance == null) {
            sInstance = new UiUpdateHandler();
        }
        return sInstance;
    }

    /**
     * Register a class to handle an action. Only one class per action
     * can be registered at a time.
     *
     * @param updater UiUpdater
     * @param action Action to implement.
     */
    public void registerUpdater(UiUpdater updater, int action) {
        Log.d(TAG, "registerUpdate() - registering action " + action + " for class " + updater.getClass().getName());
        mUpdaters.put(action, updater);
    }

    /**
     * Unregisters a class from handling actions. After unregistering,
     * actions that were assigned to this UiUpdater are ignored.
     *
     * @param updater UiUpdater to unregister.
     */
    public void unRegisterUpdater(UiUpdater updater) {
        Log.d(TAG, "unregisterUpdater() - unregistering " + updater.getClass().getName());
        for (int action : mUpdaters.keySet()) {
            if (mUpdaters.get(action) == updater) {
                Log.d(TAG, "unregisterUpdater() - unregistered action " + action);
                mUpdaters.remove(action);
            }
        }
    }


    @Override
    public void handleMessage(Message msg) {
        Log.d(TAG, "handleMessage() - what  = " + msg.what);

        if (!mUpdaters.containsKey(msg.what)) {
            Log.d(TAG, "handleMessage() - no UiUpdater assigned -> do nothing");
            return;
        }

        UiUpdater updater = mUpdaters.get(msg.what);
        updater.updateUi(msg.getData());

    }
}
