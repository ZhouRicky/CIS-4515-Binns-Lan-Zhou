package edu.temple.projectblz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {

    private SharedPrefs sharedPrefs;
    public ArrayList<LocationObject> locationList;
    private MapView map;
    private IMapController mapController;
    private GeoPoint startPoint;
    private Marker startMarker;
    private Drawable drawable;
    private LocationManager locationManager;
    private Location myLocation;
    private LocationService myService;
    private double lat, lon;
    private int speedLimitCount = 0;
    private TextToSpeech textToSpeech;
    private String username, password, driverId;
    private int playedSpeech = 0;
    private int checkPlayedSpeech = 0;
    private TextView speedLimitValue, currentSpeedValue;
    private int currentSpeed = 0;
    public int speedLimit = 15;
    public int color;
    private boolean speedFlag = false;
    private boolean silenceFlag = false;
    private ImageButton speakButton;
    private ImageButton silentButton;
    boolean requestfound = false;
    RequestQueue queue;

    private int[] speedArray = {15, 20, 25, 30, 35, 40, 45, 50, 55};

    CardView currentSpeedCard;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    //Initializing a sensor, sensormanager
    SensorManager sensorManager;
    Sensor LightSensor, AccelerometerSensor;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      
        new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        /* ensures that the map has a writable location for the map cache*/
        Configuration.getInstance().load(getApplication(), PreferenceManager.getDefaultSharedPreferences(getApplication()));
        sharedPrefs = new SharedPrefs(this);
        queue = Volley.newRequestQueue(this);


        //request location & writing to system permission
        //initialization of the sensor and manager
        Initialization();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("driverMood"));
        // TODO: Get all of those into Initialization(). easier to read
        //  RZ comment: maybe

        createNotificationChannel();

        getStartService();

        locationManager = getSystemService(LocationManager.class);

        checkPermission();
        locationList = new ArrayList<>();
        speedLimitValue = findViewById(R.id.speedLimitValueTextView);
        currentSpeedValue = findViewById(R.id.currentSpeedValueTextView);
        currentSpeedCard = findViewById(R.id.currentSpeedCardView);

        username = sharedPrefs.getLoggedInUser();
        password = sharedPrefs.getPassword();
        driverId = sharedPrefs.getDriverId();

        silentButton = findViewById(R.id.silentButton);
        speakButton = findViewById(R.id.speakButton);

        /* text to speech will be on by default*/
        silentButton.setOnClickListener(v -> {
            speakButton.setVisibility(View.VISIBLE);
            silentButton.setVisibility(View.INVISIBLE);
            silenceFlag = true;
        });

        speakButton.setOnClickListener(v -> {
            silentButton.setVisibility(View.VISIBLE);
            speakButton.setVisibility(View.INVISIBLE);
            silenceFlag = false;
        });


        // ================================================================================
        //      Navigation Drawer Code Start
        // ================================================================================

        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.myDrawerLayout);
        navigationView = findViewById(R.id.myNavigationView);
        // navigationView.setBackgroundColor(Color.BLUE);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(this);

        // ================================================================================
        //      Navigation Drawer Code End
        // ================================================================================

        //map initialization

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        mapController = map.getController();
        if (myLocation != null) {
            startPoint = new GeoPoint(myLocation.getLatitude(), myLocation.getLongitude());
            mapController.setCenter(startPoint);
            mapController.setZoom(19.5);
            startMarker = new Marker(map);
            startMarker.setPosition(startPoint);
            startMarker.setTitle("You are here");
        }

        drawable = getResources().getDrawable(R.drawable.red_car_marker);
        //if(drawable!=null){
        try {
            startMarker.setIcon(drawable);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        } catch (Exception e) {
            e.printStackTrace();
        }

        map.getOverlays().add(startMarker);

        findViewById(R.id.saveParkButton).setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Save Parking Location")
                    .setMessage("Do you want to save your parking location?")
                    .create();

            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Yes", (dialog, which) -> savePark());

            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "No", (dialog, which) -> dialog.cancel());

            alertDialog.show();
        });
        textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.ENGLISH);

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.d("mtag", "Language not supported ");
                }
            } else {
                Log.d("mtag", "Initialization failed");
            }
        });
        textToSpeech.speak("testing testing 1 2 3", TextToSpeech.QUEUE_FLUSH, null, null);
    }

    /**
     * this will refresh the osmdroid configuration on resuming
     * Registering Listener
     */
    public void onResume() {
        super.onResume();
        checkPermission();
        //map.onResume();

        // TODO: this should also go into onCreate?
        //register light sensor and accelerometer sensor
        sensorManager.registerListener(this, LightSensor, sensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, AccelerometerSensor, sensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * this will enable osmdroid to be refreshed
     * UnregisterListener
     */
    public void onPause() {
        super.onPause();
        map.onPause();
        //onpause unregister the sensor
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        getEndService();
        super.onDestroy();
    }

    /*initialization of
               1.sensor Manager
               2.Light Sensor
               3.AccelerometerSensor
         */
    private void Initialization() {
        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        LightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        AccelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    // uses dexter library to check for permissions at runtime
    private void checkPermission() {
        Dexter.withContext(this).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            sharedPrefs.setIsPermissionGranted(true);
                            myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }

                        if (multiplePermissionsReport.getDeniedPermissionResponses().size() > 0) {
                            sharedPrefs.clearIsPermissionGranted();
                            Toast.makeText(MainActivity.this, "All permissions are required to continue", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), "");
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

        // checks for WRITE_SETTINGS permission
        if (!Settings.System.canWrite(this)) {
            Toast.makeText(this, "Modify system settings must be allowed", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), "");
            intent.setData(uri);
            startActivity(intent);
        }
    }

    // TODO: Implement Alert notify brightness caution
    //  RZ comment: I changed the permission check code, so see where this can fit


    /**
     * this function saves the drivers parking location
     */
    private void savePark() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.PARK_URL,
                response -> {
                    Log.d("mtag", "came60 " + "sup");
                    Log.d("JSON", String.valueOf(response));

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("status").equals("success")) {
                            sharedPrefs.setLatParked(String.valueOf(lat));
                            Log.d("mtag", "came61 " + "sup2");
                            sharedPrefs.setLonParked(String.valueOf(lon));
                            Toast.makeText(this, "Location saved", Toast.LENGTH_SHORT).show();
                            Log.d("JSON", "success: " + jsonObject.getString("message"));
                        } else if (jsonObject.getString("status").equals("error")) {
                            Log.d("JSON", "error: " + jsonObject.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "try/catch error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> VolleyLog.d("Error", "Error: " + error.getMessage())) {
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("park_lat", String.valueOf(lat));
                params.put("park_lon", String.valueOf(lon));
                params.put("driver_id", driverId);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void parkingHistory() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.HISTORY_URL,
                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("status").equals("success")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            getArraylist(jsonArray);

                            /* send over the list of locations from db with this intent*/
                            Intent newIntent = new Intent(MainActivity.this, ParkingItemsActivity.class);
                            newIntent.putExtra(Constant.LOCATIONLIST, locationList);
                            startActivity(newIntent);
                            finish();
                        } else if (jsonObject.getString("status").equals("error")) {
                            Log.d("JSON", "error: " + jsonObject.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "try/catch error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> VolleyLog.d("Error", "Error: " + error.getMessage())) {
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                params.put("driver_id", driverId);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    /**
     * connect to the service
     */
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myService = ((LocationService.MyLocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /* this function starts a service*/
    private void getStartService(){
        Intent intent = new Intent(this, LocationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /* this function end a service*/
    private void getEndService(){
        Intent intent = new Intent(this, LocationService.class);
        unbindService(serviceConnection);
        stopService(intent);
    }

    /* a notification is required for a foreground service*/
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(
                "driver"
                , "Start Driving"
                , NotificationManager.IMPORTANCE_DEFAULT
        );

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    /* receive the driver location from services*/
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            speedLimitCount++;

            /* get data from location services*/
            lat = intent.getDoubleExtra(Constant.LATITUDE, 0);
            lon = intent.getDoubleExtra(Constant.LONGITUDE, 0);

            /* set a flag to ensure starting speed, and get current speed from location services*/
            if(speedFlag){
                currentSpeed = (int) intent.getFloatExtra(Constant.CURRENTSPEED, 0);
                if(currentSpeed != 0) {
                    currentSpeedValue.setText(String.valueOf(currentSpeed));
                }
            } else {
                currentSpeed = 0;
            }
            //currentSpeedValue.setText(String.valueOf(currentSpeed));
            speedFlag = true;

            //new UpdatingSpeedLimit().execute();

            /* get the speed limit for the current road segment*/
            if((speedLimitCount % 10) == 0){
                speedLimit = getSpeedLimit();
                speedLimitValue.setText(String.valueOf(speedLimit));
            }

            /* check if we need to send a warning to driver - see if they are approaching the speed limit */
            checkWarning(currentSpeed, speedLimit);
            currentSpeedCard.setCardBackgroundColor(color);

            /* playedspeech is a value assigned to a string that has just been played, check played ensures we don't give the same warning back to back*/
            if(!silenceFlag) {//Silence flag is used to check whether or not we should use text too speech
                Log.d("Speech", "playedSpeech: " + playedSpeech + "   checkPlayedSpeech: " + checkPlayedSpeech);
                if (playedSpeech != checkPlayedSpeech) {
                    getSpeech(playedSpeech);
                }
            }
            checkPlayedSpeech = playedSpeech;

            //TODO: when car stops, speed should be zero, check how to send such message - we collect speed in broadcast receiver
            Log.d("lat", "this lat " + lat);
            if (startMarker == null) {
                startPoint.setCoords(lat, lon);
                startMarker.setPosition(startPoint);
                startMarker.setTitle("You are here");
                drawable = getResources().getDrawable(R.drawable.red_car_marker);
                startMarker.setIcon(drawable);
                //startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                // map.getOverlays().add(startMarker);
            } else {
                GeoPoint geoPoint = new GeoPoint(lat, lon);
                startPoint.setCoords(lat, lon);
                startMarker.setRotation((float) lat);
                startMarker.setRotation((float) startPoint.bearingTo(geoPoint));
                startMarker.setPosition(startPoint);
            }
            mapController.setCenter(startPoint);
            mapController.setZoom(17.5);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            map.getOverlays().add(startMarker);
        }
    };

    /**populate an arraylist with database parking info*/
    private void getArraylist(JSONArray list) throws JSONException {
        final int arraySize = list.length();
        for(int i = 0; i < arraySize; i++) {
            JSONObject object = list.getJSONObject(i);
            locationList.add(new LocationObject(object.getDouble(Constant.LATITUDE), object.getDouble(Constant.LONGITUDE), object.getInt(Constant.PARK_ID), object.getString(Constant.CREATED_AT), object.getInt(Constant.DRIVER_ID)));
        }
    }

    protected int getSpeedLimit(){
        Random random = new Random();
        int limit;
        limit = speedArray[random.nextInt(speedArray.length)];
        return limit;
    }

    /**this function gives the speed warning*/
    public void checkWarning(int currentSpeed, int speedLimit){
      /*
        int statement1 = (Integer.valueOf(speedLimitValue.getText().toString()) - Integer.valueOf(currentSpeedValue.getText().toString()));
        int statement2 =(Integer.valueOf(currentSpeedValue.getText().toString()) - Integer.valueOf(speedLimitValue.getText().toString()));

        if(statement1 >=5){
            currentSpeedCard.setCardBackgroundColor(Color.WHITE);
            playedSpeech = 0;
        }
        else if(statement1<=3&&statement1>0) {
            currentSpeedCard.setCardBackgroundColor(Color.YELLOW);
            //  textToSpeech.speak(preWarning, TextToSpeech.QUEUE_ADD, null, null);
            playedSpeech = 1;
        }
        else if(statement1 == 0){
            currentSpeedCard.setCardBackgroundColor(Color.MAGENTA);
            // textToSpeech.speak(atLimit, TextToSpeech.QUEUE_ADD, null, null);
            playedSpeech = 2;
        }
        else if(statement2 >= 5){
            currentSpeedCard.setCardBackgroundColor(Color.RED);
            //  textToSpeech.speak(postWarning, TextToSpeech.QUEUE_ADD, null, null);
            playedSpeech = 3;
        }
        */
      
        if((speedLimit - currentSpeed) <= 3 && (speedLimit - currentSpeed) > 0){
            //String preWarning = "You are approaching the speed limit";
            color = Color.YELLOW;
            //textToSpeech.speak(preWarning, TextToSpeech.QUEUE_ADD, null, null);
            playedSpeech = 1;
        }
        else if((speedLimit - currentSpeed) == 0){
            //String atLimit = "You are at the speed limit";
            color = Color.MAGENTA;
            //textToSpeech.speak(atLimit, TextToSpeech.QUEUE_ADD, null, null);
            playedSpeech = 2;
        }
        else if((currentSpeed - speedLimit) >= 5){
            //String postWarning = "You are over the speed limit";
            color = Color.RED;
            //textToSpeech.speak(postWarning, TextToSpeech.QUEUE_ADD, null, null);
            playedSpeech = 3;
        }
        else if((speedLimit - currentSpeed) >= 5){
            color = Color.WHITE;
            playedSpeech = 0;
        }
    }

    /**this function plays the appropriate speech for the speed the driver is going*/
    private void getSpeech(int value){
        switch (value){
            case 1:
                String preWarning = "You are approaching the speed limit";
                textToSpeech.speak(preWarning, TextToSpeech.QUEUE_ADD, null, null);
                break;
            case 2:
                String atLimit = "You are at the speed limit";
                textToSpeech.speak(atLimit, TextToSpeech.QUEUE_ADD, null, null);
                break;
            case 3:
                String postWarning = "You are over the speed limit";
                textToSpeech.speak(postWarning, TextToSpeech.QUEUE_ADD, null, null);
                break;
            case 0:
                String noWarning = "You are well below the speed limit";
                textToSpeech.speak(noWarning, TextToSpeech.QUEUE_ADD, null, null);
                break;
        }
    }

    /**
     * menu items to be selected
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.shareButton) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, showAddress(lat, lon));
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        }
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override//options for share button
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()) {//TODO: TEST THE CONDITION OF THE IF STATEMENT
            case R.id.nav_last_parked:
                if (sharedPrefs.getLonParked() != "None" || sharedPrefs.getLatParked() != null) {
                   /* Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse(Constant.GOOGLE_MAP_URL + Double.valueOf(sharedPrefs.getLatParked()) + "," + Double.valueOf(sharedPrefs.getLonParked())));
                    startActivity(intent);*/
                    Log.d("mtag", "CAME HERE 41 " + sharedPrefs.getLatParked());
                    getLastAddress();
                } else {
                    Toast.makeText(this, "No recent parked location, please check parking history", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_parking_history:
                parkingHistory();
                break;
            case R.id.nav_logout:
                sharedPrefs.clearAllUserSettings();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getLastAddress() {
        //String addressReturned = ;
        /* confirm if the driver wants to navigate to the address found in the list*/
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_menu_directions)
                .setTitle(showAddress(Double.parseDouble(sharedPrefs.getLatParked()), Double.parseDouble(sharedPrefs.getLonParked())))
                .setMessage("Do you want to navigate to this address?")
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse(Constant.GOOGLE_MAP_URL + Double.valueOf(sharedPrefs.getLatParked()) + "," + Double.valueOf(sharedPrefs.getLonParked())));
                        startActivity(intent);
                    }
                })
                .setPositiveButton("No", null)
                .show();

    }

    /**
     * this function gets the actual address form the lat and lon coordinates
     */
    public String showAddress(double lat, double lon) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        String address = null;

        /* try catch  - to prevent null exceptions*/
        try {
            /* Here 1 represent max location result to returned, by documents it recommended 1 to 5*/
            addresses = geocoder.getFromLocation(lat, lon, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* double check if address is empty*/
        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "Sorry, no address found ", Toast.LENGTH_SHORT).show();
        } else {
            /* If any additional address line present than only 1, check with max available address lines by getMaxAddressLineIndex()*/
            address = addresses.get(0).getAddressLine(0);
        }
        return address;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
            setBrightness((int) sensorEvent.values[0]);
        }
    }

    private void setBrightness(int brightness) {
        // TODO: Implement Algorithm for brightness control
        //  RZ comment: this doesn't really do anything rn

        if (brightness < Constant.Brightness_Zero) {
            brightness = Constant.Brightness_Zero;
        } else if (brightness > Constant.Brightness_Max) {
            brightness = Constant.Brightness_Max;
        }
        Log.d(Constant.BrightnessLog, "Brightness is: " + brightness);
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // TODO: override for interface, would have to implement if needed for accerlerometer sensor
    }

    public void getmaxSpeed(String latitude, String longitude, String maxlat, String maxlon) {
        String RequestURL = Constant.OverpassAPIPrefix + longitude + "," + latitude + "," + maxlon + "," + maxlat + "]";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, RequestURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                speedLimit = responseParse(response);
                Log.d(Constant.TestingLineOfRequest, RequestURL);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(Constant.VolleyErrorPrompt, error.toString());
            }
        }) {

        };
        queue.add(stringRequest);

    }


    public int responseParse(String response) {
        Scanner scanner = new Scanner(response);
        String SpeedLimit = null;
        int SpeedLimits = -1;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith(Constant.SpeedLimitLinePrefix)) {
                requestfound = true;
                SpeedLimit = line.replaceAll("[^0-9]", "");
                Log.d(Constant.SpeedLimitLogForResponse, "The speed limit is :" + SpeedLimit);
                Log.d(Constant.TestingLineOfRequest, line);
                SpeedLimits = Integer.valueOf(SpeedLimit);
                speedLimitValue.setText(String.valueOf(speedLimit));
                break;

                // process the line
            }
            if (SpeedLimit == null) {
                //SpeedLimit = "25";
                Log.d(Constant.SpeedLimitLogForNull, "The speed limit is :" + SpeedLimit);
                requestfound = false;
                //TODO Better algorithm to stays constant

            }

       /* try {
            //get json object from (whole) response string
            JSONObject responseObj = XML.toJSONObject(response);
            Log.d("Array",response.toString());
            //pull out jsonArray (of elements) from object
            JSONArray responseArr = responseObj.getJSONArray("meta");

            //empty previous road list
            roadArrList = new ArrayList<>();

            //for each element item in json array
            for (int i = 0; i < responseArr.length(); i++) {
                //convert element to string of contents
                String elementStr = responseArr.getString(i);
                //create individual element object from (whole) element string
                JSONObject elementObj = new JSONObject(elementStr);

                //pass element (road) for road data string extraction (and adding to list)
                getCardData(elementObj);

            }
        } catch (Exception e) {
            //todo: handle error
            e.printStackTrace();
        }

        //set recyclerView with road cards

        */
        }
        return SpeedLimits;
    }


    class UpdatingSpeedLimit extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            checkWarning(currentSpeed, speedLimit);


            if (lat != 0 || lon != 0) {
                if (speedLimitCount == 0)
                    getmaxSpeed(String.valueOf(lat), String.valueOf(lon), String.valueOf(lat + 0.0001), String.valueOf(lon + 0.0001));
                else if (speedLimitCount >= 10 && speedLimitCount % 10 == 0) {
                    getmaxSpeed(String.valueOf(lat), String.valueOf(lon), String.valueOf(lat + 0.0001), String.valueOf(lon + 0.0001));

                }
            }

            if (requestfound == false) {
                if (speedLimitCount == 0) {
                    speedLimit = 25;
                    speedLimitValue.setText(String.valueOf(speedLimit));
                    checkWarning(currentSpeed, speedLimit);
                }
                if (speedLimitCount >= 10 && speedLimitCount % 10 == 0) {
                    boolean flag = true;
                    while (true) {
                        int temp = getSpeedLimit();
                        if (Math.abs(temp - currentSpeed) <= 30) {
                            speedLimit = temp;
                            speedLimitValue.setText(String.valueOf(speedLimit));
                            checkWarning(currentSpeed, speedLimit);
                            break;
                        }
                    }


                }
            } else {
                checkWarning(currentSpeed, speedLimit);
            }

            Log.d("Done","Done background");
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            if (!silenceFlag) {//Silence flag is used to check whether or not we should use text too speech
                if (playedSpeech != checkPlayedSpeech) {
                    getSpeech(playedSpeech);
                }
            }
            checkPlayedSpeech = playedSpeech;
            Log.d("OnPost","Done onpost");

        }
    }
}

