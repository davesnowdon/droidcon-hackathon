package com.emoware.emoware;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class WearHome extends Activity implements SensorEventListener {

    private static final String TAG = WearHome.class.getName();

    private static final int SENSOR_TYPE_HEARTRATE = 65562;
    private static final long CONNECTION_TIME_OUT_MS = 5000;
    private static final String HEART_RATE_CHANGED = "HeartRateChanged";

    private TextView mTextView;

    SensorManager mSensorManager;
    private Sensor mHeartRateSensor;

    private CountDownLatch latch;

    private String nodeId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear_home);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        latch = new CountDownLatch(1);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                latch.countDown();
            }
        });

        mSensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
        //Sensor mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mHeartRateSensor = mSensorManager.getDefaultSensor(SENSOR_TYPE_HEARTRATE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        retrieveDeviceNode();
        mSensorManager.registerListener(this, this.mHeartRateSensor, 3);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        try {
            latch.await();
            if(sensorEvent.values[0] > 0){
                Log.d(TAG, "sensor event: " + sensorEvent.accuracy + " = " + sensorEvent.values[0]);
                String heartRateStr = String.valueOf(sensorEvent.values[0]);
                mTextView.setText(heartRateStr);
                sendHeartRate(heartRateStr);
                //accuracy.setText("Accuracy: "+sensorEvent.accuracy);
                //sensorInformation.setText(sensorEvent.sensor.toString());
            }

        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "accuracy changed: " + accuracy);
    }

    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }

    private void retrieveDeviceNode() {
        final GoogleApiClient client = getGoogleApiClient(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(client).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    nodeId = nodes.get(0).getId();
                    Log.i(TAG, "nodeId set = "+nodeId);
                    //sendHeartRate("CONNECTED");
                }
                client.disconnect();
            }
        }).start();
    }

    private void sendHeartRate(final String heartRate) {
        if (null == nodeId) {
            Log.w(TAG, "Warning: attempt to send message when nodeId is null");
            return;
        }

        final GoogleApiClient client = getGoogleApiClient(this);
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "sending heart rate message");
                    client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(client, nodeId, HEART_RATE_CHANGED, heartRate.getBytes());
                    client.disconnect();
                }
            }).start();
        }
    }
}
