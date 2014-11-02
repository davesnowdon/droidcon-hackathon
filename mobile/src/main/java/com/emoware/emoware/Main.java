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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View.OnTouchListener;
import java.util.ArrayList;
import java.util.List;


public class Main extends Activity {
    ListView list;
    private List<String> List_of_persons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List_of_persons = new ArrayList<String>();
        list = (ListView)findViewById(R.id.listview);
        CreateListView();
    }

    private void CreateListView()
    {
        List_of_persons.add("Maria");
        List_of_persons.add("Peter");
        List_of_persons.add("Sarah");
        List_of_persons.add("John");
        List_of_persons.add("Chloe");

        list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, List_of_persons));

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
