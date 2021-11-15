package edu.temple.projectblz;

public class Constant {

    // constants URL
    public static final String LOGIN_URL = "http://cis-linux2.temple.edu/~tul58076/login.php";
    public static final String REGISTER_URL = "http://cis-linux2.temple.edu/~tul58076/register.php";
    public static final String PARK_URL = "http://cis-linux2.temple.edu/~tul58076/insertpark.php";

    // constants for LocationService
    public static final String LOG_LOCATION = "Location";

    //location coordinates
    public static final String LATITUDE = "Lat";
    public static final String LONGITUDE = "Lon";

    // constants for SharedPrefs
    public static final String SHARED_PREFS_NAME = "ProjectBLZ";
    public static final String SHARED_PREFS_DEFAULT_STRING = "None";
    public static final String USERNAME = "username";
    public static final String IS_LOGGED_IN = "is_logged_in";
    public static final String IS_PERMISSION_GRANTED = "is_permission_granted";

    // constants for LoginActivity
    public static final String INVALID_INPUT = "Please enter username and/or password.";
    public static final String INCORRECT_INFO = "Username and/or Password incorrect.";

    // constants for SignUpActivity
    public static final String ENTER_ALL_INFO = "Please enter all information.";
    public static final String PW_DO_NOT_MATCH = "Passwords do not match.";

}
