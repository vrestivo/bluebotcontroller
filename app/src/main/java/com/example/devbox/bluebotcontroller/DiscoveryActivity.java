package com.example.devbox.bluebotcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by devbox on 2/10/17.
 */

public class DiscoveryActivity extends AppCompatActivity {

    private ListView mPairedDevices;
    private ListView mAvailableDevices;
    private Button mScanButton;
    private BluetoothAdapter mBtAdapter;
    private ArrayList<String> mPaired;
    private ArrayList<String> mAvailable;


    private final BroadcastReceiver mBtReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String devName = device.getName();
                String devAddress = device.getAddress();
                mAvailable.add(devName + " " + devAddress);
            }
        }
    };



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);

        mPaired = new ArrayList<>();
        mAvailable = new ArrayList<>();

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        mPairedDevices = (ListView) findViewById(R.id.devices_paired_lv);
        mAvailableDevices = (ListView) findViewById(R.id.devices_available_lv);
        mScanButton = (Button) findViewById(R.id.button_scan);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBtReceiver, filter);

        mPairedDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mAvailableDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBtReceiver);
    }



    public void clickPaired(){
        //TODO connect to paired device
    }

    public void clickAvailable(){
        //TODO connect to available device
    }

    public void scan(){
        if(mBtAdapter.isEnabled()){
            //check if adapter is already discovering
            //if so cancel the ongoing discovery
            if(mBtAdapter.isDiscovering()){
                mBtAdapter.cancelDiscovery();
            }
            mBtAdapter.startDiscovery();

        }
        else{
            Toast.makeText(getApplicationContext(),
                    getString(R.string.bt_error_off),
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }



}
