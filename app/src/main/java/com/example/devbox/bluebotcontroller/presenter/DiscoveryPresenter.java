package com.example.devbox.bluebotcontroller.presenter;

import android.bluetooth.BluetoothDevice;

import com.example.devbox.bluebotcontroller.model.Model;

import java.util.Set;

public class DiscoveryPresenter implements IDiscoveryPresenter {

    @Override
    public void loadAvailableDevices(Set<BluetoothDevice> availableDevices) {

    }

    @Override
    public void loadPairedDevices(Set<BluetoothDevice> pairedDevices) {

    }

    @Override
    public void onDeviceSelected(BluetoothDevice selectedDevice) {

    }

    @Override
    public void sendMessageToUI(String mesageToUI) {

    }

    @Override
    public void onDeviceFound() {

    }

    @Override
    public void onBluetoothOff() {
        // TODO return to main activity
    }
}
