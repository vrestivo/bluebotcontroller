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
    public boolean isBluetoothEnabled() {
        if(mModel!=null) return mModel.isBluetoothEnabled();
        return false;
    }

    @Override
    public void scanForDevices() {
        if(mModel!=null){
            mModel.scanForDevices();
        }
    }

    @Override
    public void getKnownDevices() {
        if(mModel!= null){
            mModel.getKnownDevices();
        }
    }

    @Override
    public void loadAvailableDevices(Set<BluetoothDevice> availableDevices) {
        if(mDiscoveryView!=null && availableDevices != null && !availableDevices.isEmpty()){
           mDiscoveryView.loadAvailableDevices(availableDevices);
        }
    }

    @Override
    public void loadPairedDevices(Set<BluetoothDevice> pairedDevices) {
        if(mDiscoveryView!=null && pairedDevices != null && !pairedDevices.isEmpty()){
            mDiscoveryView.loadPairedDevices(pairedDevices);
        }
    }

    @Override
    public void onDeviceSelected(BluetoothDevice selectedDevice) {
        if(mModel != null && selectedDevice!=null){
            mModel.connectToDevice(selectedDevice);
        }
    }

    @Override
    public void sendMessageToUI(String messageToUI) {
        if(mDiscoveryView!=null){
            mDiscoveryView.displayMessage(messageToUI);
        }
    }

    @Override
    public void onBluetoothOff() {
        if(mDiscoveryView!=null){
            mDiscoveryView.onBluetoothOff();
        }
    }

    @Override
    public void lifecycleCleanup() {
        mDiscoveryView = null;
        mModel = null;

    }

}
