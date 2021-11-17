package edu.temple.projectblz;

public class Constant {

    // constants URL
    public static final String LOGIN_URL = "https://cis-linux2.temple.edu/~tul58076/login.php";
    public static final String REGISTER_URL = "https://cis-linux2.temple.edu/~tul58076/register.php";
    public static final String PARK_URL = "https://cis-linux2.temple.edu/~tul58076/insertpark.php";
    public static final String HISTORY_URL = "https://cis-linux2.temple.edu/~tul58076/parkinghistory.php";

    // constants for LocationService
    public static final String LOG_LOCATION = "Location";
    public static final int FOREGROUND_SERVICE_ID = 1;


    //location coordinates
    public static final String LATITUDE = "Lat";
    public static final String LONGITUDE = "Lon";

    // constants for SharedPrefs
    public static final String SHARED_PREFS_NAME = "ProjectBLZ";
    public static final String SHARED_PREFS_DEFAULT_STRING = "None";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String DRIVER_ID = "driverId";
    public static final String IS_LOGGED_IN = "is_logged_in";
    public static final String IS_PERMISSION_GRANTED = "is_permission_granted";

    // constants for LoginActivity
    public static final String INVALID_INPUT = "Please enter username and/or password.";
    public static final String INCORRECT_INFO = "Username and/or Password incorrect.";
    public static final String LoginPhp = "http://192.168.1.78/login.php";

    // constants for SignUpActivity
    public static final String ENTER_ALL_INFO = "Please enter all information.";
    public static final String PW_DO_NOT_MATCH = "Passwords do not match.";
    public static final String RegisterPhp = "http://192.168.1.78/register.php";

    //Constant for MainActivity
        //Constant for Tag
        public static final String TAG = "Main Activity";

        //Constant for Volley
        public static final String SUCCESS_CODE = "success";
        //Constant for RequestCode
        public static final int RequestCode_FineLocation = 123;
        public static final int RequestCode_WriteSetting = 234;
        //Constant for brightness
        public static final int Brightness_Zero = 0;
        public static final int Brightness_Max = 255;
        //Constant for URL
        public static final String ParkPhp = "http://192.168.1.78/insertpark.php";
        //Constant for Parking
        //Modified Constant
        public static final String ParkingLatitude = "park_lat";
        public static final String ParkingLongitude = "park_lon";
        public static final String ParkingDriverID= "driver_id";
        //Constant for RequestCode_Permission_WriteSetting
        public static final int RequestCode_Permission_WriteSetting = 100;




}
