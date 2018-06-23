package com.example.devbox.bluebotcontroller.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.example.devbox.bluebotcontroller.view.IMainView;

public class MainViewActivity extends AppCompatActivity implements IMainView {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void checkBluetoothPermissions() {

    }

    @Override
    public void startBluetoothDiscovery() {
        //TODO implement
    }

    @Override
    public void disconnect() {
        //TODO implement
    }

    @Override
    public void cleanup() {
        //TODO implement
    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void sendMessageToRemoteDevice(String message) {

    }


    @Override
    public void onBluetoothOff() {

    }

    @Override
    public void disableBluetoothFeatures() {

    }

    @Override
    public void showDeviceStatus(String status) {

    }

    @Override
    public void requestBluetoothPermissions() {

    }


}
