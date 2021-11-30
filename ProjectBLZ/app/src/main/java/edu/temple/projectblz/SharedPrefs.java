package edu.temple.projectblz;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPrefs {

    private Context context;
    private SharedPreferences sharedPrefs;

    public SharedPrefs(Context initialContext) {
        context = initialContext;
        sharedPrefs = context.getSharedPreferences(Constant.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void clearAllUserSettings() {
        clearLoggedInUser();
        clearPassword();
        clearDriverId();
        clearIsLoggedIn();
        clearIsPermissionGranted();
    }


    // ================================================================================
    //      USERNAME FOR CURRENTLY LOGGED IN USER
    // ================================================================================
    public void setLoggedInUser(String username) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Constant.USERNAME, username);
        editor.apply();
    }

    public String getLoggedInUser() {
        return sharedPrefs.getString(Constant.USERNAME, Constant.SHARED_PREFS_DEFAULT_STRING);
    }

    protected void clearLoggedInUser() {
        setLoggedInUser(Constant.SHARED_PREFS_DEFAULT_STRING);
    }


    // ================================================================================
    //      PASSWORD FOR CURRENTLY LOGGED IN USER
    // ================================================================================
    public void setPassword(String password) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Constant.PASSWORD, password);
        editor.apply();
    }

    public String getPassword() {
        return sharedPrefs.getString(Constant.PASSWORD, Constant.SHARED_PREFS_DEFAULT_STRING);
    }

    protected void clearPassword() {
        setPassword(Constant.SHARED_PREFS_DEFAULT_STRING);
    }


    // ================================================================================
    //      DRIVER ID FOR CURRENTLY LOGGED IN USER
    // ================================================================================
    public void setDriverId(String driverId) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Constant.DRIVER_ID, driverId);
        editor.apply();
    }

    public String getDriverId() {
        return sharedPrefs.getString(Constant.DRIVER_ID, Constant.SHARED_PREFS_DEFAULT_STRING);
    }

    protected void clearDriverId() {
        setDriverId(Constant.SHARED_PREFS_DEFAULT_STRING);
    }


    // ================================================================================
    //      IS LOGGED IN FOR CURRENTLY LOGGED IN USER
    // ================================================================================
    public void setIsLoggedIn(Boolean isLoggedIn) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(Constant.IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public Boolean getIsLoggedIn() {
        return sharedPrefs.getBoolean(Constant.IS_LOGGED_IN, false);
    }

    protected void clearIsLoggedIn() {
        setIsLoggedIn(false);
    }


    // ================================================================================
    //      IS PERMISSION GRANTED FOR CURRENTLY LOGGED IN USER
    // ================================================================================
    public void setIsPermissionGranted(Boolean isPermissionGranted) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(Constant.IS_PERMISSION_GRANTED, isPermissionGranted);
        editor.apply();
    }

    public Boolean getIsPermissionGranted() {
        return sharedPrefs.getBoolean(Constant.IS_PERMISSION_GRANTED, false);
    }

    protected void clearIsPermissionGranted() {
        setIsPermissionGranted(false);
    }



    // ================================================================================
    //     LAST LAT PARKING LOCATION FOR CURRENTLY LOGGED IN USER
    // ================================================================================
    public void setLatParked(double lat) {
        Log.d("tag3 ","came here2 " +  lat);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Constant.LATPARKED, String.valueOf(lat));
        editor.apply();
    }

    public String getLatParked() {
        return sharedPrefs.getString(Constant.LATPARKED, Constant.SHARED_PREFS_DEFAULT_STRING);
    }

   // protected void clearPassword() {
      //  setPassword(Constant.SHARED_PREFS_DEFAULT_STRING);

    // ================================================================================
    //     LAST LON PARKING LOCATION FOR CURRENTLY LOGGED IN USER
    // ================================================================================
    public void setLonParked(double lon) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Constant.LONPARKED, String.valueOf(lon));
        editor.apply();
    }

    public String getLonParked() {
        return sharedPrefs.getString(Constant.LONPARKED, Constant.SHARED_PREFS_DEFAULT_STRING);
    }
   // }



}
