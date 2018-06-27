package com.example.devbox.bluebotcontroller.view;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.Set;

public class DiscoveryVeiwActivity extends AppCompatActivity implements IDiscoveryView {




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    }

    @Override
    public void lifecycleCleanup() {

    }
}
