package com.example.devbox.bluebotcontroller;

import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RemoteViewsService;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by devbox on 2/10/17.
 */

public class DiscoveryActivity extends AppCompatActivity {

    //constant definitions
    public static final String DEVICE_NAME = "DEV_NAME";
    public static final String DEVICE_ADDRESS = "DEV_ADDR";
    public static final String DEVICE_STRING = "DEV_STR";



    private ListView mPairedDevices;
    private ListView mAvailableDevices;
    private Button mScanButton;
    private BluetoothAdapter mBtAdapter;
    private ArrayList<String> mPaired = new ArrayList<>();
    private ArrayList<String> mAvailable = new ArrayList<>();
    private ArrayAdapter mAvailableAdapter;
    private ArrayAdapter mPairedAdapter;



    private final BroadcastReceiver mBtReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String devName = device.getName();
                String devAddress = device.getAddress();
                mAvailable.add(devName + " " + devAddress);
                mAvailable.notify();
                mAvailableAdapter.notifyDataSetChanged();
            }
        }
    };



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);

        mAvailableAdapter = new ArrayAdapter(this, R.layout.device_list_item, mAvailable);

        mPairedAdapter = new ArrayAdapter(this, R.layout.device_list_item);

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        mPairedDevices = (ListView) findViewById(R.id.devices_paired_lv);

        mAvailableDevices = (ListView) findViewById(R.id.devices_available_lv);
        mAvailableDevices.setAdapter(mAvailableAdapter);

        mScanButton = (Button) findViewById(R.id.button_scan);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBtReceiver, filter);


        mPairedDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String deviceString = ((TextView) view).getText().toString();
                Intent resultIntent = new Intent();
                resultIntent.putExtra(DEVICE_STRING, deviceString);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });


        mAvailableDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String deviceString = ((TextView) view).getText().toString();
                Intent resultIntent = new Intent();
                resultIntent.putExtra(DEVICE_STRING, deviceString);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
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
