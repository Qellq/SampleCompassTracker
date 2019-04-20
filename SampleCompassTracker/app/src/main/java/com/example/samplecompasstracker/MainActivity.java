package com.example.samplecompasstracker;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.samplecompasstracker.listener.CompassListener;
import com.example.samplecompasstracker.listener.FusedLocationListener;
import com.example.samplecompasstracker.model.Coordinates;
import com.example.samplecompasstracker.permission.LocationPermissionManager;
import com.example.samplecompasstracker.util.DestinationPoint;
import com.example.samplecompasstracker.util.Utils;
import com.example.samplecompasstracker.util.ValidUtils;
import com.example.samplecompasstracker.view.CompassView;

import static com.example.samplecompasstracker.permission.LocationPermissionManager.PERMISSIONS_REQUEST;

public class MainActivity extends AppCompatActivity implements CompassListener, FusedLocationListener {
    public static final String TAG = MainActivity.class.getSimpleName();

    private CompassSensor compassSensor;
    private FusedLocationClient fusedLocationClient;
    private Coordinates userLocation = null;
    private Coordinates destinationLocation = null;

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
        fusedLocationClient.addFusedListener(this);
        getFusedLocation();
    }

    private void init() {
        compassSensor = new CompassSensor(this);
        compassSensor.setCompassListener(this);
        destinationButton.setOnClickListener(v -> {
            removeFocus();
            if (!LocationPermissionManager.checkLocationPermissions(this)) {
                LocationPermissionManager.checkRequestLocationPermissions(this);
            } else {
                setDestinationLocation(latitudeEditText.getText().toString(),
                        longitudeEditText.getText().toString());
                if (ValidUtils.startValid(this, latitudeTextLayout, longitudeTextLayout, userLocation, destinationLocation)) {
                    compassView.setDestinationLoaction(DestinationPoint.getBearing(userLocation, destinationLocation));
                }
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

    private void getFusedLocation() {
        if (LocationPermissionManager.checkLocationPermissions(this)) {
            fusedLocationClient.getFusedLocation();
        } else {
            Log.i(TAG, "Permissions not granted");
            LocationPermissionManager.checkRequestLocationPermissions(this);
        }
    }

    private void setDestinationLocation(String lat, String lng) {
        if (!lat.isEmpty() && !lng.isEmpty()) {
            destinationLocation = Utils.getCoordinatesFromPoints(lat, lng);
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

    private void removeFocus() {
        latitudeTextLayout.clearFocus();
        longitudeTextLayout.clearFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeFusedListener(this);
        if (fusedLocationClient != null)
            fusedLocationClient.removeLocationUpdate();
    }

    private void updateUserLocationOnUI(Coordinates userLocation) {
        if (userLocation != null) {
            userLatitude.setText("Lat: " + Utils.FormatCoordinateText(userLocation.getLatitude()));
            userLongitude.setText("Lng: " + Utils.FormatCoordinateText(userLocation.getLongitude()));
        } else {
            userLatitude.setText("Lat: " + getString(R.string.unknown_location));
            userLongitude.setText("Lng: " + getString(R.string.unknown_location));
        }
    }

    @Override
    public void onRotationChanged(float azimuth) {
        compassView.setAzimuth(azimuth);
    }

    @Override
    public void setFusedLocation(Location location) {
        userLocation = new Coordinates(location.getLatitude(), location.getLongitude());
        updateUserLocationOnUI(userLocation);
    }

    @Override
    public void setUnknownLocation() {
        updateUserLocationOnUI(null);
    }

    @Override
    public void onNewLocation(Location lastLocation) {
        userLocation.setCoordinates(lastLocation.getLatitude(), lastLocation.getLongitude());
        updateUserLocationOnUI(userLocation);
    }
}
