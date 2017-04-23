package jaakko.jaaska.softwaretycoon.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
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

import jaakko.jaaska.softwaretycoon.R;
import jaakko.jaaska.softwaretycoon.engine.core.GameEngine;
import jaakko.jaaska.softwaretycoon.ui.fragment.EmployeesFragment;
import jaakko.jaaska.softwaretycoon.ui.fragment.ProjectsFragment;

/**
 * Created by jaakko on 7.3.2017.
 */

public class MainActivity extends FragmentActivity implements UiUpdater {

    private static final String TAG = "MainActivity";

    public static final int FRAGMENT_PROJECTS = 0;
    public static final int FRAGMENT_HUMAN_RESOURCES = 1;

    /** Currently visible fragment. */
    private int mCurrentFragment = Integer.MIN_VALUE;

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        // Start the game engine
        GameEngine engine = GameEngine.getInstance();
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

                Message msg = UiUpdateHandler.getInstance().obtainMessage(UiUpdateHandler.ACTION_REPLACE_FRAGMENT);
                Bundle data = new Bundle();
                data.putInt("FRAGMENT", FRAGMENT_HUMAN_RESOURCES);
                msg.setData(data);
                msg.sendToTarget();
            }
        });


        //
        // Navigation drawer item handling
        mDrawerLayout = (DrawerLayout) findViewById(R.id.layoutDrawer);
        setNavItemListeners((TextView) findViewById(R.id.textViewNavProjects), FRAGMENT_PROJECTS);
        setNavItemListeners((TextView) findViewById(R.id.textViewNavEmployees), FRAGMENT_HUMAN_RESOURCES);

    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        UiUpdateHandler.getInstance().registerUpdater(this, UiUpdateHandler.ACTION_REPLACE_FRAGMENT);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
        UiUpdateHandler.getInstance().unRegisterUpdater(this);
    }

    /**
     * Switches the content fragment to another one.
     * @param fragment Const of the fragment to switch to.
     */
    private void switchFragment(int fragment) {
        Log.d(TAG, "switchFragment() - fragment = " + fragment);

        Fragment newFragment = null;

        switch(fragment) {
            case FRAGMENT_PROJECTS:
                newFragment = new ProjectsFragment();
                break;
            case FRAGMENT_HUMAN_RESOURCES:
                newFragment = new EmployeesFragment();
                break;
            default:
                break;
        }

        if (newFragment != null) {
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
                    tv.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
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
            switchFragment(mLinksToFragment);
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void updateUi(Bundle args) {
        switchFragment(args.getInt(UiUpdateHandler.ARG_TARGET_FRAGMENT));
    }
}
