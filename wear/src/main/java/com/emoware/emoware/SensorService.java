package com.emoware.emoware;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SensorService extends IntentService {
    private static final String TAG = SensorService.class.getName();

    private static final long CONNECTION_TIME_OUT_MS = 5000;

    public static final String HEART_RATE_CHANGED = "HeartRateChanged";

    HeartRateMonitor monitor = null;

    private String nodeId = null;

    public SensorService() {
        super("SensorService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        //Log.e(TAG, "onHandleIntent: " + workIntent.getDataString());
        String action = Util.getAction(workIntent.getData());
        String dataString = Util.getData(workIntent.getData());
        Log.i(TAG, "action = "+action+", data = "+dataString);

        switch (action) {
            case Constants.URI_SENSOR_CONTROL:
                switch (dataString) {
                    case Constants.START:
                        if (null == monitor) {
                            Log.i(TAG, "Starting sensors");
                            monitor = new HeartRateMonitor(this);
                            monitor.start();
                        }
                        break;
                    case Constants.STOP:
                        if (null != monitor) {
                            Log.i(TAG, "Stopping sensors");
                            monitor.stop();
                            monitor = null;
                        }
                        break;
                }
                break;
            case Constants.URI_HEART_RATE:
                Log.i(TAG, "heart rate changed");
                sendHeartRate(dataString);
                break;
            default:
                Log.e(TAG, "Unexpected action = "+action);
        }
    }

    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }

    private void retrieveDeviceNode() {
        final GoogleApiClient client = getGoogleApiClient(this);
        client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
        NodeApi.GetConnectedNodesResult result =
                Wearable.NodeApi.getConnectedNodes(client).await();
        List<Node> nodes = result.getNodes();
        if (nodes.size() > 0) {
            nodeId = nodes.get(0).getId();
            Log.i(TAG, "nodeId set = " + nodeId);
            //sendHeartRate("CONNECTED");
        }
        client.disconnect();
    }

    private void sendHeartRate(final String heartRate) {
        if (null == nodeId) {
            Log.w(TAG, "Warning: attempt to send message when nodeId is null");
            retrieveDeviceNode();
        }

        final GoogleApiClient client = getGoogleApiClient(this);
        if (nodeId != null) {
            Log.i(TAG, "sending heart rate message");
            client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
            Wearable.MessageApi.sendMessage(client, nodeId, HEART_RATE_CHANGED, heartRate.getBytes());
            client.disconnect();
        }
    }
}
