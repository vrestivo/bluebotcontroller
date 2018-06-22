package com.example.devbox.bluebotcontroller.presenter;

public interface IMainPresenter {

    void sendMessageToUI(String messageToUI);
    void sendMessageToRemoteDevice(String messageToDevice);
    void updateDeviceStatus(String status);
    void onBluetoothOn();
    void onBluetoothOff();
    void disableBluetoothFeatures();
    void disconnect();
    void cleanup();
}
