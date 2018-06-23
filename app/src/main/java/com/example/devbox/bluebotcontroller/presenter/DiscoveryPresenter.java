package com.example.devbox.bluebotcontroller.presenter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.devbox.bluebotcontroller.model.IModel;
import com.example.devbox.bluebotcontroller.model.Model;
import com.example.devbox.bluebotcontroller.view.IDiscoveryView;

import java.util.Set;

public class DiscoveryPresenter implements IDiscoveryPresenter {

    private IDiscoveryView mDiscoveryView;
    private IModel mModel;



    public DiscoveryPresenter(@NonNull IDiscoveryView discoveryView, @NonNull Context applicationContext) {
        mDiscoveryView = discoveryView;
        mModel = Model.getInstance(applicationContext, this);
    }

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
    public void sendMessageToUI(String messageToUI) {

    }


    @Override
    public void onBluetoothOff() {
        if(mDiscoveryView!=null){
            mDiscoveryView.onBluetoothOff();
        }
    }
}
