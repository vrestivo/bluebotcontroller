package com.example.devbox.bluebotcontroller.model;

import android.bluetooth.BluetoothDevice;

public interface IBluetoothConnectionThreadContract {

    void connect(BluetoothDevice device);
    void disconnect();
    void sendMessageToRemoteDevice();
    boolean isConnected();

}
