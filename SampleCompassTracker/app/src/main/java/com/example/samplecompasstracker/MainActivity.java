package com.example.samplecompasstracker;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static com.example.samplecompasstracker.LocationPermissionManager.PERMISSIONS_REQUEST;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final long UPDATE_INTERVAL = 500;
    private static final long FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL/2;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createLocationRequest();
        getFusedLocation();
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
    private void getFusedLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (LocationPermissionManager.checkLocationPermissions(this)) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        Log.i(TAG, "OnSuccessListener - get location");
                        if (location != null) {
                            // TODO: 18.04.2019  success - set user location
                        } else {
                            Log.w(TAG, "Failed to get location");
                            // TODO: 18.04.2019 failed - do something?
                        }
                    });
        } else {
            Log.i(TAG, "Permissions not granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult");
        switch (requestCode) {
            case PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission granted");
                    getFusedLocation();
                    // TODO: 18.04.2019 do something with target
                }
                break;
        }
    }
}
