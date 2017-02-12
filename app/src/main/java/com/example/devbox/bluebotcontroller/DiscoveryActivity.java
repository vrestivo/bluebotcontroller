package com.example.devbox.bluebotcontroller;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    public static final long SCAN_PERIOD = 15000;
    public static final int MY_PERMISSION_REQUEST = 777;

    //btle stuff
    private BluetoothLeScanner mBtScanner;
    private BtScanCallback mScanCallback;

    private Handler mScanHandler;
    private ListView mPairedDevicesView;
    private ListView mAvailableDevicesView;
    private Button mScanButton;
    private BluetoothAdapter mBtAdapter;
    private BtScanAdapter mAvailableAdapter;
    private BtScanAdapter mPairedAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);

        mScanHandler = new Handler();
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        mBtScanner = mBtAdapter.getBluetoothLeScanner();

        mScanButton = (Button) findViewById(R.id.button_scan);
        mPairedDevicesView = (ListView) findViewById(R.id.devices_paired_lv);
        mAvailableDevicesView = (ListView) findViewById(R.id.devices_available_lv);

        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        mAvailableAdapter = new BtScanAdapter();
        mPairedAdapter = new BtScanAdapter();
        Util.fillAdapterFromSet(pairedDevices, mPairedAdapter);

        mAvailableDevicesView.setAdapter(mAvailableAdapter);
        mPairedDevicesView.setAdapter(mPairedAdapter);


        mPairedDevicesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String deviceMac = ((BluetoothDevice) mPairedAdapter.getItem(position)).getAddress();
                Intent resultIntent = new Intent();
                resultIntent.putExtra(DEVICE_STRING, deviceMac);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });


        mAvailableDevicesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String deviceMac = ((BluetoothDevice) mPairedAdapter.getItem(position)).getAddress();
                Intent resultIntent = new Intent();
                resultIntent.putExtra(DEVICE_STRING, deviceMac);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();
            }
        });

        mScanCallback = new BtScanCallback();
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
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void scan() {
        if (mBtAdapter.isEnabled()) {


            if (mScanCallback == null) {
                mScanCallback = new BtScanCallback();
            }
            mScanHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopScan();
                }
            }, SCAN_PERIOD);

            if (mAvailableAdapter != null) {
                mAvailableAdapter.clearAll();
                mAvailableAdapter.notifyDataSetChanged();
            }

            mBtScanner.startScan(mScanCallback);

            Toast.makeText(getApplicationContext(), "Starting Scan", Toast.LENGTH_SHORT).show();


        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.bt_error_off),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void stopScan() {

        if (mScanCallback != null) {
            mBtScanner.stopScan(mScanCallback);
            mScanCallback = null;
            Toast.makeText(getApplicationContext(), "Stopping Scan", Toast.LENGTH_SHORT).show();
        }

        mAvailableAdapter.notifyDataSetChanged();

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

    public class BtScanCallback extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (!mAvailableAdapter.hasDevice(result.getDevice())) {
                mAvailableAdapter.add(result.getDevice());
                //TODO delete logging
                Log.v("_BtScanCallback:", "adding device: " + result.getDevice().getAddress());
            } else {
                //TODO delete logging
                Log.v("_BtScanCallback:", "already has device: " + result.getDevice().getAddress());
            }

            mAvailableAdapter.notifyDataSetChanged();
            //TODO delete logging
            Log.v("_ScanCallback:", "available devices: " + mAvailableAdapter.getCount());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            for (ScanResult result : results) {
                if (!mAvailableAdapter.hasDevice(result.getDevice())) {
                    mAvailableAdapter.add(result.getDevice());
                    //TODO delete logging
                    Log.v("_BtScanCallback:", "adding device: " + result.getDevice().getAddress());
                } else {
                    //TODO delete logging
                    Log.v("_BtScanCallback:", "already has device: " + result.getDevice().getAddress());
                }
            }
            mAvailableAdapter.notifyDataSetChanged();
            //TODO delete logging
            Log.v("_ScanCallback:", "available devices: " + mAvailableAdapter.getCount());
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Toast.makeText(getApplicationContext(),
                    "Scan Error: " + errorCode,
                    Toast.LENGTH_SHORT).show();
            //TODO delete logging
            Log.v("_ScanCallback:", "scan failed: ");
        }
    }
}

