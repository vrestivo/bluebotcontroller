package com.example.devbox.bluebotcontroller.view;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.devbox.bluebotcontroller.R;
import com.example.devbox.bluebotcontroller.Util;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by devbox on 2/10/17.
 */

public class DiscoveryActivity extends AppCompatActivity {

    private String LOG_TAG = "DiscoveryActivity";

    //constant definitions
    public static final String DEVICE_NAME = "DEV_NAME";
    public static final String DEVICE_ADDRESS = "DEV_ADDR";
    public static final String DEVICE_STRING = "DEV_STR";
    public static final long SCAN_PERIOD = 15000;
    public static final int MY_PERMISSION_REQUEST = 777;

    private Handler mScanHandler;
    private ListView mPairedDevicesView;
    private ListView mAvailableDevicesView;
    private Button mScanButton;
    private BluetoothAdapter mBtAdapter;
    private BtScanAdapter mAvailableAdapter;
    private BtScanAdapter mPairedAdapter;

    //BT traditional
    private BroadcastReceiver mBtReceiver;
    private IntentFilter mIntentFilter;

    private Toolbar mToolbar;

    public DiscoveryActivity() {
        super();
        mBtReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                    //TODO delete logging
                    Log.v("_inBroadcastRecv", "action_discovery_finished");
                    mBtAdapter.cancelDiscovery();
                } else if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                    //TODO delete logging
                    Log.v("_inBroadcastRecv", "action_found");
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (!mAvailableAdapter.hasDevice(device)) {
                        mAvailableAdapter.add(device);
                        mAvailableAdapter.notifyDataSetChanged();
                    }
                }
            }
        };
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);

        mScanHandler = new Handler();
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        mToolbar = (Toolbar) findViewById(R.id.discovery_toolbar);
        setSupportActionBar(mToolbar);



        mScanButton = (Button) findViewById(R.id.button_scan);
        mPairedDevicesView = (ListView) findViewById(R.id.devices_paired_lv);
        mAvailableDevicesView = (ListView) findViewById(R.id.devices_available_lv);

        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        mAvailableAdapter = new BtScanAdapter();
        mPairedAdapter = new BtScanAdapter();
        Util.fillAdapterFromSet(pairedDevices, mPairedAdapter);

        mAvailableDevicesView.setAdapter(mAvailableAdapter);
        mPairedDevicesView.setAdapter(mPairedAdapter);

        //BT Traditional
        mIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBtReceiver, mIntentFilter);


        mPairedDevicesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mBtAdapter.isDiscovering()) {
                    mBtAdapter.cancelDiscovery();
                }
                BluetoothDevice btDev =  (BluetoothDevice) mPairedAdapter.getItem(position);
                String deviceMac = btDev.getAddress();
                Intent resultIntent = new Intent();
                resultIntent.putExtra(DEVICE_STRING, deviceMac);
                resultIntent.putExtra(BluetoothDevice.EXTRA_DEVICE, btDev);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });


        mAvailableDevicesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mBtAdapter.isDiscovering()) {
                    mBtAdapter.cancelDiscovery();
                }
                BluetoothDevice btDev =  (BluetoothDevice) mAvailableAdapter.getItem(position);
                String deviceMac = btDev.getAddress();
                Intent resultIntent = new Intent();
                resultIntent.putExtra(DEVICE_STRING, deviceMac);
                resultIntent.putExtra(BluetoothDevice.EXTRA_DEVICE, btDev);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discovery();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                    }, MY_PERMISSION_REQUEST
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //BT Traditional
        if (mBtReceiver != null) {
            try {
                unregisterReceiver(mBtReceiver);
            }
            catch (IllegalArgumentException e){
                Log.e(LOG_TAG, "mBtReceiver is not registered", e);
            }
        }
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public void discovery() {
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        mBtAdapter.startDiscovery();
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
            if (device.getAddress() != null && device.getName() != null) {
                String devString = device.getName() + " " + device.getAddress();
                deviceTv.setText(devString);
            } else if (device.getAddress() != null) {
                deviceTv.setText(device.getAddress());
            } else {
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

        public boolean hasDevice(BluetoothDevice device) {
            return mBtDevArrayList.contains(device);
        }

    }

}

