package com.emoware.emoware;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by jairodiaz on 02/11/2014.
 */
public class FragmentOne extends Fragment {
    String mPersonName;
    TextView mTextView;

    public FragmentOne(String name) {
        mPersonName = name;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_one, container, false);
        mTextView = (TextView) v.findViewById(R.id.t1);
        mTextView.setText(mPersonName);

        return v;
    }
}
