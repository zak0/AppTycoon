package jaakko.jaaska.apptycoon.ui.listener;

import android.util.Log;
import android.view.View;

import jaakko.jaaska.apptycoon.ui.UiUpdateHandler;

/**
 * Universal {@link android.view.View.OnClickListener OnClickListener} for
 * the back button of fragments that have it.
 *
 * Constructor takes the ID of the previous fragment that the back button navigates to.
 *
 * Created by jaakko on 8.5.2017.
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
        UiUpdateHandler.obtainReplaceFragmentMessage(mPreviousFragment).sendToTarget();
    }
}
