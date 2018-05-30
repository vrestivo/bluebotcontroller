package model;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;

public interface IModel {

    ArrayList<BluetoothDevice> scanForDevices();
    void connectToDevice(BluetoothDevice);
    void sendMessageToUI(String message);
    void updateDeviceStatus();
    void scanForDevices();
    void disconnect();

}
