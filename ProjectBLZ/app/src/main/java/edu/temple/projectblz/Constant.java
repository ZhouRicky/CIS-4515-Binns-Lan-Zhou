package edu.temple.projectblz;

public class Constant {

    /* URL constants */
    public static final String LOGIN_URL = "https://cis-linux2.temple.edu/~tul58076/login.php";
    public static final String REGISTER_URL = "https://cis-linux2.temple.edu/~tul58076/register.php";
    public static final String PARK_URL = "https://cis-linux2.temple.edu/~tul58076/insertpark.php";
    public static final String HISTORY_URL = "https://cis-linux2.temple.edu/~tul58076/parkinghistory.php";

    /* LocationService constants */
    public static final String LOG_LOCATION = "Location";
    public static final int FOREGROUND_SERVICE_ID = 1;

    /* Coordinate constants */
    public static final String LATITUDE = "Lat";
    public static final String LONGITUDE = "Lon";

    /* SharedPrefs constants */
    public static final String SHARED_PREFS_NAME = "ProjectBLZ";
    public static final String SHARED_PREFS_DEFAULT_STRING = "None";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String DRIVER_ID = "driverId";
    public static final String IS_LOGGED_IN = "is_logged_in";
    public static final String IS_PERMISSION_GRANTED = "is_permission_granted";

    /* LoginActivity constants */
    public static final String INVALID_INPUT = "Please enter username and/or password.";
    public static final String INCORRECT_INFO = "Username and/or Password incorrect.";

    /* SignUpActivity constants */
    public static final String ENTER_ALL_INFO = "Please enter all information.";
    public static final String PW_DO_NOT_MATCH = "Passwords do not match.";

    /* MainActivity constants */
    public static final int RequestCode_WriteSetting = 234;
    // constants for brightness
    public static final int Brightness_Zero = 0;
    public static final int Brightness_Max = 255;
    // constant for RequestCode_Permission_WriteSetting
    public static final int RequestCode_Permission_WriteSetting = 100;
}
