package com.emoware.emoware;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnTouchListener;

//Code for creating a dynamic list of users:
// http://www.coderzheaven.com/2011/03/12/creating-scrolling-listview-in-android/

//Fragments are on:
// http://developer.android.com/guide/components/fragments.html

public class Main extends Activity {
//    Button b1;
    TextView t1;
    LinearLayout l1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        b1 = (Button) findViewById(R.id.b1);
//
//        b1.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentManager fm = getFragmentManager();
//                FragmentTransaction ft = fm.beginTransaction();
//                FragmentOne f1 = new FragmentOne();
//                ft.add(R.id.fr1_id, f1);
//                ft.addToBackStack("f1");
//                ft.commit();
//            }
//        });

        l1 = (LinearLayout) findViewById(R.id.l1);

        l1.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        t1 = (TextView) findViewById(R.id.t1);

        t1.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_CANCEL:
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        FragmentOne f1 = new FragmentOne();
                        ft.add(R.id.fr1_id, f1);
                        ft.addToBackStack("f1");
                        ft.commit();
                        return true;
                    default:
                        return false;

                }
            }
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
