package com.example.devbox.bluebotcontroller;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends FragmentActivity {

    private static final String TAG_MAIN_FRAGMENT = "MainFragment";
    private String LOG_TAG = "MainActivity:";

    private MainFragment mMainFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();

        mMainFragment = (MainFragment) fm.findFragmentByTag(TAG_MAIN_FRAGMENT);

        //create the fragment on the first run
        if (mMainFragment == null) {
            Log.v(LOG_TAG, "creating new MainFragment");
        //if (mMainFragment == null) {
            mMainFragment = new MainFragment();
            fm.beginTransaction().replace(R.id.gui_container, mMainFragment, TAG_MAIN_FRAGMENT).commit();


            //FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            //fragmentTransaction.replace(R.id.gui_container, mMainFragment).commit();
        }
    }
}
