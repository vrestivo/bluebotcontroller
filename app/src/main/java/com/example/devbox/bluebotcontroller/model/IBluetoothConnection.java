package com.example.devbox.bluebotcontroller.model;

import android.bluetooth.BluetoothDevice;

import java.util.Set;

public interface IBluetoothConnection {

    Set<BluetoothDevice> getBondedDevices();
    void startDiscovery();
    void stopDiscovery();
    void connectToRemoteDevice(BluetoothDevice remoteDevice);
    void sendMessageToRemoteDevice(String message);
    boolean isConnected();
    void disconnect();
    void notifyMainPresenter(String message);
    void notifyDiscoveryPresenter(String message);
    void updateDeviceStatus(String status);

}
