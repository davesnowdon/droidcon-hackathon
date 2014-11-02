package com.emoware.emoware;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.util.Log;

public class HeartRateMonitor implements SensorEventListener {

    private static final String TAG = HeartRateMonitor.class.getName();

    private static final int SENSOR_TYPE_HEARTRATE = 65562;

    private SensorManager mSensorManager;
    private Sensor mHeartRateSensor;
    private Context context;

    private String lastHeartRate = "";

    public HeartRateMonitor(Context context) {
        this.context = context;
    }

    public void start() {
        Log.i(TAG, "HeartRateMonitor starting");
        mSensorManager = ((SensorManager)context.getSystemService(Context.SENSOR_SERVICE));
        //Sensor mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mHeartRateSensor = mSensorManager.getDefaultSensor(SENSOR_TYPE_HEARTRATE);
        mSensorManager.registerListener(this, this.mHeartRateSensor, 3);
    }

    public void stop() {
        Log.i(TAG, "HeartRateMonitor stopping");
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.values[0] > 0){
                Log.d(TAG, "sensor event: " + sensorEvent.accuracy + " = " + sensorEvent.values[0]);
                String heartRateStr = String.valueOf(sensorEvent.values[0]);
                // only send changes
                if (!lastHeartRate.equals(heartRateStr)) {
                    sendHeartRate(heartRateStr);
                    //accuracy.setText("Accuracy: "+sensorEvent.accuracy);
                    //sensorInformation.setText(sensorEvent.sensor.toString());
                    lastHeartRate = heartRateStr;
                }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "accuracy changed: " + accuracy);
    }

    protected void sendHeartRate(String heartRate) {
        Log.i(TAG, "sending heart rate = " + heartRate);
        Intent mServiceIntent = new Intent(context, SensorService.class);
        mServiceIntent.setData(Util.makeControlUri(Constants.URI_HEART_RATE, heartRate));
        context.startService(mServiceIntent);
    }
}
