package com.example.devbox.bluebotcontroller.presenter;

public interface IMainPresenter {
    boolean bluetoothPermissionsGranted();
    void requestBluetoothPermissions();
    void sendMessageToUI(String messageToUI);
    void sendMessageToRemoteDevice(String messageToDevice);
    void updateDeviceStatus(String status);
    void enableBluetooth();
    void disableBluetooth();
    void onBluetoothOn();
    void onBluetoothOff();
    boolean isBluetoothEnabled();
    boolean isBluetoothSupported();
    void disableBluetoothFeatures();
    void disconnect();
    void cleanup();
}
