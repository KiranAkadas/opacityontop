package com.bacloud.opacityontop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;

public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("@@@@@@@@@@@@@@");
        SensorManager sManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sManager.registerListener(new ShakeEventListener(), sensor, SensorManager.SENSOR_DELAY_NORMAL); // or other delay
    }
}
