package com.example.devbox.bluebotcontroller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

/**
 * Created by devbox on 2/10/17.
 */

public class DiscoveryActivity extends AppCompatActivity {

    private ListView mPairedDevices;
    private ListView mAvailableDevices;
    private Button mScanButton;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);

        mPairedDevices = (ListView) findViewById(R.id.devices_paired_lv);
        mAvailableDevices = (ListView) findViewById(R.id.devices_available_lv);
        mScanButton = (Button) findViewById(R.id.button_scan);

    }


}
