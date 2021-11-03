package edu.temple.projectblz;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

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

public class MainActivity extends AppCompatActivity {

    SharedPrefs sharedPrefs;
    String lat = "40.4589";
    String lon = "-35.5698";
    String id = "12";
    MapView map;
    IMapController mapController;
    GeoPoint startPoint;
    Marker startMarker;
    Drawable drawable;
    LocationManager locationManager;
    Location myLocation;
    LocationService myService;
   // double lat;
    //double lon;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        /** ensures that the map has a writable location for the map cache*/
        Configuration.getInstance().load(getApplication(), PreferenceManager.getDefaultSharedPreferences(getApplication()));
        sharedPrefs = new SharedPrefs(this);

        locationManager = getSystemService(LocationManager.class);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);


        mapController = map.getController();
        startPoint = new GeoPoint(48.8583, 2.2944);
        mapController.setCenter(startPoint);
        mapController.setZoom(19.5);
        startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setTitle("You are here");
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

    /**
     * this will refresh the osmdroid configuration on resuming
     */
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    /**
     * this will enable osmdroid to be refreshed
     */
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    private void savePark() {

        final String URL = "http://10.109.0.138/insertpark.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                    Log.d("TAG1", "Response: " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String result = jsonObject.getString("status");
                        if (result.equals("success")) {
                            //sharedPrefs.setLoggedInUser(username);
                            Toast.makeText(this, "Location saved", Toast.LENGTH_LONG).show();
                        }
                        Toast toast = Toast.makeText(this, result, Toast.LENGTH_LONG);
                        toast.show();
                        Log.d("TAG", "resultKey1 " + result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error, Please try again " + e.toString(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error, Please try again" + error.toString(), Toast.LENGTH_LONG).show();
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("park_lat", lat); //TODO
                params.put("park_lon", lon);//TODO
                params.put("driver_id", id);//TODO
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

    /**receive the driver location from services*/
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

           // lat = intent.getDoubleExtra(Constant.LATITUDE, 0);
           // lon = intent.getDoubleExtra(Constant.LONGITUDE, 0);

            if(startMarker == null){
              //  startPoint.setCoords(lat, lon);
                startMarker.setPosition(startPoint);
                startMarker.setTitle("You are here");
                drawable = getResources().getDrawable(R.drawable.red_car_marker);
                startMarker.setIcon(drawable);
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                map.getOverlays().add(startMarker);
            }
            else{
               // GeoPoint geoPoint = new GeoPoint(lat, lon);
                //startPoint.setCoords(lat, lon);
                //startPoint.bearingTo(geoPoint);
                startMarker.setPosition(startPoint);
            }
        }
    };
}
