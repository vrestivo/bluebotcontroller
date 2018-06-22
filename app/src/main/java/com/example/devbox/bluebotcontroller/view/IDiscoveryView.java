package com.example.devbox.bluebotcontroller.view;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.Set;

public interface IDiscoveryView {

    void scanForDevices();
    void onDeviceFound(BluetoothDevice newDevice);
    void loadPairedDevices(Set<BluetoothDevice> pairedDevices);
    void onBluetoothOff();
    void cleanup();

}
