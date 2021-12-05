package edu.temple.projectblz;

public class Constant {

    /* URL constants */
    public static final String LOGIN_URL = "https://cis-linux2.temple.edu/~tul58076/login.php";
    public static final String REGISTER_URL = "https://cis-linux2.temple.edu/~tul58076/register.php";
    public static final String PARK_URL = "https://cis-linux2.temple.edu/~tul58076/insertpark.php";
    public static final String HISTORY_URL = "https://cis-linux2.temple.edu/~tul58076/parkinghistory.php";
    public static final String DELETE_URL = "https://cis-linux2.temple.edu/~tul58076/deleteparking.php";
    public static final String GOOGLE_MAP_URL = "http://maps.google.com/maps?daddr=";

    /* LocationService constants */
    public static final String LOG_LOCATION = "Location";
    public static final int FOREGROUND_SERVICE_ID = 1;

    /* Coordinate constants */
    public static final String LATITUDE = "park_lat";
    public static final String LONGITUDE = "park_lon";

    /* SharedPrefs constants */
    public static final String SHARED_PREFS_NAME = "ProjectBLZ";
    public static final String SHARED_PREFS_DEFAULT_STRING = "None";
    public static final String LATPARKED = "latparked";
    public static final String LOCATIONLIST = "locationlist";
    public static final String LONPARKED = "lonparked";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String CURRENTSPEED = "current_speed";
    public static final String DRIVER_ID = "driver_id";
    public static final String CREATED_AT = "createdAt";
    public static final String PARK_ID = "park_id";
    public static final String IS_LOGGED_IN = "is_logged_in";
    public static final String IS_PERMISSION_GRANTED = "is_permission_granted";

    /* LoginActivity constants */
    public static final String INVALID_INPUT = "Please enter username and/or password.";
    public static final String INCORRECT_INFO = "Username and/or Password incorrect.";

    /* SignUpActivity constants */
    public static final String ENTER_ALL_INFO = "Please enter all information.";
    public static final String PW_DO_NOT_MATCH = "Passwords do not match.";

    /* MainActivity constants */
    // constants for brightness
    public static final int Brightness_Zero = 0;
    public static final int Brightness_Max = 255;
    // constant for Speedlimit Request
    public static final String SpeedLimitLinePrefix = "    <tag k=\"maxspeed\" ";
    public static final String SpeedLimitLogForResponse = "Speed Limit from Http Response";
    public static final String TestingLineOfRequest = "The Request Line as URL";
    public static final String SpeedLimitLogForNull = "The Speedlimit was null and set to default";
    public static final String VolleyErrorPrompt = "The volley has an error:";
    public static final String OverpassAPIPrefix = "http://www.overpass-api.de/api/xapi?*[maxspeed=*][bbox=";
    // constant for brightness
    public static final String BrightnessLog = "Value For Brightness";
}
