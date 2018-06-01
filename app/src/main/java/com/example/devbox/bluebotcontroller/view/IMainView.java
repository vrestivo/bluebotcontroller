package com.example.devbox.bluebotcontroller.view;

public interface IMainView {
    void startBluetoothDiscovery();
    void disconnect();
    void cleaup();
    void showMessage(String message);
    void sendMessageToRemoteDevice(String message);
    void showDeviceStatus(String status);
}
