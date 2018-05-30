package com.example.devbox.bluebotcontroller.view;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.example.devbox.bluebotcontroller.R;

public class MainActivity extends FragmentActivity {

    private static final String TAG_MAIN_FRAGMENT = "MainFragment";
    private String LOG_TAG = "MainActivity:";

    private MainFragment mMainFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();

        mMainFragment = (MainFragment) fm.findFragmentByTag(MainFragment.TAG);

        //create the fragment on the first run
        if (mMainFragment == null) {
            Log.v(LOG_TAG, "creating new MainFragment");
            mMainFragment = new MainFragment();
        }
        fm.beginTransaction().replace(R.id.gui_container, mMainFragment, MainFragment.TAG).commit();
    }

    public MainFragment getmMainFragment(){
        return mMainFragment;
    }

}

