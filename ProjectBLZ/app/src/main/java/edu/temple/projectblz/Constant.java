package edu.temple.projectblz;

public class Constant {

    // constants URL
    public static final String LOGIN_URL = "https://cis-linux2.temple.edu/~tul58076/login.php";
    public static final String REGISTER_URL = "https://cis-linux2.temple.edu/~tul58076/register.php";
    public static final String PARK_URL = "https://cis-linux2.temple.edu/~tul58076/insertpark.php";
    public static final String HISTORY_URL = "https://cis-linux2.temple.edu/~tul58076/parkinghistory.php";
    public static final String GOOGLE_MAP_URL = "http://maps.google.com/maps?daddr=";

    // constants for LocationService
    public static final String LOG_LOCATION = "Location";

    //location coordinates
    public static final String LATITUDE = "park_lat";
    public static final String LONGITUDE = "park_lon";

    // constants for SharedPrefs
    public static final String SHARED_PREFS_NAME = "ProjectBLZ";
    public static final String SHARED_PREFS_DEFAULT_STRING = "None";
    public static final String LATPARKED = "latparked";
    public static final String LOCATIONLIST = "locationlist";
    public static final String LONPARKED = "lonparked";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String DRIVER_ID = "driverId";
    public static final String CREATED_AT = "createdAt";
    public static final String PARK_ID = "park_id";
    public static final String IS_LOGGED_IN = "is_logged_in";
    public static final String IS_PERMISSION_GRANTED = "is_permission_granted";

    // constants for LoginActivity
    public static final String INVALID_INPUT = "Please enter username and/or password.";
    public static final String INCORRECT_INFO = "Username and/or Password incorrect.";

    // constants for SignUpActivity
    public static final String ENTER_ALL_INFO = "Please enter all information.";
    public static final String PW_DO_NOT_MATCH = "Passwords do not match.";

}
