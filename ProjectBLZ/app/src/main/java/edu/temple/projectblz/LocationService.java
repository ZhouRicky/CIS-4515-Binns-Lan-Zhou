package edu.temple.projectblz;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class LocationService extends Service {
    //Initialization of the location service based off TA's version

    //Initialization of the Location Manager, Listener and Notification
    private static final int FOREGROUND_SERVICE_ID = 1;
    private LocationManager locationManager;
    private LocationListener listener;
    private Notification notification;

    @Override
    public void onCreate() {
        super.onCreate();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = location -> {

            //TODO Updating our features for our own listener

        };

        buildForegroundNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            startForeground(FOREGROUND_SERVICE_ID, notification);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    10, 1, listener);
            //TODO Initializing the distance based of 1 meter and 10 ms but would need to change if needed
            Log.d(Constant.LOG_LOCATION, "The foreground location service has successfully started: Location Update Initialization is successful");
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
        //TODO implements onBind
    }

    @Override
    public void onDestroy() {
        Log.i(Constant.LOG_LOCATION, "Location Service is onDestroy and the Location Service is eliminated");
        locationManager.removeUpdates(listener);
        //Use Manager to remove the listener;
    }

    /**
     * Construct a notification to the user to let them know what we're doing with this service
     */
    private void buildForegroundNotification() {
        //TODO Setting our own buildforegroundNotification
    }


}