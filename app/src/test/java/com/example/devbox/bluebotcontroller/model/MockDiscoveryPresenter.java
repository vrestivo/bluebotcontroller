package com.example.devbox.bluebotcontroller.model;

import android.bluetooth.BluetoothDevice;

import com.example.devbox.bluebotcontroller.presenter.IDiscoveryPresenter;

import java.util.Set;

/**
 *  No implementation since this class will be mocked out
 */
public class MockDiscoveryPresenter implements IDiscoveryPresenter {

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
    public void sendMesageToUI(String mesageToUI) {

    }

    @Override
    public void onDeviceFound() {

    }

}
