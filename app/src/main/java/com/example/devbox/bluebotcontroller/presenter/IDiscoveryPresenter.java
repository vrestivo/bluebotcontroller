package com.example.devbox.bluebotcontroller.presenter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.Set;

public interface IDiscoveryPresenter {

    void loadAvailableDevices(Set<BluetoothDevice> availableDevices);
    void loadPairedDevices(Set<BluetoothDevice> pairedDevices);
    void onDeviceSelected(BluetoothDevice selectedDevice);
    void sendMessageToUI(String mesageToUI);
    void onDeviceFound();
    void onBluetoothOff();

}


