package com.example.samplecompasstracker;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import static com.example.samplecompasstracker.LocationPermissionManager.PERMISSIONS_REQUEST;

public class MainActivity extends AppCompatActivity implements CompassListener, FusedLocationListener {

    public static final String TAG = MainActivity.class.getSimpleName();
//    private static final long UPDATE_INTERVAL = 5000;
//    private static final long FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL/2;

//    private FusedLocationProviderClient fusedLocationProviderClient;
//    private LocationRequest locationRequest;
//    private LocationCallback locationCallback;
    private CompassSensor compassSensor;
    private FusedLocationClient fusedLocationClient;

    private TextInputLayout longitudeTextLayout, latitudeTextLayout;
    private EditText longitudeEditText, latitudeEditText;
    private TextView userLongitude, userLatitude, destinationLongitude, destinationLatitude;
    private Button destinationButton;
    private CompassView compassView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        bindViews();
        init();

        fusedLocationClient = FusedLocationClient.getInstance(this);
        fusedLocationClient.setFusedListener(this);

        getFusedLocation();
//
//        createLocationRequest();
//        getFusedLocation();
    }

    private void init() {
        compassSensor = new CompassSensor(this);
        compassSensor.setCompassListener(this);

//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                onNewLocation(locationResult.getLastLocation());
//            }
//        };

        destinationButton.setOnClickListener(v -> {
            if (!LocationPermissionManager.checkLocationPermissions(this)) {
                LocationPermissionManager.checkRequestLocationPermissions(this);
            } else {
                setDestinationLocation(latitudeEditText.getText().toString(),
                        longitudeEditText.getText().toString());
            }
        });
    }

    private void bindViews() {
        longitudeTextLayout = findViewById(R.id.longitute_layout);
        latitudeTextLayout = findViewById(R.id.latitute_layout);

        longitudeEditText = findViewById(R.id.longitute_et);
        latitudeEditText = findViewById(R.id.latitute_et);

        userLongitude = findViewById(R.id.user_longitude);
        userLatitude = findViewById(R.id.user_latitude);

        destinationLongitude = findViewById(R.id.destination_longitude);
        destinationLatitude = findViewById(R.id.destination_latitude);

        destinationButton = findViewById(R.id.destination_button);

        compassView = findViewById(R.id.compass_view);
    }

    /**
     * Priority can be set on PRIORITY_BALANCED_POWER_ACCURACY for better battery optimization
     */
//    private void createLocationRequest() {
//        locationRequest = new LocationRequest();
//        locationRequest.setInterval(UPDATE_INTERVAL);
//        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//    }

    /**
     * Permission will be check, so we don't have to do it here again (checkLocationPermissions)
     */
    private void getFusedLocation() {
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (LocationPermissionManager.checkLocationPermissions(this)) {
            fusedLocationClient.getFusedLocation();
//            fusedLocationProviderClient.getLastLocation()
//                    .addOnSuccessListener(this, location -> {
//                        Log.i(TAG, "OnSuccessListener - get location");
//                        if (location != null) {
//                            setUserLocation(location);
//                        } else {
//                            Log.w(TAG, "Failed to get location");
//                            setUserLocation(location);
//                        }
//                    });
//            requestLocationUpdate();
        } else {
            Log.i(TAG, "Permissions not granted");
            LocationPermissionManager.checkRequestLocationPermissions(this);
        }
    }

//    private void requestLocationUpdate() {
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
//    }

//    private void setUserLocation(Location location) {
//        if (location != null) {
//
//        } else {
//            userLatitude.setText(getString(R.string.location_unknow));
//            userLongitude.setText(getString(R.string.location_unknow));
//        }
//    }

    private void setDestinationLocation(String lat, String lng) {
        if (lat != null && lng != null) {
            Coordinates destinationCoordinates = Utils.getCoordinatesFromPoints(lat, lng);
            destinationLatitude.setText("Lat: " + lat);
            destinationLongitude.setText("Lng: " + lng);
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
                    fusedLocationClient.getFusedLocation();
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (compassSensor != null)
            compassSensor.initSensor();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (compassSensor != null)
            compassSensor.uninitSensor();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null)
            fusedLocationClient.removeLocationUpdate();
    }

    @Override
    public void onRotationChanged(float azimuth) {
        compassView.setAzimuth(azimuth);
    }

    @Override
    public void setFusedLocation(Location location) {
        userLatitude.setText("Lat: " + Utils.FormatCoordinateText(location.getLatitude()));
        userLongitude.setText("Lng: " + Utils.FormatCoordinateText(location.getLongitude()));
    }

    @Override
    public void setUnknowLocation() {
        userLatitude.setText("Lat: ?????");
        userLongitude.setText("Lng: ?????");
    }

    @Override
    public void onNewLocation(Location lastLocation) {
        userLatitude.setText("Lat: " + Utils.FormatCoordinateText(lastLocation.getLatitude()));
        userLongitude.setText("Lng: " + Utils.FormatCoordinateText(lastLocation.getLongitude()));
    }
}
