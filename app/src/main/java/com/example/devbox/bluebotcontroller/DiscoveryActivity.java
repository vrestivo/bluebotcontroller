package com.example.devbox.bluebotcontroller;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by devbox on 2/10/17.
 */

public class DiscoveryActivity extends AppCompatActivity {

    //constant definitions
    public static final String DEVICE_NAME = "DEV_NAME";
    public static final String DEVICE_ADDRESS = "DEV_ADDR";
    public static final String DEVICE_STRING = "DEV_STR";
    public static final long SCAN_PERIOD = 10000;

    //btle stuff
    private BluetoothLeScanner mBtScanner;
    private ScanCallback mScanCallback;

    private Handler mScanHandler;
    private ListView mPairedDevicesView;
    private ListView mAvailableDevicesView;
    private Button mScanButton;
    private BluetoothAdapter mBtAdapter;
    private ArrayList<String> mPairedArrayList = new ArrayList<>();
    private ArrayList<String> mAvailableArrayList = new ArrayList<>();
    private BtScanAdapter mAvailableAdapter;
    private ArrayAdapter<String> mPairedAdapter;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);


        mBtAdapter = BluetoothAdapter.getDefaultAdapter();



        mBtScanner = mBtAdapter.getBluetoothLeScanner();


        mScanHandler = new Handler();


        //TODO Extract device string and store in array
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        mPairedArrayList = Util.extractDeviceString(pairedDevices);

        mPairedDevicesView = (ListView) findViewById(R.id.devices_paired_lv);
        mAvailableDevicesView = (ListView) findViewById(R.id.devices_available_lv);


        mAvailableAdapter = new BtScanAdapter();
        mPairedAdapter = new ArrayAdapter<String>(this, R.layout.device_list_item, mPairedArrayList);

        mScanButton = (Button) findViewById(R.id.button_scan);

        mAvailableDevicesView.setAdapter(mAvailableAdapter);
        mPairedDevicesView.setAdapter(mPairedAdapter);


        mPairedDevicesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mBtAdapter.cancelDiscovery();
                String deviceString = ((TextView) view).getText().toString();
                Intent resultIntent = new Intent();
                resultIntent.putExtra(DEVICE_STRING, deviceString);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });


        mAvailableDevicesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mBtAdapter.cancelDiscovery();
                String deviceString = ((TextView) view).getText().toString();
                Intent resultIntent = new Intent();
                resultIntent.putExtra(DEVICE_STRING, deviceString);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtAdapter.cancelDiscovery();
                scan();
            }
        });

        mScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                mAvailableAdapter.add(result.getDevice());
                mAvailableAdapter.notifyDataSetChanged();
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                for(ScanResult result: results){
                    mAvailableAdapter.add(result.getDevice());
                }
                mAvailableAdapter.notifyDataSetChanged();
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Toast.makeText(getApplicationContext(),
                        "Scan Error: " + errorCode,
                        Toast.LENGTH_SHORT).show();
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //TODO delete logging
        Log.v("_onResume: ", "registering broadcast receiver");

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mBtReceiver, filter);

        this.registerReceiver(mBtReceiver, filter);

    }

    @Override
    protected void onPause() {
        super.onPause();

        //TODO delete logging
        Log.v("_onPause: ", "unregistering broadcast receiver");

        //LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mBtReceiver);
        this.unregisterReceiver(mBtReceiver);

        //TODO verify needed
        mBtAdapter.cancelDiscovery();
    }


    public void scan() {
        if (mBtAdapter.isEnabled()) {
            //check if adapter is already discovering
            //if so cancel the ongoing discovery
//            if (mBtAdapter.isDiscovering()) {
//                mBtAdapter.cancelDiscovery();
//            }
            //mBtAdapter.startDiscovery();
            mBtScanner.startScan(mScanCallback);

        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.bt_error_off),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }


    public class BtScanAdapter extends BaseAdapter {

        private ArrayList<BluetoothDevice> mBtDevArrayList;
        private LayoutInflater mInflator;

        public BtScanAdapter() {
            super();
            mBtDevArrayList = new ArrayList<>();
            mInflator = DiscoveryActivity.this.getLayoutInflater();

        }

        @Override
        public int getCount() {
            return mBtDevArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBtDevArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView deviceTv = null;

            if (convertView == null) {
                deviceTv = (TextView) mInflator.inflate(R.layout.device_list_item, null);
            } else {
                deviceTv = (TextView) convertView;
            }

            BluetoothDevice device = mBtDevArrayList.get(position);
            if(device.getAddress()!= null && device.getName()!=null){
                String devString = device.getName() + " " + device.getAddress();
                deviceTv.setText(devString);
            }
            else{
                //TODO put in strings.xml
                deviceTv.setText("Unavailable");
            }


            return deviceTv;
        }

        public void add(BluetoothDevice device) {
            mBtDevArrayList.add(device);
        }

        public void clearAll() {
            mBtDevArrayList.clear();
        }

    }

}