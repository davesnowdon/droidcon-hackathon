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

public class WearHome extends Activity  {

    private static final String TAG = WearHome.class.getName();

    private TextView mTextView;

    private CountDownLatch latch;

    private String nodeId = null;

    HeartRateMonitor monitor;

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        monitor = new HeartRateMonitor(this);
        monitor.start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //monitor.stop();
    }
}
