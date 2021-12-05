package edu.temple.projectblz;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This test requires the user to not be logged in; If user is logged in, please logout of application first before running test.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class FullAppTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> loginActivityActivityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setup() {
        ActivityScenario.launch(LoginActivity.class);
    }

    @Test
    public void fullTest() {
        // test navigation to sign up page
        onView(withId(R.id.signUpTextView)).perform(click());
        onView(withId(R.id.createAccountButton)).perform(click());
        onView(withId(R.id.cancelTextView)).perform(click());

        // test login
        String username = "Rickayyy";
        String password = "pass";
        onView(withId(R.id.usernameEditText)).perform(ViewActions.typeText(username));
        onView(withId(R.id.passwordEditText)).perform(ViewActions.typeText(password));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.loginButton)).perform(click());

        // in some cases, the view doesn't load fast enough; sleep for 1 second to allow for view to load
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // test silent button
        onView(withId(R.id.silentButton)).perform(click());
        onView(withId(R.id.speakButton)).perform(click());

        // test last parked location, should be null on clean run
        onView(withId(R.id.myDrawerLayout)).check(matches(isClosed())).perform(DrawerActions.open());
        onView(withId(R.id.nav_last_parked)).perform(click());

        // test parking history
        onView(withId(R.id.myDrawerLayout)).check(matches(isClosed())).perform(DrawerActions.open());
        onView(withId(R.id.nav_parking_history)).perform(click());
        onView(withId(R.id.parkingItemsRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withText("No")).inRoot(isDialog()).perform(click());
        onView(withId(R.id.closeItemBtn)).perform(click());

        // test save parking location
        onView(withId(R.id.saveParkButton)).perform(click());
        onView(withText("Yes")).inRoot(isDialog()).perform(click());

        // test last parked location again, this time will have a saved location
        onView(withId(R.id.myDrawerLayout)).check(matches(isClosed())).perform(DrawerActions.open());
        onView(withId(R.id.nav_last_parked)).perform(click());
        onView(withText("No")).inRoot(isDialog()).perform(click());

        // test parking history, this time a new item will be added to top of list
        // test delete button
        onView(withId(R.id.myDrawerLayout)).check(matches(isClosed())).perform(DrawerActions.open());
        onView(withId(R.id.nav_parking_history)).perform(click());
        onView(withId(R.id.parkingItemsRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withText("No")).inRoot(isDialog()).perform(click());
        onView(withId(R.id.parkingItemsRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.deleteImageView)));
        onView(withText("No")).inRoot(isDialog()).perform(click());
        onView(withId(R.id.parkingItemsRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.deleteImageView)));
        onView(withText("Yes")).inRoot(isDialog()).perform(click());
        onView(withId(R.id.closeItemBtn)).perform(click());

        // test logout
        onView(withId(R.id.myDrawerLayout)).check(matches(isClosed())).perform(DrawerActions.open());
        onView(withId(R.id.nav_logout)).perform(click());
    }

    // function to click child items within the recycler view index
    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified id.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                v.performClick();
            }
        };
    }
}
