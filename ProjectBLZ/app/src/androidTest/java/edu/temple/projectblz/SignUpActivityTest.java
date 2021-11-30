package edu.temple.projectblz;

import static org.junit.Assert.*;

import android.app.Activity;
import android.view.View;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SignUpActivityTest {

    @Rule
    public ActivityScenarioRule<SignUpActivity> myActivityTestRule = new ActivityScenarioRule<>(SignUpActivity.class);

    private SignUpActivity myActivity = null;

    @Before
    public void setUp() throws Exception {
       myActivityTestRule.getScenario().onActivity(activity -> {
           myActivity = activity;
        });
    }

    @Test
    public void testLaunch(){
        View view = myActivity.findViewById(R.id.firstNameEditText);
        /**Not null proves that the activity was launch, because the appcompat.widget...editText is present*/
        assertNotNull(view);// - test passed
       // assertNull(view); - will fail test
    }

    @After
    public void tearDown() throws Exception{
        myActivity = null;
    }
}