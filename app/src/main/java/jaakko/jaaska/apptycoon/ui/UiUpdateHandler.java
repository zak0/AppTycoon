package jaakko.jaaska.apptycoon.ui;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
 * One UI class can be registered for multiple actions and multiple UI classes
 * can be registered for one action.
 *
 * Be sure to unregister UiUpdaters before they go unreachable or else crashes
 * are certain.
 *
 * Created by jaakko on 23.4.2017.
 */

public class UiUpdateHandler extends Handler {

    private static final String TAG = "UiUpdateHandler";

    //
    //
    // Update actions that UiUpdaters implement.

    /** Replaces the currently visible fragment with another one. */
    public static final int ACTION_REPLACE_FRAGMENT = 12;

    /** Refreshes the UI. This calls the updateUi() of all registered UiUpdaters. */
    public static final int ACTION_REFRESH_UI = 20;

    /** Navigates one step back in the navigation stack. */
    public static final int ACTION_GO_BACK = 30;

    //
    //
    // Constants for argument Bundle keys.

    /** ID of the fragment to navigate to. Type: int */
    public static final String ARG_TARGET_FRAGMENT = "target_fragment";

    /** Flag to toggle fragment transition animation of/off.
     * This is optional and defaults to true. Type: boolean */
    public static final String ARG_REPLACE_FRAGMENT_WITH_ANIMATION = "replace_fragment_anim";

    /** Flag for telling the new fragment is a previous one in the navigation stack.
     * Type: boolean */
    public static final String ARG_IS_BACK_TRANSITION = "override_back_animation";

    /** Used with project slot related fragments. The array index of the project slot.
     * Type: int */
    public static final String ARG_PROJECT_SLOT_INDEX = "project_slot_index";

    /** Used with product related fragments. The ID of the product type. Type: int */
    public static final String ARG_NEW_PRODUCT_TYPE = "product_type";

    /** Used to pass a product to a fragment. This should be an index of the product in the
     * List of products in the Company object. */
    public static final String ARG_PRODUCT_INDEX = "product_index";

    // List of currently registered UiUpdaters and their actions.
    private Map<Integer, List<UiUpdater>> mUpdaters;

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
     * Register a class to handle an action.
     *
     * @param updater UiUpdater
     * @param action Action to implement.
     */
    public void registerUpdater(UiUpdater updater, int action) {
        Log.d(TAG, "registerUpdate() - registering action " + action + " for class " + updater.getClass().getName());
        List<UiUpdater> updaters = mUpdaters.get(action);

        if (updaters == null) {
            updaters = new ArrayList<>();
            mUpdaters.put(action, updaters);
        }

        updaters.add(updater);
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
            List<UiUpdater> updaters = mUpdaters.get(action);

            Log.d(TAG, "unRegisterUpdater() - size before remove = " + updaters.size());
            updaters.remove(updater);
            Log.d(TAG, "unRegisterUpdater() - size after remove = " + updaters.size());

            Log.d(TAG, "unregisterUpdater() - unregistered action " + action);
        }
    }

    /**
     * Unregisters all updaters for a specific action.
     *
     * @param action ID of the action to unregister UiUpdaters for.
     */
    public void unRegisterAllForAction(int action) {
        mUpdaters.remove(action);
    }


    @Override
    public void handleMessage(Message msg) {
        //Log.d(TAG, "handleMessage() - what  = " + msg.what);

        if (!mUpdaters.containsKey(msg.what)) {
            Log.d(TAG, "handleMessage() - no UiUpdater assigned -> do nothing");
            return;
        }

        for (UiUpdater updater : mUpdaters.get(msg.what)) {
            updater.updateUi(msg.what, msg.getData());
        }

    }

    /**
     * Generates a message which after sending changes the current fragment.
     *
     * @param targetFragment Fragment ID to change to.
     * @return The message
     */
    public static Message obtainReplaceFragmentMessage(int targetFragment) {
        Message msg = getInstance().obtainMessage(UiUpdateHandler.ACTION_REPLACE_FRAGMENT);
        Bundle data = new Bundle();
        data.putInt(UiUpdateHandler.ARG_TARGET_FRAGMENT, targetFragment);
        msg.setData(data);
        return msg;
    }

    /**
     * Generates a message which after sending navigates one step back on the navigation
     * stack.
     *
     * @return The message
     */
    public static Message obtainGoBackMessage() {
        Message msg = getInstance().obtainMessage(UiUpdateHandler.ACTION_GO_BACK);
        return msg;
    }

    public static Message obtainUiRefreshMessage() {
        Message msg = getInstance().obtainMessage(ACTION_REFRESH_UI);
        return msg;
    }
}
