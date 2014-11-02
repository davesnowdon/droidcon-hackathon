package com.emoware.emoware;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class ListenerService extends WearableListenerService {
    private static final String TAG = ListenerService.class.getName();

    String nodeId;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        nodeId = messageEvent.getSourceNodeId();
        final String heartRate = new String(messageEvent.getData());
        final String msg = messageEvent.getPath() + " " + heartRate;
        Log.i(TAG, "received message = "+msg);
        showToast(msg);
    }

    private void showToast(String message) {
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Main.mHeartRateView.setText(message);
    }

}
