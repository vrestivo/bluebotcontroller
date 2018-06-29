package com.example.devbox.bluebotcontroller.view;

public interface IMainView {
    void startBluetoothDiscovery();
    void disconnect();
    void cleanup();
    void showMessage(String message);
    void sendMessageToRemoteDevice(String message);
    void showDeviceStatus(String status);
    void requestBluetoothPermissions();
    void enableBluetoothFeatures();
    void disableBluetoothFeatures();
    void enableBluetooth();
    void disableBluetooth();
    void onBluetoothOn();
    void onBluetoothOff();
}
