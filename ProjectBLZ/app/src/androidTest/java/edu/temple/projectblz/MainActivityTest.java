package edu.temple.projectblz;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import android.view.View;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> myActivityTestRule = new ActivityScenarioRule<>(MainActivity.class);

    private MainActivity myActivity = null;

    @Before
    public void setUp() throws Exception {
        myActivityTestRule.getScenario().onActivity(activity -> {
            myActivity = activity;
        });
    }

    @Test
    public void testLaunch(){
        View view = myActivity.findViewById(R.id.speedLimitValueTextView);
        /**Not null proves that the activity was launch, because the appcompat.widget...editText is present*/
        assertNotNull(view);// - test passed
        // assertNull(view); - will fail test
    }

    /**this test the starting value for the speed limit*/
    @Test
    public void testSpeedLimitValueIsTen(){
        int speedLimit = myActivity.speedLimit;
        assertEquals(10, speedLimit);
    }

    /**this test the speed limit array values - ensuring that one of the values in the array set is returned*/
    @Test
    public void testSpeedLimitArrayValues(){
        int speedLimit = myActivity.getSpeedLimit();
        assertThat(speedLimit, anyOf(is(10), is(15), is(25), is(30), is(35) ,is(40), is(45), is(50), is(55), is(65), is(70)));
    }

    @After
    public void tearDown() throws Exception {
        myActivity = null;
    }
}