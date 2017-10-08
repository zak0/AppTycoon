package jaakko.jaaska.apptycoon.ui.listener;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;

import jaakko.jaaska.apptycoon.ui.UiUpdateHandler;

/**
 * Universal {@link android.view.View.OnClickListener OnClickListener} for
 * the back button of fragments that have it.
 *
 */

public class FragmentBackButtonOnClickListener implements View.OnClickListener {
    private static final String TAG = "FBBOCL";

    private Bundle mArgs;
    private int mPreviousFragment;

    /**
     * Use this to simply navigate one step back on the navigation stack.
     */
    public FragmentBackButtonOnClickListener() {

    }

    /**
     * Use this to navigate back AND pass a custom set of arguments with the transition to
     * the target fragment.
     *
     * @param previousFragment ID of the fragment to navigate to
     * @param args Custom set of arguments to pass with the transition
     */
    public FragmentBackButtonOnClickListener(int previousFragment, Bundle args) {
        mPreviousFragment = previousFragment;
        mArgs = args;
    }

    @Override
    public void onClick(View v) {
        boolean customArgs = mArgs != null;

        Log.d(TAG, "onClick() - custom args set: " + customArgs);

        // If custom args are set, treat the transition as a 'regular' fragment transition.
        Message msg = customArgs ? UiUpdateHandler.obtainReplaceFragmentMessage(mPreviousFragment) :
                                    UiUpdateHandler.obtainGoBackMessage();

        if (customArgs) {
            // Raise the flag telling that this 'regular' transition is actually a back transition.
            mArgs.putBoolean(UiUpdateHandler.ARG_IS_BACK_TRANSITION, true);
            mArgs.putAll(msg.getData()); // Add all the original args as well, as e.g. the target
                                        // fragment ID is set there.
            msg.setData(mArgs);
        }

        msg.sendToTarget();
    }
}
