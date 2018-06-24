package com.example.devbox.bluebotcontroller.model;

import android.bluetooth.BluetoothDevice;

import java.util.Set;

public interface IBluetoothConnection {

    void verifyBluetoothSupport();
    boolean isBluetoothEnabled();
    Set<BluetoothDevice> getBondedDevices();
    void getKnownDevices();
    void scanForDevices();
    void startDiscovery();
    void stopDiscovery();
    void onDeviceFound(BluetoothDevice device);
    void onBluetoothOff();
    void onBluetoothOn();
    void connectToRemoteDevice(BluetoothDevice remoteDevice);
    void sendMessageToRemoteDevice(String message);
    boolean isConnected();
    void disconnect();
    void notifyMainPresenter(String message);
    void notifyDiscoveryPresenter(String message);
    void updateConnectionStatusIndicator(String status);
    void unregisterReceiver();
    void updateConnectionStatus(int status);
}
