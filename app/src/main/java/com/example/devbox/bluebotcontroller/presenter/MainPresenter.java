package com.example.devbox.bluebotcontroller.presenter;

import android.support.annotation.NonNull;

import com.example.devbox.bluebotcontroller.view.IMainView;

import com.example.devbox.bluebotcontroller.model.IModel;

public class MainPresenter implements IMainPresenter {

    private IMainView mMainView;
    private IModel mModel;

    public MainPresenter(@NonNull IMainView mainView, @NonNull IModel model) {
        mMainView = mainView;
        mModel = model;
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
    public void cleanup() {
        mMainView = null;
        mModel.cleanup();
        mModel = null;
    }
}