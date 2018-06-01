package com.example.devbox.bluebotcontroller.model;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.Set;

public interface IModel {

    ArrayList<BluetoothDevice> scanForDevices();
    Set<BluetoothDevice> getPairedDevices();
    void sendMessageToRemoteDevice(String message);
    void startDiscovery();
    void stopDiscovery();
    void connectToDevice(BluetoothDevice device);
    void notifyPresenter(String message);
    void updateDeviceStatus();
    void disconnect();
    void cleanup();

}
