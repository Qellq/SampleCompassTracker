package com.example.samplecompasstracker;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.samplecompasstracker.listener.CompassListener;

public class CompassSensor implements SensorEventListener {

    private boolean hasVector = true;
    private SensorManager sensorManager;
    private Sensor rotationVectorSensor;
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];
    private float azimuth = 0.0f;

    private CompassListener compassListener;
    private long mLastTime = 0;
    private int mIntervalTime = 0;

    public CompassSensor(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    public void setCompassListener(CompassListener compassListener) {
        this.compassListener = compassListener;
    }

    public void initSensor() {
        if (rotationVectorSensor == null) {
            hasVector = false;
        } else {
            sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    public void uninitSensor() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (compassListener == null || !hasVector)
            return;
        long time = System.currentTimeMillis();
        if (time - mLastTime > mIntervalTime) {
            synchronized (this) {
                switch (event.sensor.getType()) {
                    case Sensor.TYPE_ROTATION_VECTOR:
                        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
                        SensorManager.getOrientation(rotationMatrix, orientation);
                        azimuth = (float) (Math.toDegrees(orientation[0]) + 360) % 360;
                        compassListener.onRotationChanged(azimuth);
                        break;
                }
            }
            mLastTime = time;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
