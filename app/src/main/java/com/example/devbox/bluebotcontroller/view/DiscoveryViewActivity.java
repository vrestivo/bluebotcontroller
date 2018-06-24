package com.example.devbox.bluebotcontroller.view;

import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;

import java.util.Set;

public class DiscoveryViewActivity extends AppCompatActivity implements IDiscoveryView {


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

    }

    @Override
    public void lifecycleCleanup() {

    }
}
