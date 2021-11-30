package edu.temple.projectblz;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

import android.app.Activity;
import android.app.Instrumentation;
import android.view.View;

import androidx.test.espresso.ViewAssertion;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LoginActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> myActivityTestRule = new ActivityScenarioRule<>(LoginActivity.class);

    private LoginActivity myActivity = null;

    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);


    @Before
    public void setUp() throws Exception {
        myActivityTestRule.getScenario().onActivity(activity -> {
            myActivity = activity;
        });
    }

    @Test
    public void testLaunchOfMainActivityOnButtonClick(){
        assertNotNull(myActivity.findViewById(R.id.loginButton));
        onView(withId(R.id.loginButton)).perform(click());
       // Activity mainActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 8000);
      //  assertNotNull(mainActivity);
       // mainActivity.finish();
        onView(withId(R.id.saveParkButton)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() throws Exception {
        myActivity = null;
    }
}