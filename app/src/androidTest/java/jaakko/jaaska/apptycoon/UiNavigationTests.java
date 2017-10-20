package jaakko.jaaska.apptycoon;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import jaakko.jaaska.apptycoon.ui.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

/**
 * Tests for verifying navigating in the UI works.
 *
 * TODO: Eventually, this class should walk through the entire UI.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class UiNavigationTests {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void reset() {
    }

    @Test
    public void drawerOpensWhenTappingTheCompanyTitle_sameActivity() {
        // At start, navigation drawer should be closed (and thus no navigation links visible).
        onView(withId(R.id.textViewNavProjects)).check(matches(not(isDisplayed())));

        // The navigation drawer should open when the user clicks on the top bar.
        // Now clicks on the TextView displaying company name.
        onView(withId(R.id.textViewCompanyName)).perform(click());

        // Now the drawer should be open.
        onView(withId(R.id.textViewNavProjects)).check(matches(isDisplayed()));
    }


    /*
    Each test case below this point first opens a fragment from the navigation drawer,
    and then navigates through all the sub-fragments and back. */

    @Test
    public void assetsFragmentNavigationWorks() {
        UiTestHelper.clickNavigationDrawerItem(R.id.textViewNavAssets);

        // Assert that we're now on the Assets fragment.
        onView(withId(R.id.textViewTitle)).check(matches(withText("Assets")));

        // Then open the Premises sub-fragment.
        onView(withId(R.id.cardViewPremisesAsset)).perform(click());
        onView(withId(R.id.textViewTitle)).check(matches(withText("Premises")));

        // Then go back
        onView(withId(R.id.textViewBack)).perform(click());
        onView(withId(R.id.textViewTitle)).check(matches(withText("Assets")));

        // Then open the IT sub-fragment.
        onView(withId(R.id.cardViewITAsset)).perform(click());
        onView(withId(R.id.textViewTitle)).check(matches(withText("IT")));

        // Then go back
        onView(withId(R.id.textViewBack)).perform(click());
        onView(withId(R.id.textViewTitle)).check(matches(withText("Assets")));

        // Now there should not be a back button visible.
        onView(withId(R.id.textViewBack)).check(matches(not(isDisplayed())));
    }
}
