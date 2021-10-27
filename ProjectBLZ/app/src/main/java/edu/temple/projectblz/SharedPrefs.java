package edu.temple.projectblz;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {

    private Context context;
    private SharedPreferences sharedPrefs;

    public SharedPrefs(Context initialContext) {
        context = initialContext;
        sharedPrefs = context.getSharedPreferences(Constant.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void clearAllUserSettings() {
        //TODO: add clear methods here
        clearLoggedInUser();
        clearAccessLocationPermissionGranted();
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
    //      IS ACCESS FINE LOCATION PERMISSION GRANTED FOR CURRENTLY LOGGED IN USER
    // ================================================================================
    public void setAccessLocationPermissionGranted(Boolean isPermissionGranted) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(Constant.IS_LOCATION_PERMISSION_GRANTED, isPermissionGranted);
        editor.apply();
    }


    public Boolean getAccessLocationPermissionGranted() {
        return sharedPrefs.getBoolean(Constant.IS_LOCATION_PERMISSION_GRANTED, false);
    }


    protected void clearAccessLocationPermissionGranted() {
        setAccessLocationPermissionGranted(false);
    }

}
