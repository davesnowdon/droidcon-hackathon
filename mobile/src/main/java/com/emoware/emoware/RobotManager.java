package com.emoware.emoware;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.aldebaran.qimessaging.*;
import com.aldebaran.qimessaging.helpers.al.ALMemory;
import com.aldebaran.qimessaging.helpers.al.ALMotion;
import com.aldebaran.qimessaging.helpers.al.ALTextToSpeech;
import com.aldebaran.qimessaging.helpers.al.ALVideoDevice;

import java.lang.Object;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Handle management of the connection to and actions on the robot.
 * Many of these methods take time and so should not be called on the UI thread
 */
public class RobotManager {
    private static final String TAG = Main.class.getName();

    private static final int IMAGE_HEIGHT = 480;
    private static final int IMAGE_WIDTH = 640;

    private Session naoSession;
    private ALMotion motionProxy;
    private ALMemory memoryProxy;
    private ALTextToSpeech ttsProxy;
    private ALVideoDevice video;

    public RobotManager() {
    }

    public void connect(String addr) throws Exception {
        if (Util.isNotBlank(addr)) {
            Log.i(TAG, "Robot address: " + addr);

            naoSession = new Session();

            try {
                naoSession.connect("tcp://" + addr + ":9559").sync(500, TimeUnit.MILLISECONDS);
                memoryProxy = new ALMemory(naoSession);
                motionProxy = new ALMotion(naoSession);
                ttsProxy = new ALTextToSpeech(naoSession);
                video = new ALVideoDevice(naoSession);
                ttsProxy.setAsynchronous(true);
                memoryProxy = new ALMemory(naoSession);
                Log.i(TAG, "Connected to " + addr);

            } catch (Exception e) {
                Log.e(TAG, "Error", e);
                e.printStackTrace();
                throw e;
            }
        }
    }

    public void disconnect() {
        naoSession.close();
    }

    public void raiseMicroEvent(String key, String value) throws Exception {
        com.aldebaran.qimessaging.Object myAlmemory = naoSession.service("ALMemory");
        myAlmemory.call("raiseMicroEvent", key, value);
    }

    public Bitmap getCameraImage() throws Exception {
        int topCamera = 0;
        int resolution = 2; // 640 x 480
        int colorspace = 11; // RGB
        int frameRate = 10; // FPS

        String moduleName = video.subscribeCamera("demoAndroid", topCamera, resolution, colorspace, frameRate);
        if (moduleName != null) {
            try {
                Log.i(TAG, "Video module name = " + moduleName);

                List<Object> image = video.getImageRemote(moduleName);
                ByteBuffer buffer = (ByteBuffer) image.get(6);
                byte[] rawData = buffer.array();

                int[] intArray = new int[IMAGE_HEIGHT * IMAGE_WIDTH];
                for (int i = 0; i < IMAGE_HEIGHT * IMAGE_WIDTH; i++) {
                    intArray[i] =
                            ((rawData[(i * 3)] & 0xFF) << 16) | // red
                                    ((rawData[i * 3 + 1] & 0xFF) << 8) | // green
                                    ((rawData[i * 3 + 2] & 0xFF)); // blue
                }

                Bitmap bitmap = Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT, Bitmap.Config.RGB_565);
                bitmap.setPixels(intArray, 0, IMAGE_WIDTH, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
                Log.d(TAG, "Got video image");
                return bitmap;

            } catch (Exception e) {
                Log.e(TAG, "imageView update failure" + e.toString());
                e.printStackTrace();
                throw e;
            } finally {
                video.unsubscribe(moduleName);
            }
        }

        throw new Exception("Failed to get module name");
    }
}
