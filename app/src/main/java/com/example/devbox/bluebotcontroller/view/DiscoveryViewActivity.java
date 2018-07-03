package com.example.devbox.bluebotcontroller.view;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.devbox.bluebotcontroller.R;
import com.example.devbox.bluebotcontroller.presenter.DiscoveryPresenter;

import java.util.Set;

public class DiscoveryViewActivity extends AppCompatActivity implements IDiscoveryView, BluetoothDeviceAdapter.OnDeviceSelected{

    private DiscoveryPresenter mDiscoveryPresenter;
    private Button mScanButton;
    private RecyclerView mPairedDevices;
    private RecyclerView mAvailableDevices;
    private BluetoothDeviceAdapter mPairedDevicesAdapter;
    private BluetoothDeviceAdapter mAvailableDevicesAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);
        initializeUI();
        // TODO get known devices
    }

    private void initializeUI(){
        mScanButton = findViewById(R.id.button_scan);
        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanForDevices();
            }
        });
        //TODO finish UI initialization
        initializeRecyclerViewsAndAdapters();
    }

    private void initializeRecyclerViewsAndAdapters(){
        mPairedDevices = findViewById(R.id.devices_paired_rv);
        mPairedDevices.setLayoutManager(new LinearLayoutManager(this));
        mPairedDevicesAdapter = new BluetoothDeviceAdapter(this);
        mPairedDevices.setAdapter(mPairedDevicesAdapter);
        //TODO initialize available devices list
        mAvailableDevices = findViewById(R.id.devices_available_rv);
        mAvailableDevices.setLayoutManager(new LinearLayoutManager(this));
        mAvailableDevicesAdapter = new BluetoothDeviceAdapter(this);
        mAvailableDevices.setAdapter(mAvailableDevicesAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mDiscoveryPresenter==null){
            mDiscoveryPresenter = new DiscoveryPresenter(this, getApplicationContext());
        }
        getKnownDevices();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isBluetoothEnabled()) bluetoothOffUI();
    }

    @Override
    protected void onStop() {
        super.onStop();
        lifecycleCleanup();
    }

    @Override
    public boolean isBluetoothEnabled() {
        if(mDiscoveryPresenter!=null) return mDiscoveryPresenter.isBluetoothEnabled();
        return false;
    }

    @Override
    public void scanForDevices() {
        if(mDiscoveryPresenter!=null) mDiscoveryPresenter.scanForDevices();
    }

    @Override
    public void onDeviceClick(BluetoothDevice device) {
        if(device!=null){
            // TODO implement
        }
    }

    @Override
    public void onDeviceFound(BluetoothDevice newDevice) {

    }

    @Override
    public void getKnownDevices() {
        if(mDiscoveryPresenter!=null) mDiscoveryPresenter.getKnownDevices();
    }

    @Override
    public void loadPairedDevices(Set<BluetoothDevice> pairedDevices) {
        if(mPairedDevicesAdapter!=null && pairedDevices!=null){
            mPairedDevicesAdapter.updateDeviceDataSet(pairedDevices);
        }
    }

    @Override
    public void loadAvailableDevices(Set<BluetoothDevice> availableDevices) {
        if(mAvailableDevicesAdapter!=null && availableDevices!=null){
            mAvailableDevicesAdapter.updateDeviceDataSet(availableDevices);
        }
    }

    @Override
    public void displayMessage(String messageToDisplay) {
        if(messageToDisplay!=null){
            Toast.makeText(this, messageToDisplay, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBluetoothOff() {
        bluetoothOffUI();
    }

    private void bluetoothOffUI(){
        // TODO disable device lists
        if(mScanButton!=null) mScanButton.setEnabled(false);
    }

    @Override
    public void lifecycleCleanup() {
        if(mDiscoveryPresenter!=null) mDiscoveryPresenter.lifecycleCleanup();
        mDiscoveryPresenter = null;
    }
}
