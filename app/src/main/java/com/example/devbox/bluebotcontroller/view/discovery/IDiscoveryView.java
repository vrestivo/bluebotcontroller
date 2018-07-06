package com.example.devbox.bluebotcontroller.view.discovery;

import android.bluetooth.BluetoothDevice;

import java.util.Set;

public interface IDiscoveryView {
    boolean isBluetoothEnabled();
    void scanForDevices();
    void getKnownDevices();
    void loadPairedDevices(Set<BluetoothDevice> pairedDevices);
    void loadAvailableDevices(Set<BluetoothDevice> availableDevices);
    void displayMessage(String messageToDisplay);
    void onBluetoothOff();
    void lifecycleCleanup();
}
