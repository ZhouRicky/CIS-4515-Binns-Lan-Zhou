package edu.temple.projectblz;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

import android.app.Activity;
import android.app.Instrumentation;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ParkingItemsActivityTest {

    @Rule
    public ActivityScenarioRule<ParkingItemsActivity> myActivityTestRule = new ActivityScenarioRule<>(ParkingItemsActivity.class);

    private ParkingItemsActivity myActivity = null;

    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);


    @Before
    public void setUp() throws Exception {
        myActivityTestRule.getScenario().onActivity(activity -> {
            myActivity = activity;
        });
    }

    @Test
    public void testLaunchOfMainActivityOnCloseButtonClick(){
        assertNotNull(myActivity.findViewById(R.id.closeItemBtn));
        onView(withId(R.id.closeItemBtn)).perform(click());
        Activity mainActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 8000);
        assertNotNull(mainActivity);
        mainActivity.finish();
    }

    @After
    public void tearDown() throws Exception {
        myActivity = null;
    }
}