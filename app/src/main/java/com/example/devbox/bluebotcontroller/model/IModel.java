package com.example.devbox.bluebotcontroller.model;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.Set;

public interface IModel {

    boolean isBluetoothSupported();
    boolean isBluetoothEnabled();
    ArrayList<BluetoothDevice> scanForDevices();
    Set<BluetoothDevice> getPairedDevices();
    void sendMessageToRemoteDevice(String message);
    void startDiscovery();
    void stopDiscovery();
    void loadPairedDevices(Set<BluetoothDevice> pairedDevices);
    void loadAvailableDevices(Set<BluetoothDevice> availableDevices);
    void connectToDevice(BluetoothDevice device);
    void notifyMainPresenter(String message);
    void notifyDiscoveryPresenter(String message);
    void updateDeviceStatus(String newStatus);
    void disableBluetoothFeatures();
    void disconnect();
    void cleanup();

}
