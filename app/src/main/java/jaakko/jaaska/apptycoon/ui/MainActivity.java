package jaakko.jaaska.apptycoon.ui;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import jaakko.jaaska.apptycoon.AppTycoonApp;
import jaakko.jaaska.apptycoon.R;
import jaakko.jaaska.apptycoon.engine.Company;
import jaakko.jaaska.apptycoon.engine.core.GameEngine;
import jaakko.jaaska.apptycoon.storage.StorageManager;
import jaakko.jaaska.apptycoon.ui.dialog.AppTycoonAlertDialog;
import jaakko.jaaska.apptycoon.ui.fragment.AssetsFragment;
import jaakko.jaaska.apptycoon.ui.fragment.EmployeesFragment;
import jaakko.jaaska.apptycoon.ui.fragment.ItFragment;
import jaakko.jaaska.apptycoon.ui.fragment.NewProductFragment;
import jaakko.jaaska.apptycoon.ui.fragment.NewProductReleaseFragment;
import jaakko.jaaska.apptycoon.ui.fragment.NewProjectFragment;
import jaakko.jaaska.apptycoon.ui.fragment.PremisesFragment;
import jaakko.jaaska.apptycoon.ui.fragment.ProductStatsFragment;
import jaakko.jaaska.apptycoon.ui.fragment.ProductsFragment;
import jaakko.jaaska.apptycoon.ui.fragment.ProjectsFragment;
import jaakko.jaaska.apptycoon.utils.Utils;

/**
 * Main activity and a container for changing content fragments.
 */

public class MainActivity extends FragmentActivity implements UiUpdater {

    private static final String TAG = "MainActivity";

    public static final int FRAGMENT_PROJECTS = 0;
    public static final int FRAGMENT_NEW_PROJECT = 1;

    public static final int FRAGMENT_HUMAN_RESOURCES = 10;

    public static final int FRAGMENT_PRODUCTS = 20;
    public static final int FRAGMENT_NEW_PRODUCT = 21;
    public static final int FRAGMENT_PRODUCT_STATS = 22;
    public static final int FRAGMENT_PRODUCT_NEW_RELEASE = 23;

    public static final int FRAGMENT_ASSETS = 30;
    public static final int FRAGMENT_PREMISES = 31;
    public static final int FRAGMENT_IT = 32;

    /** Currently visible fragment. */
    private int mCurrentFragment = Integer.MIN_VALUE;

    /** IDs of the fragments that the user navigated to.
     * Used for providing "back" action.
     */
    private ArrayList<Integer> mNavigationStack = new ArrayList<>();

    /** A list containing all the navigation drawer TextViews.
     * This is used to control their appearance based on which fragment
     * is visible. */
    private ArrayList<TextView> mNavigationTextViews = new ArrayList<>();

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        // Start the game engine
        final GameEngine engine = GameEngine.getInstance();
        engine.loadTestData();
        engine.startEngine();

        setContentView(R.layout.activity_main);

        RelativeLayout topBar = (RelativeLayout) findViewById(R.id.layoutTopBar);
        topBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //
        // Navigation drawer item handling
        mDrawerLayout = (DrawerLayout) findViewById(R.id.layoutDrawer);
        setNavItemListeners((TextView) findViewById(R.id.textViewNavProjects), FRAGMENT_PROJECTS);
        setNavItemListeners((TextView) findViewById(R.id.textViewNavProducts), FRAGMENT_PRODUCTS);
        setNavItemListeners((TextView) findViewById(R.id.textViewNavEmployees), FRAGMENT_HUMAN_RESOURCES);
        setNavItemListeners((TextView) findViewById(R.id.textViewNavAssets), FRAGMENT_ASSETS);

        // Go to the Projects fragment on startup
        findViewById(R.id.textViewNavProjects).performClick();

        updateUi(Integer.MIN_VALUE, null);

    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        UiUpdateHandler uiUpdateHandler = UiUpdateHandler.getInstance();
        uiUpdateHandler.registerUpdater(this, UiUpdateHandler.ACTION_REPLACE_FRAGMENT);
        uiUpdateHandler.registerUpdater(this, UiUpdateHandler.ACTION_GO_BACK);
        uiUpdateHandler.registerUpdater(this, UiUpdateHandler.ACTION_REFRESH_UI);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
        UiUpdateHandler uiUpdateHandler = UiUpdateHandler.getInstance();
        uiUpdateHandler.unRegisterUpdater(this);
    }

    /**
     * Switches the content fragment to another one. Also passes args to the fragment if the
     * fragment needs them.
     * @param fragment Const of the fragment to switch to.
     */
    private void switchFragment(int fragment, Bundle args) {
        Log.d(TAG, "switchFragment() - fragment = " + fragment +
            ", has args = " + (args != null));

        // If the target fragment is the same as current one, do nothing.
        if (fragment == mCurrentFragment) {
            Log.d(TAG, "switchFragment() - already at target fragment!");
            return;
        }

        Fragment newFragment = null;

        switch(fragment) {
            case FRAGMENT_PROJECTS:
                newFragment = new ProjectsFragment();
                break;
            case FRAGMENT_HUMAN_RESOURCES:
                newFragment = new EmployeesFragment();
                break;
            case FRAGMENT_NEW_PROJECT:
                newFragment = new NewProjectFragment();
                break;
            case FRAGMENT_PRODUCTS:
                newFragment = new ProductsFragment();
                break;
            case FRAGMENT_NEW_PRODUCT:
                newFragment = new NewProductFragment();
                break;
            case FRAGMENT_ASSETS:
                newFragment = new AssetsFragment();
                break;
            case FRAGMENT_PREMISES:
                newFragment = new PremisesFragment();
                break;
            case FRAGMENT_IT:
                newFragment = new ItFragment();
                break;
            case FRAGMENT_PRODUCT_STATS:
                newFragment = new ProductStatsFragment();
                break;
            case FRAGMENT_PRODUCT_NEW_RELEASE:
                newFragment = new NewProductReleaseFragment();
                break;
            default:
                break;
        }

        if (newFragment != null) {

            boolean animateTransition = true;
            boolean isBackTransition = false;

            if (args != null) {
                newFragment.setArguments(args);
                animateTransition = args.getBoolean(UiUpdateHandler.ARG_REPLACE_FRAGMENT_WITH_ANIMATION, true);
                isBackTransition = args.getBoolean(UiUpdateHandler.ARG_IS_BACK_TRANSITION, false);
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if (animateTransition) {
                if (isBackTransition) {
                    transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                } else {
                    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }

            transaction.replace(R.id.layoutFragmentContainer, newFragment).commit();

            // Only add the current fragment into the stack when navigation is going forward.
            if (!isBackTransition) {
                if (mCurrentFragment >= 0) { // Skip the invalid initialized value of mCurrentFragment.
                    mNavigationStack.add(mCurrentFragment);
                }
            } else {
                // Remove the last fragment from the navigation stack when going back.
                mNavigationStack.remove(mNavigationStack.size() - 1);
            }

            mCurrentFragment = fragment;
        }
    }

    /**
     * Sets listeners for navigation drawer textviews.
     * @param navItem The item to set listeners for.
     * @param linkItemTo The fragment that the item should navigate to.
     */
    private void setNavItemListeners(TextView navItem, int linkItemTo) {
        // Set the listeners
        navItem.setOnClickListener(new NavItemOnClickListener(linkItemTo));
        navItem.setOnTouchListener(new NavItemOnTouchListener());

        // And add the TextView to the list of navigation drawer text views
        mNavigationTextViews.add(navItem);
    }

    /**
     * Navigation drawer link items have a indicator ("[ ]" / [x]")
     * that tells which navigation item is currently active.
     * This method clears all the navigation items back to "[ ]" state.
     */
    private void clearNavItemIndicators() {
        for (TextView tw : mNavigationTextViews) {
            setNavItemIndicator(tw, false);
        }
    }

    /**
     * Sets the selected nav item indicator for a text view either on or off.
     *
     * @param tw Navigation item TextView
     * @param selected True marks this item selected ("[x]")
     */
    private void setNavItemIndicator(TextView tw, boolean selected) {
        String textNow = tw.getText().toString();
        String newToggle = "[" + (selected ? "x" : " ") + "]";
        tw.setText(textNow.replace(textNow.substring(0, 3), newToggle));
    }

    /**
     * OnTouchListener for navigation drawer items.
     * The listener highlights the item when it is touched.
     */
    private class NavItemOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // These are TextViews.
            TextView tv = (TextView) v;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    tv.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.accent));
                    break;
                default:
                    tv.setTextColor(Color.WHITE);
            }
            return false;
        }
    }

    /**
     * OnClickListener for navigation drawer items.
     * Clicking an item changes the currently active fragment visible
     * on the MainActivity.
     */
    private class NavItemOnClickListener implements View.OnClickListener {
        private int mLinksToFragment; // The fragment this items links to.

        NavItemOnClickListener(int linksTo) {
            mLinksToFragment = linksTo;
        }

        @Override
        public void onClick(View v) {
            updateUi(Integer.MIN_VALUE, null);
            switchFragment(mLinksToFragment, null);
            mDrawerLayout.closeDrawer(GravityCompat.START);

            // Navigation stack is emptied when a new fragment is entered from
            // the navigation drawer.
            mNavigationStack.clear();

            clearNavItemIndicators();
            setNavItemIndicator((TextView) v, true);
        }
    }

    @Override
    public void updateUi(int action, Bundle args) {
        //Log.d(TAG, "updateUi() - start");

        // First change the fragment if a fragment change is requested.
        if (action == UiUpdateHandler.ACTION_REPLACE_FRAGMENT) {
            switchFragment(args.getInt(UiUpdateHandler.ARG_TARGET_FRAGMENT), args);
        } else if (action == UiUpdateHandler.ACTION_GO_BACK) { // Or navigate back on the navigation stack.
            goBackOnNavigationStack();
        }

        // Then update the top bar
        Company company = GameEngine.getInstance().getGameState().getCompany();
        TextView companyName = (TextView) findViewById(R.id.textViewCompanyName);
        TextView money = (TextView) findViewById(R.id.textViewMoney);
        TextView moneyRate = (TextView) findViewById(R.id.textViewNavMoneyRate);
        TextView codeRate = (TextView) findViewById(R.id.textViewNavCodeRate);

        Resources res = AppTycoonApp.getContext().getResources();
        String strMoney = res.getString(R.string.top_bar_money,
                Utils.largeNumberToNiceString(company.getFunds(), 2));

        companyName.setText(company.getName());
        money.setText(strMoney);

        moneyRate.setText(Utils.largeNumberToNiceString(company.getIncome() - company.getRunningCosts(), 2));
        codeRate.setText(Utils.largeNumberToNiceString(company.getCps(), 2));

    }

    /**
     * Switches the current fragment back to the previous one on the stack.
     *
     * If the currently visible fragment is the only one in the stack, then this
     * method does nothing.
     *
     * NOTE! Back navigation with custom arguments do not go through this method, but directly
     * to switchFragment!
     */
    private void goBackOnNavigationStack() {
        if (mNavigationStack.size() <= 0) {
            Log.d(TAG, "goBackOnNavigationStack() - already at the bottom");
        } else {
            Log.d(TAG, "goBackOnNavigationStack() - going back");
            int prevFragment = mNavigationStack.get(mNavigationStack.size() - 1);
            Bundle args = new Bundle();
            args.putBoolean(UiUpdateHandler.ARG_IS_BACK_TRANSITION, true);
            switchFragment(prevFragment, args);
        }
    }

    /**
     * Prompts the user for quitting the app. Quits on no.
     */
    private void promptExit() {
        final AppTycoonAlertDialog dialog = new AppTycoonAlertDialog(this, "Exit", "Are you sure?");
        dialog.setOkAction("Yes", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.setCancelAction("No", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * Override back button to prevent accidental app exits. This because everything is now
     * running on one activity. So, without this, a single back press would kill the entire app.
     * TODO: Make back button to navigate one step back when there is a back action set for current fragment.
     */
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed() - navstack size: " + mNavigationStack.size());

        // If the navigation stack is empty, prompt for exit.
        if (mNavigationStack.size() > 0) {
            goBackOnNavigationStack();
        } else {
            promptExit();
        }
    }
}
