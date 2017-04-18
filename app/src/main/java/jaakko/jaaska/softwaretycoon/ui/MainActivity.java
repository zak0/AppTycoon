package jaakko.jaaska.softwaretycoon.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import jaakko.jaaska.softwaretycoon.R;
import jaakko.jaaska.softwaretycoon.engine.core.GameEngine;
import jaakko.jaaska.softwaretycoon.utils.Utils;

/**
 * Created by jaakko on 7.3.2017.
 */

public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";

    private static final int PROJECTS_FRAGMENT = 0;
    private static final int EMPLOYEES_FRAGMENT = 1;

    /** Currently visible fragment. */
    private int mCurrentFragment = Integer.MIN_VALUE;

    private DrawerLayout mDrawerLayout;


    //private GameEngine engine;

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

            mCurrentFragment = PROJECTS_FRAGMENT;
        }

        RelativeLayout topBar = (RelativeLayout) findViewById(R.id.layoutTopBar);
        topBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
                //switchFragment(mCurrentFragment == EMPLOYEES_FRAGMENT ? PROJECTS_FRAGMENT : EMPLOYEES_FRAGMENT);
            }
        });


        //
        // Navigation drawer item handling
        mDrawerLayout = (DrawerLayout) findViewById(R.id.layoutDrawer);
        setNavItemListeners((TextView) findViewById(R.id.textViewNavProjects), PROJECTS_FRAGMENT);
        setNavItemListeners((TextView) findViewById(R.id.textViewNavEmployees), EMPLOYEES_FRAGMENT);

    }

    /**
     * Switches the content fragment to another one.
     * @param fragment Const of the fragment to switch to.
     */
    private void switchFragment(int fragment) {
        Fragment newFragment = null;

        switch(fragment) {
            case PROJECTS_FRAGMENT:
                newFragment = new ProjectsFragment();
                break;
            case EMPLOYEES_FRAGMENT:
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

}
