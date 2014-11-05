package com.emoware.emoware;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aldebaran.qimessaging.Application;
import com.aldebaran.qimessaging.Object;
import com.aldebaran.qimessaging.Session;
import com.aldebaran.qimessaging.helpers.al.ALMemory;
import com.aldebaran.qimessaging.helpers.al.ALMotion;
import com.aldebaran.qimessaging.helpers.al.ALTextToSpeech;
import com.aldebaran.qimessaging.helpers.al.ALVideoDevice;
import com.google.android.gms.common.api.GoogleApiClient;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class Main extends Activity {
    public static TextView mHeartRateView;

    private static final String TAG = Main.class.getName();

    private String moduleName;
    private ALVideoDevice video;
    private Application application;
    private ImageView image;
    private Context context;

    // NAO
    private Session naoSession;
    private ALMotion motionProxy;
    private ALMemory memoryProxy;
    private ALTextToSpeech ttsProxy;

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";

    ListView list;
    private List<String> List_of_persons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        List_of_persons = new ArrayList<String>();
        list = (ListView)findViewById(R.id.listview);
        CreateListView();

        // TODO!
        //dataClient = getGoogleApiClient(this);
    }

    /*
    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {  // more about this later
            dataClient.connect();
        }
    }

    @Override
    protected void onStop() {
        dataClient.disconnect();
        super.onStop();
    }
    */

    public void onConnectNao(View view) {
        Log.i(TAG, "Connect clicked");
        EditText ipView = (EditText) findViewById(R.id.robot_ip_edit);
        final String addr = ipView.getText().toString();
        Log.i(TAG, "Robot IP text field: " + addr);

        if (Util.isNotBlank(addr)) {
           connectAndGrabImage(addr);
        } else {
            Log.i(TAG, "Robot address was blank");
            Toast.makeText(context, "Please enter a robot ip or a robot name", Toast.LENGTH_SHORT).show();
        }
    }

    private void connectAndGrabImage(final String addr) {
        if (Util.isNotBlank(addr)) {
            Log.i(TAG, "Robot address: " + addr);

            Thread connectThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    RobotManager manager = new RobotManager();
                    try {
                        manager.connect(addr);
                        final Bitmap bitmap = manager.getCameraImage();

                        // send the bitmap data to the wearable
                        // TODO!
                        //sendBitmapToWearable(bitmap);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                image = (ImageView) findViewById(R.id.imageView);
                                image.setImageBitmap(bitmap);
                            }
                        });
                    } catch (Exception e) {

                    } finally {
                        manager.disconnect();
                    }
                }
            });
            connectThread.start();
        }
    }


    private void CreateListView()
    {
        List_of_persons.add("Maria");
        List_of_persons.add("Peter");
        List_of_persons.add("Sarah");
        List_of_persons.add("John");
        List_of_persons.add("Chloe");

        list.setAdapter(new ArrayAdapter<String>(this, R.layout.rowlayout, R.id.label, List_of_persons));

        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3)
            {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                FragmentOne f1 = new FragmentOne(List_of_persons.get(arg2));
                ft.add(R.id.fr1_id, f1);
                ft.addToBackStack("f1");
                ft.commit();            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
