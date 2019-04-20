package com.example.samplecompasstracker;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class CompassSensor implements SensorEventListener {

    private final float ALPHA = 0.9f;
    private boolean hasVector = true;
    private boolean hasMagnetic =  true;
    private SensorManager sensorManager;
    private Sensor rotationVectorSensor, magneticSensor, accelerometerSensor;
    private float[] geomagnetic = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];
    private float azimuth = 0.0f;
    private float preAzimuth;

    private CompassListener compassListener;
    private long mLastTime = 0;
    private int mIntervalTime = 0;

    public CompassSensor(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
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
        if (magneticSensor == null) {
            hasMagnetic = false;
        } else {
            sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    public void uninitSensor() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (compassListener == null || !(hasVector && hasMagnetic))
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
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        geomagnetic[0] = ALPHA * geomagnetic[0] + (1 - ALPHA) * event.values[0];
                        geomagnetic[1] = ALPHA * geomagnetic[1] + (1 - ALPHA) * event.values[1];
                        geomagnetic[2] = ALPHA * geomagnetic[2] + (1 - ALPHA) * event.values[2];


//                        boolean success = SensorManager.getRotationMatrix(rotationMatrix, null, null, geomagnetic);
//                        if (success) {
//                            SensorManager.getOrientation(rotationMatrix, orientation);
//                            azimuth = (float) ((Math.toDegrees(orientation[0]) + 360) % 360);
//                            compassListener.onRotationChanged(azimuth);
//                        }
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
