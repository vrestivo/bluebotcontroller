package com.example.devbox.bluebotcontroller.model;

import android.bluetooth.BluetoothDevice;

import java.util.Set;

public interface IModel {

    void verifyBluetoothSupport();
    boolean isBluetoothSupported();
    boolean isBluetoothEnabled();
    void scanForDevices();
    void sendMessageToRemoteDevice(String message);
    void startDiscovery();
    void stopDiscovery();
    void getKnownDevices();
    void loadPairedDevices(Set<BluetoothDevice> pairedDevices);
    void loadAvailableDevices(Set<BluetoothDevice> availableDevices);
    void connectToDevice(BluetoothDevice device);
    void notifyMainPresenter(String message);
    void notifyDiscoveryPresenter(String message);
    void updateDeviceStatus(String newStatus);
    void enableBluetooth();
    void disableBluetooth();
    void onBluetoothOff();
    void onBluetoothOn();
    void checkBluetoothPermissions();
    void disableBluetoothFeatures();
    void disconnect();
    void cleanup();

}
