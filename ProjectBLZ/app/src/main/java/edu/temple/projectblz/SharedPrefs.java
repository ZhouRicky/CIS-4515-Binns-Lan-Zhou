package edu.temple.projectblz;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {

    public static final String SHARED_PREFS_NAME = "ProjectBLZ";
    public static final String SHARED_PREFS_DEFAULT_STRING = "None";

    public static final String USERNAME = "username";
    public static final String IS_LOCATION_PERMISSION_GRANTED = "is_location_permission_granted";

    private Context context;
    private SharedPreferences sharedPrefs;

    public SharedPrefs(Context initialContext) {
        context = initialContext;
        sharedPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void clearAllUserSettings() {
        //TODO: empty for now, but add clear methods here
        clearLoggedInUser();
        clearAccessLocationPermissionGranted();
    }

    // ================================================================================
    //      USERNAME FOR CURRENTLY LOGGED IN USER
    // ================================================================================
    public void setLoggedInUser(String username) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(USERNAME, username);
        editor.apply();
    }

    public String getLoggedInUser() {
        return sharedPrefs.getString(USERNAME, SHARED_PREFS_DEFAULT_STRING);
    }

    protected void clearLoggedInUser() {
        setLoggedInUser(SHARED_PREFS_DEFAULT_STRING);
    }

    // ================================================================================
    //      IS ACCESS FINE LOCATION PERMISSION GRANTED FOR CURRENTLY LOGGED IN USER
    // ================================================================================
    public void setAccessLocationPermissionGranted(Boolean isPermissionGranted) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(IS_LOCATION_PERMISSION_GRANTED, isPermissionGranted);
        editor.apply();
    }


    public Boolean getAccessLocationPermissionGranted() {
        return sharedPrefs.getBoolean(IS_LOCATION_PERMISSION_GRANTED, false);
    }


    protected void clearAccessLocationPermissionGranted() {
        setAccessLocationPermissionGranted(false);
    }

}
