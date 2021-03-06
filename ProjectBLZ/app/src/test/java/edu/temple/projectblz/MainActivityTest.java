package edu.temple.projectblz;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.graphics.Color;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;

@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {

    MainActivity mainActivity = new MainActivity();
    int speedLimit, currentSpeed;
    ArrayList<LocationObject> listItem = mainActivity.locationList;
    ParkingAdapter parkingAdapter = new ParkingAdapter(ApplicationProvider.getApplicationContext(), listItem);


    // test the starting value for the speed limit
    @Test
    public void testSpeedLimitValueIsFifteen() {
        speedLimit = mainActivity.speedLimit;
        assertEquals("Should be initially set to 25", 25, speedLimit);
    }

    // test the speed limit array values, ensuring that one of the values in the array set is returned
    @Test
    public void testSpeedLimitArrayValues(){
        boolean checked25 = false, checked30 = false, checked35 = false, checked40 = false, checked45 = false, checked50 = false, checked55 = false;

        while(!checked25 || !checked30 || !checked35 || !checked40 || !checked45 || !checked50 || !checked55) {
            speedLimit = mainActivity.getSpeedLimit();
            assertThat("Should match any of the speed values within array", speedLimit, anyOf(is(25), is(30), is(35), is(40), is(45), is(50), is(55)));
            switch(speedLimit) {
                case 25:
                    checked25 = true;
                    break;
                case 30:
                    checked30 = true;
                    break;
                case 35:
                    checked35 = true;
                    break;
                case 40:
                    checked40 = true;
                    break;
                case 45:
                    checked45 = true;
                    break;
                case 50:
                    checked50 = true;
                    break;
                case 55:
                    checked55 = true;
                    break;
            }
        }
    }

    // test for change in color when speed gets close to speed limit
    // uses the same if conditions as in MainActivity
    @Test
    public void testWarningColorChange() {
        speedLimit = 15;
        currentSpeed = 9;
        boolean checkedYellow = false, checkedMagenta = false, checkedRed = false, checkedWhite = false, checkedWhiteAgain = false;

        while(!checkedYellow || !checkedMagenta || !checkedRed || !checkedWhite || !checkedWhiteAgain) {
            mainActivity.checkWarning(currentSpeed, speedLimit);
            System.out.println(currentSpeed);
            if ((speedLimit - currentSpeed) <= 3 && (speedLimit - currentSpeed) > 0) {
                assertEquals("Should be yellow", Color.YELLOW, mainActivity.color);
                checkedYellow = true;
            } else if ((speedLimit - currentSpeed) == 0) {
                assertEquals("Should be magenta", Color.MAGENTA, mainActivity.color);
                checkedMagenta = true;
            } else if ((currentSpeed - speedLimit) >= 0) {
                assertEquals("Should be red", Color.RED, mainActivity.color);
                checkedRed = true;
            } else if ((speedLimit - currentSpeed) >= 5) {
                assertEquals("Should be white", Color.WHITE, mainActivity.color);
                checkedWhite = true;
            } else { // This case should only happen if currentSpeed is exactly 4 less than speedLimit
                assertEquals("Should also be white", Color.WHITE, mainActivity.color);
                checkedWhiteAgain = true;
            }
            if(checkedYellow && checkedMagenta && checkedRed && checkedWhite) {
                currentSpeed = 11;
            } else {
                currentSpeed += 3;
            }
        }
    }

    // test initial parking history array list size
    @Test
    public void testParkingHistoryArrayListShouldBeNull() {
        assertNull("Should be null on initialization", listItem);
        assertNull("Should be null on initialization", parkingAdapter.listItem);
    }

    // test while loop for speed limit randomizer
    @Test
    public void testLoopForSpeedLimits() {
        boolean isLessThan = false;
        int currSpeedLimit = 25;

        speedLimit = mainActivity.getSpeedLimit();
        while(Math.abs(speedLimit - currSpeedLimit) > 15) {
            speedLimit = mainActivity.getSpeedLimit();
        }

        if(speedLimit <= currSpeedLimit + 15) {
            isLessThan = true;
        }

        assertTrue(isLessThan);
    }
}
