package com.example.devbox.bluebotcontroller.view;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.devbox.bluebotcontroller.R;
import com.example.devbox.bluebotcontroller.presenter.DiscoveryPresenter;

import java.util.Set;

public class DiscoveryViewActivity extends AppCompatActivity implements IDiscoveryView {

    private DiscoveryPresenter mDiscoveryPresenter;
    private Button mScanButton;

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
        //TODO finish UI unitialization
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mDiscoveryPresenter==null){
            mDiscoveryPresenter = new DiscoveryPresenter(this, getApplicationContext());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        lifecycleCleanup();
    }


    @Override
    public void scanForDevices() {
        if(mDiscoveryPresenter!=null) mDiscoveryPresenter.scanForDevices();
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

    }

    @Override
    public void loadAvailableDevices(Set<BluetoothDevice> pairedDevices) {

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
        if(mScanButton!=null) mScanButton.setEnabled(false);
    }

    @Override
    public void lifecycleCleanup() {
        if(mDiscoveryPresenter!=null) mDiscoveryPresenter.lifecycleCleanup();
        mDiscoveryPresenter = null;
    }
}
