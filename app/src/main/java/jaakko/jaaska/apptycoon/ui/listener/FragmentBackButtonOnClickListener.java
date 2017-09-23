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
 * Constructor takes the ID of the previous fragment that the back button navigates to.
 *
 */

public class FragmentBackButtonOnClickListener implements View.OnClickListener {
    private static final String TAG = "FBBOCL";

    private int mPreviousFragment;

    public FragmentBackButtonOnClickListener(int previousFragment) {
        mPreviousFragment = previousFragment;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick() - previous fragment = " + mPreviousFragment);
        Message msg = UiUpdateHandler.obtainReplaceFragmentMessage(mPreviousFragment);
        Bundle args = msg.getData();
        args.putBoolean(UiUpdateHandler.ARG_IS_BACK_TRANSITION, true);
        msg.setData(args);

        msg.sendToTarget();
    }
}
