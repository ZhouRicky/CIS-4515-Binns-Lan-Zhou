package edu.temple.projectblz;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class LocationService extends Service {
    //Initialization of the location service based off TA's version

    //Initialization of the Location Manager, Listener and Notification
    private LocationManager locationManager;
    private LocationListener listener;
    private Notification notification;
    private final IBinder myBinder = new MyLocalBinder();
    Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (location != null){

                    try{
                        Intent intent = new Intent("driverMood");
                        intent.putExtra(Constant.LATITUDE, location.getLatitude());
                        intent.putExtra(Constant.LONGITUDE, location.getLongitude());
                        Log.d("tag3", "Longitude " + location.getLongitude());
                        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
                        localBroadcastManager.sendBroadcast(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("Print", String.valueOf(e));
                    }

                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }

        };

        buildForegroundNotification();

    }

    /**this class is to return the service*/
    public class MyLocalBinder extends Binder {
        LocationService getService(){
            return LocationService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            startForeground(Constant.FOREGROUND_SERVICE_ID, notification);
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
        return myBinder;

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
        notification = (new NotificationCompat.Builder(this, "driver"))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setChannelId("driver")
                .setContentTitle("Driving started")
                .setContentText("You have just started location services")
                .build();
        startForeground(Constant.FOREGROUND_SERVICE_ID, notification);
    }


}