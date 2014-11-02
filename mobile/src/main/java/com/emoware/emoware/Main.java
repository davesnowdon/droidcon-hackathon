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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class Main extends Activity {
    public static TextView mHeartRateView;

    private static final String TAG = Main.class.getName();
    private static final int HEIGHT = 480;
    private static final int WIDTH = 640;

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
    }

    public void onConnectNao(View view) {
        Log.i(TAG, "Connect clicked");
        EditText ipView = (EditText) findViewById(R.id.robot_ip_edit);
        final String addr = ipView.getText().toString();
        Log.i(TAG, "Robot IP text field: " + addr);

        if (isNotBlank(addr)) {
            doConnect(addr);
        } else {
            Log.i(TAG, "Robot address was blank");
            Toast.makeText(context, "Please enter a robot ip or a robot name", Toast.LENGTH_SHORT).show();
        }
    }

    private void doConnect(final String addr) {
        if (isNotBlank(addr)) {
            Log.i(TAG, "Robot address: " + addr);

            Thread connectThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    Looper.prepare();
                    naoSession = new Session();

                    try {
                        naoSession.connect("tcp://" + addr + ":9559").sync(500, TimeUnit.MILLISECONDS);
                        memoryProxy = new ALMemory(naoSession);
                        motionProxy = new ALMotion(naoSession);
                        ttsProxy = new ALTextToSpeech(naoSession);
                        video = new ALVideoDevice(naoSession);
                        ttsProxy.setAsynchronous(true);
                        ttsProxy.say("Oh My God ");
                        memoryProxy = new ALMemory(naoSession);
                        Log.i(TAG, "Connected to "+addr);
                        Object myAlmemory=naoSession.service("ALMemory");
                        myAlmemory.call("raiseMicroEvent","AndroidIP", new Date().toString());

                        int topCamera = 0;
                        int resolution = 2; // 640 x 480
                        int colorspace = 11; // RGB
                        int frameRate = 10; // FPS

                        myAlmemory.call("raiseMicroEvent","AndroidIP", "calling video.subscribeCamera");
                        moduleName = video.subscribeCamera("demoAndroid", topCamera, resolution, colorspace, frameRate);
                        if (moduleName != null) {
                        myAlmemory.call("raiseMicroEvent", "AndroidIP", "finished video.subscribeCamera:" + moduleName);
                        final Bitmap MyImage = getVideo();
                        video.unsubscribe(moduleName);

                        try{

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    image = (ImageView) findViewById(R.id.imageView);
                                    image.setImageBitmap(MyImage);
                                }
                            });


                            myAlmemory.call("raiseMicroEvent","AndroidIP", "imageView update complete");
                        } catch (Exception e) {
                            myAlmemory.call("raiseMicroEvent", "AndroidIP", "imageView update failure" + e.toString());
                            e.printStackTrace();
                        }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "Error", e);
                    }
                }
            });
            connectThread.start();
        }
    }

    public Bitmap getVideo() throws Exception {
        List<java.lang.Object> image = video.getImageRemote(moduleName);
        ByteBuffer buffer = (ByteBuffer)image.get(6);
        byte[] rawData = buffer.array();

        int[] intArray = new int[HEIGHT * WIDTH];
        for (int i = 0; i < HEIGHT * WIDTH; i++) {
//            ((255 & 0xFF) << 24) | // alpha
            intArray[i] =
                    ((rawData[(i * 3)] & 0xFF) << 16) | // red
                            ((rawData[i * 3 + 1] & 0xFF) << 8) | // green
                            ((rawData[i * 3 + 2] & 0xFF)); // blue
        }

        Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.RGB_565);
        bitmap.setPixels(intArray, 0, WIDTH, 0, 0, WIDTH, HEIGHT);
        return bitmap;
//        return null;
    }



    boolean isNotBlank(String str) {
        return (null != str) && !str.trim().equals("");
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
