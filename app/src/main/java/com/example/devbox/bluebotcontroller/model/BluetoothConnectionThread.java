package com.example.devbox.bluebotcontroller.model;

import android.bluetooth.BluetoothDevice;
import android.os.HandlerThread;

public class BluetoothConnectionThread extends HandlerThread implements IBluetoothConnectionThreadContract {

    public BluetoothConnectionThread(String name) {
        super(name);
    }




    public void connect(BluetoothDevice device){

    }

    public void disconnect(){

    }

    @Override
    public void sendMessageToRemoteDevice() {

    }

    @Override
    public boolean isConnected() {
        return false;
    }
}
