package com.example.devbox.bluebotcontroller.presenter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public interface IDiscoveryPresenter {

    void loadAvailableDevices();
    void loadPairedDevices();
    void onDeviceSelected(BluetoothDevice selectedDevice);
    void onDeviceFound();

}


