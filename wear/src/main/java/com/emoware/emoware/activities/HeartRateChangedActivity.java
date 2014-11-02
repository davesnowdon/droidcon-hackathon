package com.emoware.emoware.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

import com.emoware.emoware.HeartRateMonitor;
import com.emoware.emoware.R;

import java.util.concurrent.CountDownLatch;

public class HeartRateChangedActivity extends Activity  {

    private static final String TAG = HeartRateChangedActivity.class.getName();

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
