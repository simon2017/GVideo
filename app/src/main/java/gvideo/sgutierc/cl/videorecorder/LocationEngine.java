package gvideo.sgutierc.cl.videorecorder;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.concurrent.CopyOnWriteArrayList;

import gvideo.sgutierc.cl.test.Camera2VideoFragment;
import gvideo.sgutierc.cl.test.PermissionsUtil;

/**
 * Created by sgutierc on 23-03-2018.
 */

public class LocationEngine implements LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private final String TAG = LocationEngine.class.getName();
    private final int MY_REQUEST_CODE = 101;
    private CopyOnWriteArrayList<LocationHandler> handlers = null;
    private String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
    private long UPDATE_TIME_MILLIS = 5000;
    private float UPDATE_DISTANCE_METER = 10;
    private Activity activity;

    private static final String[] LOCATION_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
    };


    /**
     * @param activity
     */
    public LocationEngine(Activity activity) {
        this.activity = activity;
        this.handlers = new CopyOnWriteArrayList<>();
    }

    private boolean isNetworkEnabled(LocationManager locationManager) {
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean isGPSEnabled(LocationManager locationManager) {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void addHandler(LocationHandler handler) {
        this.handlers.add(handler);
    }

    public void removeHandler(LocationHandler handler) {
        this.handlers.remove(handler);
    }

    public void startListening() {

        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (!PermissionsUtil.hasPermissionsGranted(LOCATION_PERMISSIONS, activity)) {
            PermissionsUtil.requestVideoPermissions(LOCATION_PERMISSIONS, MY_REQUEST_CODE, activity);
            return;
        }
        requestUpdates();
    }

    @SuppressWarnings("MissingPermission")
    private void requestUpdates() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LOCATION_PROVIDER, UPDATE_TIME_MILLIS, UPDATE_DISTANCE_METER, this);
        //set my last known location into map
        Location lastKnown = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
        for (LocationHandler handler : handlers) {
            handler.handleLocation(lastKnown, LocationHandler.Event.START);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        for (LocationHandler handler : handlers) {
            handler.handleLocation(location, LocationHandler.Event.UPDATE);
        }
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");
        boolean granted = true;
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length == LOCATION_PERMISSIONS.length) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        Camera2VideoFragment.ErrorDialog.newInstance(activity.getString(R.string.permission_request))
                                .show(activity.getFragmentManager(), "dialog");
                        granted = false;
                        break;
                    }
                }
            } else {
                granted = false;
                Camera2VideoFragment.ErrorDialog.newInstance(activity.getString(R.string.permission_request))
                        .show(activity.getFragmentManager(), "dialog");
            }
        } else {
            granted = false;
            PermissionsUtil.requestVideoPermissions(LOCATION_PERMISSIONS, MY_REQUEST_CODE, activity);
        }

        if (granted) {
            requestUpdates();
        }
    }

    /**
     * Function to show settings alert dialog.
     * On pressing the Settings button it will launch Settings Options.
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing the Settings button.
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(intent);
            }
        });

        // On pressing the cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}
