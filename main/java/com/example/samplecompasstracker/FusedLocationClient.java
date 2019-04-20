package com.example.samplecompasstracker;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class FusedLocationClient {

    private static final String TAG = FusedLocationClient.class.getSimpleName();
    private static final long UPDATE_INTERVAL = 5000;
    private static final long FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL/2;

    private static FusedLocationClient instance = null;

    public FusedLocationProviderClient fusedLocationProviderClient;
    public LocationRequest locationRequest;
    public LocationCallback locationCallback;
    private FusedLocationListener fusedListener;
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
                if (locationResult.getLastLocation() != null) {
                    fusedListener.onNewLocation(locationResult.getLastLocation());
                } else {
                    fusedListener.setUnknowLocation();
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

    public void setFusedListener(FusedLocationListener listener) {
        fusedListener = listener;
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void getFusedLocation() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(activity, location -> {
            if (location != null) {
                fusedListener.setFusedLocation(location);
            } else {
                fusedListener.setUnknowLocation();
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
