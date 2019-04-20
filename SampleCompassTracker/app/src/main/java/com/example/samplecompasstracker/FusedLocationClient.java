package com.example.samplecompasstracker;

import android.app.Activity;
import android.os.Looper;

import com.example.samplecompasstracker.listener.FusedLocationListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class FusedLocationClient {

    private static final String TAG = FusedLocationClient.class.getSimpleName();
    private static final long UPDATE_INTERVAL = 5000;
    private static final long FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL/2;

    private static FusedLocationClient instance = null;

    public FusedLocationProviderClient fusedLocationProviderClient;
    public LocationRequest locationRequest;
    public LocationCallback locationCallback;
    private List<FusedLocationListener> fusedLocationListenerList = new ArrayList<>();
    private Activity activity;

    private FusedLocationClient(Activity activity) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        this.activity = activity;
        createLocationRequest();
        createLocationCallback();
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (FusedLocationListener fusedLocationListener : fusedLocationListenerList) {
                    if (locationResult.getLastLocation() != null) {
                        fusedLocationListener.onNewLocation(locationResult.getLastLocation());
                    } else {
                        fusedLocationListener.setUnknownLocation();
                    }
                }
            }
        };
    }

    public static FusedLocationClient getInstance(Activity activity) {
        if (instance == null) {
            instance = new FusedLocationClient(activity);
        }
        return instance;
    }

    public void addFusedListener(FusedLocationListener listener) {
        fusedLocationListenerList.add(listener);
    }

    public void removeFusedListener(FusedLocationListener listener) {
        fusedLocationListenerList.remove(listener);
    }

    /**
     * Priority can be set on PRIORITY_BALANCED_POWER_ACCURACY for better battery optimization
     */
    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Permission will be check, so we don't have to do it here again (checkLocationPermissions)
     */
    public void getFusedLocation() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(activity, location -> {
            for (FusedLocationListener fusedLocationListener : fusedLocationListenerList) {
                if (location != null) {
                    fusedLocationListener.setFusedLocation(location);
                } else {
                    fusedLocationListener.setUnknownLocation();
                }
            }
        });
        requestLocationUpdate();
    }

    public void removeLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

    }

    private void requestLocationUpdate() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }
}
