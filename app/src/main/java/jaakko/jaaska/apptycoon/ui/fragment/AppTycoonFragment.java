package jaakko.jaaska.apptycoon.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import jaakko.jaaska.apptycoon.R;
import jaakko.jaaska.apptycoon.ui.listener.FragmentBackButtonOnClickListener;
import jaakko.jaaska.apptycoon.ui.listener.TextViewChangeColourOnTouchListener;

/**
 * A class for providing consistent look and feel for all fragments that are shown within the
 * MainActivity. This includes the "fragment title bar" back button and action button handling.
 *
 * Extend this class for new content fragments.
 *
 * In the constructor of the subclass, only call the constructor of the superclass. Don't call
 * other superclass methods. Superclass is properly initialized after the overridden onContentCreateView
 * method is being called. So, that is the earliest point at which to call superclass methods.
 */
public abstract class AppTycoonFragment extends Fragment {

    private static final String TAG = "AppTycoonFragment";

    private int mContentLayoutResourceId;

    private String mTitle;
    private Integer mBackFragment;

    private String mActionLabel;
    private Action mAction;

    private TextView mTextViewBack;
    private TextView mTextViewAction;

    public AppTycoonFragment(String title, int contentLayout) {
        mContentLayoutResourceId = contentLayout;
        mTitle = title;
    }

    /**
     * Set the fragment to which the back button navigates back to.
     * @param fragmentId ID of the fragment to navigate back to.
     */
    protected void setBackTargetFragment(int fragmentId) {
        Log.d(TAG, "setBackTargetFragment() - fragmentId = " + fragmentId);
        mBackFragment = fragmentId;
        bindBackButton();
    }

    /**
     * Set an action for the action button on the fragment title bar.
     * @param label Label to be shown on the action button.
     * @param action Action to perform when the button is clicked.
     */
    protected void setAction(String label, Action action) {
        Log.d(TAG, "setAction() - label = '" + label + "'");
        mActionLabel = label;
        mAction = action;
        bindActionButton();
    }

    private void bindBackButton() {
        Log.d(TAG, "bindBackButton()");
        mTextViewBack.setVisibility(View.VISIBLE);
        mTextViewBack.setOnTouchListener(new TextViewChangeColourOnTouchListener(Color.BLACK,
                mTextViewBack.getCurrentTextColor()));
        mTextViewBack.setOnClickListener(new FragmentBackButtonOnClickListener(mBackFragment));
    }

    private void bindActionButton() {
        Log.d(TAG, "bindActionButton()");
        mTextViewAction.setVisibility(View.VISIBLE);
        mTextViewAction.setText(mActionLabel);
        mTextViewAction.setOnTouchListener(new TextViewChangeColourOnTouchListener(Color.BLACK,
                mTextViewAction.getCurrentTextColor()));
        mTextViewAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAction.doAction();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parent, container, false);

        // Inflate the content...
        View contentView = inflater.inflate(mContentLayoutResourceId, null);

        // ...and add it into the container layout for it.
        LinearLayout layoutContent = (LinearLayout) view.findViewById(R.id.layoutFragmentContent);
        layoutContent.addView(contentView);

        // Set the fragment title
        TextView textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        textViewTitle.setText(mTitle);

        // Store references to action and back buttons
        mTextViewBack = (TextView) view.findViewById(R.id.textViewBack);
        mTextViewAction = (TextView) view.findViewById(R.id.textViewAction);

        // The back and action buttons are hidden when nothing are assigned to them.
        mTextViewBack.setVisibility(View.INVISIBLE);
        mTextViewAction.setVisibility(View.INVISIBLE);

        // Set back and/or action buttons visible if actions are already set for them.
        if (mBackFragment != null) {
            bindBackButton();
        }
        if (mAction != null) {
            bindActionButton();
        }

        // Now configure the content of the fragment that was inflated from a separate
        // layout resource.
        onContentCreateView(contentView);

        return view;
    }

    /**
     * This is called at parent fragment onCreateView method.
     * So, do things here as you would with the 'normal' onCreateView() override for a fragment.
     *
     * @param view The root of the inflated content layout.
     */
    protected abstract void onContentCreateView(View view);

    /**
     * Action button actions need to implement this interface.
     */
    public interface Action {
        void doAction();
    }
}
