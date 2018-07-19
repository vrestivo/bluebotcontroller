package com.example.devbox.bluebotcontroller.view.discovery;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.devbox.bluebotcontroller.R;
import com.example.devbox.bluebotcontroller.presenter.DiscoveryPresenter;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DiscoveryViewActivity extends AppCompatActivity implements IDiscoveryView, BluetoothDeviceListAdapter.OnDeviceSelected {

    private DiscoveryPresenter mDiscoveryPresenter;
    @BindView(R.id.button_scan) Button mScanButton;
    @BindView(R.id.devices_paired_rv) RecyclerView mPairedDevices;
    @BindView(R.id.devices_available_rv) RecyclerView mAvailableDevices;
    private BluetoothDeviceListAdapter mPairedDevicesAdapter;
    private BluetoothDeviceListAdapter mAvailableDevicesAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);
        ButterKnife.bind(this);
        initializeUI();
    }

    private void initializeUI(){
        Toolbar toolbar = findViewById(R.id.discovery_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanForDevices();
            }
        });
        initializeRecyclerViewsAndAdapters();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initializeRecyclerViewsAndAdapters(){
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);

        mPairedDevices.setLayoutManager(new LinearLayoutManager(this));
        mPairedDevicesAdapter = new BluetoothDeviceListAdapter(this);
        mPairedDevices.setAdapter(mPairedDevicesAdapter);
        mPairedDevices.addItemDecoration(divider);

        mAvailableDevices.setLayoutManager(new LinearLayoutManager(this));
        mAvailableDevicesAdapter = new BluetoothDeviceListAdapter(this);
        mAvailableDevices.setAdapter(mAvailableDevicesAdapter);
        mAvailableDevices.addItemDecoration(divider);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mDiscoveryPresenter == null){
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
        System.out.println("DEBUG: in onStop()");
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
        if(mDiscoveryPresenter != null && device!=null){
            mDiscoveryPresenter.onDeviceSelected(device);
        }
        lifecycleCleanup();
        finish();
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
