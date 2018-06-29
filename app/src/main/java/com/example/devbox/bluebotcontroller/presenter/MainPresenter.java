package com.example.devbox.bluebotcontroller.presenter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.devbox.bluebotcontroller.model.Model;
import com.example.devbox.bluebotcontroller.view.IMainView;

import com.example.devbox.bluebotcontroller.model.IModel;
import com.example.devbox.bluebotcontroller.view.MainViewActivity;

public class MainPresenter implements IMainPresenter {

    private IMainView mMainView;
    private IModel mModel;

    public MainPresenter(@NonNull IMainView mainView, @NonNull Context applicationContext) {
        mMainView = mainView;
        mModel = Model.getInstance(applicationContext, this);
    }


    @Override
    public boolean isBluetoothSupported() {
        if(mModel!=null){
            return mModel.isBluetoothSupported();
        }
        return false;
    }


    @Override
    public boolean bluetoothPermissionsGranted() {
        if(mModel!=null){
           return mModel.bluetoothPermissionsGranted();
        }
        return false;
    }


    @Override
    public void requestBluetoothPermissions() {
        if(mMainView != null){
            mMainView.requestBluetoothPermissions();
        }
    }


    @Override
    public void sendMessageToUI(String messageToUI) {
        if(mMainView!=null){
            mMainView.showMessage(messageToUI);
        }
    }


    @Override
    public void sendMessageToRemoteDevice(String messageToDevice) {
        if(mModel!=null){
            mModel.sendMessageToRemoteDevice(messageToDevice);
        }
    }


    @Override
    public void updateDeviceStatus(String status) {
        if(mMainView!=null){
            mMainView.showDeviceStatus(status);
        }
    }


    @Override
    public void enableBluetooth() {
        if(mModel!=null){
            mModel.enableBluetooth();
        }
    }


    @Override
    public void disableBluetooth() {
        if(mModel!=null){
            mModel.disableBluetooth();
        }
    }


    @Override
    public void onBluetoothOn() {
        if(mMainView!=null){
            mMainView.onBluetoothOn();
        }
    }


    @Override
    public void onBluetoothOff() {
        if(mMainView!=null){
            mMainView.onBluetoothOff();
        }
    }


    @Override
    public boolean isBluetoothEnabled() {
        if(mModel!=null){
            return mModel.isBluetoothEnabled();
        }
        return false;
    }


    @Override
    public void disableBluetoothFeatures() {
        if(mMainView!=null){
            mMainView.disableBluetoothFeatures();
        }
    }


    @Override
    public void disconnect() {
        if(mModel!=null) {
            mModel.disconnect();
        }
    }


    @Override
    public void cleanup() {
        mMainView = null;
        mModel.cleanup();
        mModel = null;
    }
}
