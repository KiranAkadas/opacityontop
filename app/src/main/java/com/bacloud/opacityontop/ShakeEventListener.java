package com.bacloud.opacityontop;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class ShakeEventListener implements SensorEventListener {
    @Override
    public void onSensorChanged(SensorEvent event) {
        handleShake(event); // see below
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void handleShake(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) ;
        {
            System.out.println("@@@@@@@@@@@@@@");
        }
    }
}