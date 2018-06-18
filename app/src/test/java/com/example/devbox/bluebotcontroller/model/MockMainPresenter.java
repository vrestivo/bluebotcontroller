package com.example.devbox.bluebotcontroller.model;

import com.example.devbox.bluebotcontroller.presenter.IMainPresenter;


/**
 *  No implementation since this class will be mocked out
 */
public class MockMainPresenter implements IMainPresenter {

    @Override
    public void sendMessageToUI(String messageToUI) {

    }

    @Override
    public void sendMessageToRemoteDevice(String messageToDevice) {

    }

    @Override
    public void updateDeviceStatus(String status) {

    }

    @Override
    public void disableBluetoothFeatures() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void cleanup() {

    }
}
