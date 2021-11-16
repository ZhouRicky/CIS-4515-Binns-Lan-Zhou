package edu.temple.projectblz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {


    SharedPrefs sharedPrefs;
    //String lat = "40.4589";
    //String lon = "-35.5698";
    String id = "12";
    MapView map;
    IMapController mapController;
    GeoPoint startPoint;
    Marker startMarker;
    Drawable drawable;
    LocationManager locationManager;
    Location myLocation;
    LocationService myService;

    double lat;
    double lon;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    //Initializing a sensor, sensormanager
    SensorManager sensorManager;
    Sensor LightSensor, AcceleroMeterSensor;
    Context context;

    @RequiresApi(api = Build.VERSION_CODES.O)


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** ensures that the map has a writable location for the map cache*/
        Configuration.getInstance().load(getApplication(), PreferenceManager.getDefaultSharedPreferences(getApplication()));
        sharedPrefs = new SharedPrefs(this);

        //request location & writting to system permission
        //initialization of the sensor and manager
        Initialization();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("driverMood"));
        //TODO Get all of those into Initialization(). easier to read

        createNotificationChannel();

        getStartService();

        locationManager = getSystemService(LocationManager.class);


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
        RequestPermission();

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


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("t", "I CAME HERE");
                savePark();
                Log.d("t", "I CAME HERE2");
            }
        });

    }

    /*initialization of
           1.sensor Manager
           2.Light Sensor
           3.AcceleroMeterSensor
     */
    private void Initialization() {
        sensorManager             = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        LightSensor               = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        AcceleroMeterSensor       = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }


    /*
        Request Permission at starting of the activity
            * Location Permission
            * Write Setting Permission
     */
    private void RequestPermission() {
        if  (!isGPSPermission()){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constant.RequestCode_FineLocation);
        }
        else {
            getGPS();
        }
        boolean value;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            value = Settings.System.canWrite(getApplicationContext());
            if(!value){
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                startActivityForResult(intent,Constant.RequestCode_Permission_WriteSetting);
            }
        }
    }

    /**
     * this will refresh the osmdroid configuration on resuming
     * Registering Listener
     */
    public void onResume() {
        super.onResume();
        map.onResume();
        //register light sensor and accelerometer sensor

        sensorManager.registerListener(this,LightSensor,sensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,AcceleroMeterSensor,sensorManager.SENSOR_DELAY_NORMAL);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constant.RequestCode_Permission_WriteSetting){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                boolean value = Settings.System.canWrite(getApplicationContext());
                if(!value){
                    Toast.makeText(this,"Permission is not granted",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**check if user gave permission*/
    private boolean isGPSPermission(){
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    //Check if User have WriteSetting PErmission
    private boolean isWriteSettingPermission(){
        return checkSelfPermission(Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
    }


    /**if permission is granted, get last known gps location*/
    @SuppressLint("MissingPermission")
    private void getGPS(){
        if (isGPSPermission())
            myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    /**check the users response for gps permission*/
    @Override
    public void onRequestPermissionsResult (int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constant.RequestCode_FineLocation && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            getGPS();
        if (requestCode == Constant.RequestCode_WriteSetting && grantResults[0] != PackageManager.PERMISSION_GRANTED){
            //TODO Implement Alert notify brightness caution
        }
    }

    private void savePark() {
        //I am replacing URL into CONSTANT :::ParkPhp
        //"https://cis-linux2.temple.edu/~tul58076/insertpark.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.ParkPhp,
                response -> {

//                    Log.d("JSON", String.valueOf(response));

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");

                        if (status.equals(Constant.SUCCESS_CODE)) {
                            // TODO: add local save location functionality if needed
                            Toast.makeText(this, "Location saved", Toast.LENGTH_SHORT).show();
                        }

                        Toast.makeText(this, status, Toast.LENGTH_SHORT).show();
                        Log.d("JSON", "status1: " + status);
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
                params.put(Constant.ParkingLatitude, String.valueOf(lat)); //TODO
                params.put(Constant.ParkingLongitude, String.valueOf(lon));//TODO
                params.put(Constant.ParkingDriverID, id);//TODO
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
            // myService.registerActivity(LoggedInActivity.this);
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
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                map.getOverlays().add(startMarker);
            } else {
                GeoPoint geoPoint = new GeoPoint(lat, lon);
                startPoint.setCoords(lat, lon);
                //startPoint.bearingTo(geoPoint);
                startMarker.setRotation((float) startPoint.bearingTo(geoPoint));
                startMarker.setPosition(startPoint);
            }
            mapController.setCenter(startPoint);
            mapController.setZoom(19.5);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            map.getOverlays().add(startMarker);
            /*
            mapController = map.getController();
            if(myLocation!=null) {
                startPoint = new GeoPoint(lat, lon);
                mapController.setCenter(startPoint);
                mapController.setZoom(19.5);
                startMarker = new Marker(map);
                startMarker.setPosition(startPoint);
            }
            */

           /* startMarker.setTitle("You are here");
            drawable = getResources().getDrawable(R.drawable.red_car_marker);
            startMarker.setIcon(drawable);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            map.getOverlays().add(startMarker);*/


        }
    };


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
            case R.id.nav_item_1:
                Toast.makeText(this, "Clicked Item 1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_item_2:
                Toast.makeText(this, "Clicked item 2", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_item_3:
                Toast.makeText(this, "Clicked item 3", Toast.LENGTH_SHORT).show();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
            //TODO Implement feature of accelerometer based of floating bar
        }
        //Implemented simple algorithm for light sensor
        if(sensorEvent.sensor.getType()==Sensor.TYPE_LIGHT){
            setBrightness((int)sensorEvent.values[0]);
        }

    }

    private void setBrightness(int brightness) {
        //TODO Implement Algorithm for brightness control

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
        //TODO override for interface, would have to implement if needed for accerlerometer sensor
    }
}
