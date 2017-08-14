package jaakko.jaaska.apptycoon.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import jaakko.jaaska.apptycoon.R;
import jaakko.jaaska.apptycoon.engine.Company;
import jaakko.jaaska.apptycoon.engine.core.GameEngine;
import jaakko.jaaska.apptycoon.storage.StorageManager;
import jaakko.jaaska.apptycoon.ui.fragment.EmployeesFragment;
import jaakko.jaaska.apptycoon.ui.fragment.NewProductFragment;
import jaakko.jaaska.apptycoon.ui.fragment.NewProjectFragment;
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

    /** Currently visible fragment. */
    private int mCurrentFragment = Integer.MIN_VALUE;

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

        // Do not load a fragment if we're restoring a previous state.
        // TODO: Check if this is necessary.
        if (savedInstanceState == null) {
            Log.d(TAG, "onCreate() loaded a previous state -> default fragment not added");
            ProjectsFragment projectsFragment = new ProjectsFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.layoutFragmentContainer, projectsFragment)
                    .commit();

            mCurrentFragment = FRAGMENT_PROJECTS;
        }

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

        updateUi(Integer.MIN_VALUE, null);

        //
        // This is only for development purposes. A button to execute something.
        View viewTestHook = findViewById(R.id.textViewNavTestHook);
        viewTestHook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                long employeeCount = GameEngine.getInstance().getGameState().getCompany().getEmployeeCount();
                Toast.makeText(MainActivity.this, "employee count = " + employeeCount, Toast.LENGTH_LONG).show();
                */

                StorageManager storage = StorageManager.getInstance();

                if (storage.localSaveGameExists()) {
                    engine.setGameState(StorageManager.getInstance().loadFromDb());
                    Toast.makeText(MainActivity.this,
                            "Loaded game state from storage.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "No saved game state exists. Did nothing.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        viewTestHook.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                StorageManager.getInstance().saveToDb(engine.getGameState());
                Toast.makeText(MainActivity.this, "Saved game state to storage.", Toast.LENGTH_LONG).show();
                return true; // Do not pass on to OnClickListener.
            }
        });
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        UiUpdateHandler uiUpdateHandler = UiUpdateHandler.getInstance();
        uiUpdateHandler.registerUpdater(this, UiUpdateHandler.ACTION_REPLACE_FRAGMENT);
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
        Log.d(TAG, "switchFragment() - fragment = " + fragment);

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
            default:
                break;
        }

        if (newFragment != null) {
            if (args != null) {
                newFragment.setArguments(args);
            }

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.layoutFragmentContainer, newFragment)
                    .commit();

            mCurrentFragment = fragment;
        }
    }

    /**
     * Sets listeners for navigation drawer textviews.
     * @param navItem The item to set listeners for.
     * @param linkItemTo The fragment that the item should navigate to.
     */
    private void setNavItemListeners(TextView navItem, int linkItemTo) {
        navItem.setOnClickListener(new NavItemOnClickListener(linkItemTo));
        navItem.setOnTouchListener(new NavItemOnTouchListener());
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

        public NavItemOnClickListener(int linksTo) {
            mLinksToFragment = linksTo;
        }

        @Override
        public void onClick(View v) {
            updateUi(Integer.MIN_VALUE, null);
            switchFragment(mLinksToFragment, null);
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void updateUi(int action, Bundle args) {
        //Log.d(TAG, "updateUi() - start");

        if (action == UiUpdateHandler.ACTION_REPLACE_FRAGMENT) {
            switchFragment(args.getInt(UiUpdateHandler.ARG_TARGET_FRAGMENT), args);
        }

        // Update the top bar
        Company company = GameEngine.getInstance().getGameState().getCompany();
        TextView companyName = (TextView) findViewById(R.id.textViewCompanyName);
        TextView money = (TextView) findViewById(R.id.textViewMoney);
        TextView cps = (TextView) findViewById(R.id.textViewCps);
        TextView quality = (TextView) findViewById(R.id.textViewQuality);

        companyName.setText(company.getName());
        money.setText("$ " + Utils.largeNumberToNiceString(company.getFunds(), 2));
        cps.setText("C/s " + company.getCps());
        quality.setText("Q " +  String.format("%.2f", company.getQualityRatio()));

    }

}