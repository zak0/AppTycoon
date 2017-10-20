package jaakko.jaaska.apptycoon;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Utilities to help setting up Espresso UI tests.
 */

class UiTestHelper {

    /**
     * Opens the navigation drawer.
     */
    static void openNavigationDrawer() {
        onView(withId(R.id.textViewCompanyName)).perform(click());
    }

    /**
     * Clicks on a view in the navigation drawer. First opens the drawer.
     *
     * @param id ID of the view to click
     */
    static void clickNavigationDrawerItem(int id) {
        openNavigationDrawer();
        onView(withId(id)).perform(click());
    }
}
