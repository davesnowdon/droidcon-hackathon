package com.emoware.emoware.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.emoware.emoware.Constants;
import com.emoware.emoware.SensorService;
import com.emoware.emoware.Util;

public class LaunchActivity extends Activity  {

    private static final String TAG = LaunchActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "LaunchActivity");
        startSensors(this);
        finish();
    }

    protected void startSensors(Context context) {
        Log.i(TAG, "Requesting sensor start");
        Intent mServiceIntent = new Intent(context, SensorService.class);
        mServiceIntent.setData(Util.makeControlUri(Constants.URI_SENSOR_CONTROL, Constants.START));
        context.startService(mServiceIntent);
    }
}
