package com.example.devbox.bluebotcontroller.view;

import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;

import com.example.devbox.bluebotcontroller.presenter.DiscoveryPresenter;

import java.util.Set;

public class DiscoveryViewActivity extends AppCompatActivity implements IDiscoveryView {

    private DiscoveryPresenter mDiscoveryPresenter;


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

    }

    @Override
    public void onDeviceFound(BluetoothDevice newDevice) {

    }

    @Override
    public void getKnownDevices() {

    }

    @Override
    public void loadPairedDevices(Set<BluetoothDevice> pairedDevices) {

    }

    @Override
    public void loadAvailableDevices(Set<BluetoothDevice> pairedDevices) {

    }

    @Override
    public void displayMessage(String messageToDisplay) {

    }

    @Override
    public void onBluetoothOff() {
        //TODO disable bluetooth-related UI
    }

    @Override
    public void lifecycleCleanup() {
        if(mDiscoveryPresenter!=null){
            mDiscoveryPresenter.lifecycleCleanup();
        }
        mDiscoveryPresenter = null;
    }
}
