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

    // Back button fragment ID and arguments are only defined when a custom back target is used.
    // The default back action (without parameters) does not need these.
    private Bundle mBackArgs;
    private Integer mBackFragment = -1; // Negative ID indicates that custom back target is not set.

    private String mActionLabel;
    private Action mAction;

    private View mView;
    private TextView mTextViewBack;
    private TextView mTextViewAction;

    public AppTycoonFragment(String title, int contentLayout) {
        mContentLayoutResourceId = contentLayout;
        mTitle = title;
    }

    /**
     * Display the back button, that navigates one step back in the navigation stack.
     */
    protected void showBackButton() {
        bindBackButton();
    }

    /**
     * Set the fragment to which the back button navigates back to. And optional arguments. Use
     * this when you need to pass arguments for the previous fragment, and the plain showBackButton()
     * otherwise.
     *
     * @param fragmentId ID of the fragment to navigate back to.
     * @param args Arguments to pass to the previous fragment.
     */
    protected void showCustomBackButton(int fragmentId, @Nullable Bundle args) {
        Log.d(TAG, "setBackTargetFragment() - fragmentId = " + fragmentId);
        mBackFragment = fragmentId;
        mBackArgs = args;
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

        // Use the listener with custom target and arguments if they're defined.
        FragmentBackButtonOnClickListener listener = mBackFragment < 0 ?
                new FragmentBackButtonOnClickListener() :
                new FragmentBackButtonOnClickListener(mBackFragment, mBackArgs);

        mTextViewBack.setOnClickListener(listener);
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

    /**
     * Set the title of the fragment.
     *
     * @param title The title to show for the fragment
     */
    protected void setTitle(String title) {
        mTitle = title;
        TextView textViewTitle = (TextView) mView.findViewById(R.id.textViewTitle);
        textViewTitle.setText(mTitle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_parent, container, false);

        // Inflate the content...
        View contentView = inflater.inflate(mContentLayoutResourceId, null);

        // ...and add it into the container layout for it.
        LinearLayout layoutContent = (LinearLayout) mView.findViewById(R.id.layoutFragmentContent);
        layoutContent.addView(contentView);

        // Set the fragment title
        setTitle(mTitle);

        // Store references to action and back buttons
        mTextViewBack = (TextView) mView.findViewById(R.id.textViewBack);
        mTextViewAction = (TextView) mView.findViewById(R.id.textViewAction);

        // The back and action buttons are hidden when nothing are assigned to them.
        mTextViewBack.setVisibility(View.INVISIBLE);
        mTextViewAction.setVisibility(View.INVISIBLE);

        // Now configure the content of the fragment that was inflated from a separate
        // layout resource.
        onContentCreateView(contentView);

        return mView;
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
