package com.example.devbox.bluebotcontroller.view;

import android.bluetooth.BluetoothDevice;

import java.util.Set;

public interface IDiscoveryView {

    void scanForDevices();
    void onDeviceFound(BluetoothDevice newDevice);
    void getKnownDevices();
    void loadPairedDevices(Set<BluetoothDevice> pairedDevices);
    void loadAvailableDevices(Set<BluetoothDevice> pairedDevices);
    void displayMessage(String messageToDisplay);
    void onBluetoothOff();
    void lifecycleCleanup();

}
