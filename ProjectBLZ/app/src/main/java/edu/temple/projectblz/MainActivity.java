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
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {

    SharedPrefs sharedPrefs;
    ArrayList<LocationObject> locationList;
    MapView map;
    IMapController mapController;
    GeoPoint startPoint;
    Marker startMarker;
    Drawable drawable;
    LocationManager locationManager;
    Location myLocation;
    LocationService myService;
    double lat, lon;

    String username, password, driverId;

    TextView speedLimitValue, currentSpeedValue;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    //Initializing a sensor, sensormanager
    SensorManager sensorManager;
    Sensor LightSensor,AccelerometerSensor;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** ensures that the map has a writable location for the map cache*/
        Configuration.getInstance().load(getApplication(), PreferenceManager.getDefaultSharedPreferences(getApplication()));
        sharedPrefs = new SharedPrefs(this);


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

        username = sharedPrefs.getLoggedInUser();
        password = sharedPrefs.getPassword();
        driverId = sharedPrefs.getDriverId();

        // ================================================================================
        //      Navigation Drawer Code Start
        // ================================================================================

        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.myDrawerLayout);
        navigationView = findViewById(R.id.myNavigationView);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        if(myLocation!=null) {
            startPoint = new GeoPoint(myLocation.getLatitude(), myLocation.getLongitude());
            mapController.setCenter(startPoint);
            mapController.setZoom(19.5);
            startMarker = new Marker(map);
            startMarker.setPosition(startPoint);
            startMarker.setTitle("You are here");
        }

        drawable = getResources().getDrawable(R.drawable.red_car_marker);
        startMarker.setIcon(drawable);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(startMarker);

        findViewById(R.id.saveParkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Save Parking Location")
                        .setMessage("Do you want to save your parking location?")
                        .create();

                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        savePark();
                    }
                });

                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alertDialog.show();
            }
        });

    }

    /**
     * this will refresh the osmdroid configuration on resuming
     * Registering Listener
     */
    public void onResume() {
        super.onResume();
        checkPermission();
        map.onResume();

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

    public void onStop(){
        super.onStop();
        getEndService();
    }

    /*initialization of
           1.sensor Manager
           2.Light Sensor
           3.AccelerometerSensor
     */
    private void Initialization() {
        sensorManager             = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        LightSensor               = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        AccelerometerSensor       = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    // uses dexter library to check for permissions at runtime
    private void checkPermission() {
        Dexter.withContext(this).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if(multiplePermissionsReport.areAllPermissionsGranted()) {
                            sharedPrefs.setIsPermissionGranted(true);
                            myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }

                        if(multiplePermissionsReport.getDeniedPermissionResponses().size() > 0){
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
        if(!Settings.System.canWrite(this)) {
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


    /**this function saves the drivers parking location*/
    private void savePark() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.PARK_URL,
                response -> {

                    Log.d("JSON", String.valueOf(response));

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("status").equals("success")) {
                            sharedPrefs.setLatParked(lat);
                            sharedPrefs.setLonParked(lon);
                            Toast.makeText(this, "Location saved", Toast.LENGTH_SHORT).show();
                            Log.d("JSON", "success: " + jsonObject.getString("message"));
                        } else if(jsonObject.getString("status").equals("error")) {
                            Log.d("JSON", "error: " + jsonObject.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "try/catch error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    VolleyLog.d("Error", "Error: " + error.getMessage());
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
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

                    // TODO: we will need a JSONArray with all the parking locations for specified driverId
                    Log.d("JSON", String.valueOf(response));

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("status").equals("success")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            getArraylist(jsonArray);

                            Intent newIntent = new Intent(MainActivity.this, ParkingItemsActivity.class);
                            newIntent.putExtra(Constant.LOCATIONLIST, locationList);
                            startActivity(newIntent);
                            finish();
                        } else if(jsonObject.getString("status").equals("error")) {
                            Log.d("JSON", "error: " + jsonObject.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "try/catch error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    VolleyLog.d("Error", "Error: " + error.getMessage());
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
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

    /**this function starts a service*/
    private void getStartService(){
        Intent intent = new Intent(this, LocationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**this function end a service*/
    private void getEndService(){
        Intent intent = new Intent(this, LocationService.class);
        unbindService(serviceConnection);
        stopService(intent);
    }

    /**a notification is required for a foreground service*/
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

    /**receive the driver location from services*/
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            lat = intent.getDoubleExtra(Constant.LATITUDE, 0);
            lon = intent.getDoubleExtra(Constant.LONGITUDE, 0);
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
                //startPoint.bearingTo(geoPoint);
                startMarker.setRotation((float) startPoint.bearingTo(geoPoint));
                startMarker.setPosition(startPoint);
                Log.d("mtag", "I KEEP COMING HERE");
            }
            mapController.setCenter(startPoint);
            mapController.setZoom(19.5);
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


    // override the onOptionsItemSelected()
    // function to implement
    // the item click listener callback
    // to open and close the navigation
    // drawer when the icon is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // TODO: add menu items and functionality to each menu item
        switch(item.getItemId()) {
            case R.id.nav_last_parked:
                //Toast.makeText(this, "Clicked item 2", Toast.LENGTH_SHORT).show();

                if(sharedPrefs.getLonParked()!=null) {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse(Constant.GOOGLE_MAP_URL + Double.valueOf(sharedPrefs.getLatParked()) + "," + Double.valueOf(sharedPrefs.getLonParked())));
                    startActivity(intent);
                }
                else{
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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
            // TODO: Implement feature of accelerometer based of floating bar
        }
        //Implemented simple algorithm for light sensor
        if(sensorEvent.sensor.getType()==Sensor.TYPE_LIGHT){
            setBrightness((int)sensorEvent.values[0]);
        }

    }

    private void setBrightness(int brightness) {
        // TODO: Implement Algorithm for brightness control
        //  RZ comment: this doesn't really do anything rn

        if(brightness < Constant.Brightness_Zero){
            brightness = Constant.Brightness_Zero;
        }
        else if(brightness > Constant.Brightness_Max){
            brightness = Constant.Brightness_Max;
        }
        Log.d("Brightness Test","Brightness is: "+ brightness);
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS,brightness);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // TODO: override for interface, would have to implement if needed for accerlerometer sensor
    }
}
