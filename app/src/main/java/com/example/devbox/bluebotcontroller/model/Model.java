package com.example.devbox.bluebotcontroller.model;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.example.devbox.bluebotcontroller.presenter.IDiscoveryPresenter;
import com.example.devbox.bluebotcontroller.presenter.IMainPresenter;

import java.util.ArrayList;
import java.util.Set;

public class Model implements IModel {

    private static IModel sModelInstance;
    private static IMainPresenter sMainPresenter;
    private static IDiscoveryPresenter sDiscoveryPresenter;
    private static Context sApplicationContext;
    private static IBluetoothConnection sBluetoothConnection;

    private Model() {

    }

    public static IModel getInstance(Context applicationContext, IMainPresenter mainPresenter) {
        initModel(applicationContext);
        sMainPresenter = mainPresenter;
        return sModelInstance;
    }

    public static IModel getInstance(Context applicationContext, IDiscoveryPresenter discoveryPresenter) {
        initModel(applicationContext);
        sDiscoveryPresenter = discoveryPresenter;
        return sModelInstance;
    }


    private static void initModel(Context applicationContext){
        if (sModelInstance == null){
            sModelInstance = new Model();
            sApplicationContext = applicationContext;
            sBluetoothConnection = new BluetoothConnection(sModelInstance, applicationContext);
        }
    }


    @Override
    public boolean isBluetoothSupported() {
        //TODO implement
        if(sBluetoothConnection != null){
            return sBluetoothConnection.isBluetoothSupported();
        }
        return false;
    }


    @Override
    public boolean isBluetoothEnabled() {
        if(sBluetoothConnection != null){
            return sBluetoothConnection.isBluetoothEnabled();
        }
        return false;
    }


    @Override
    public ArrayList<BluetoothDevice> scanForDevices() {
        return null;
    }


    @Override
    public Set<BluetoothDevice> getPairedDevices() {
        return null;
    }


    @Override
    public void startDiscovery() {
        if(sBluetoothConnection!=null){
            if(sBluetoothConnection.isConnected()){
                sBluetoothConnection.disconnect();
            }
            sBluetoothConnection.startDiscovery();
        }
    }

    @Override
    public void stopDiscovery() {
        if(sBluetoothConnection!=null){
            sBluetoothConnection.stopDiscovery();
        }
    }

    @Override
    public void loadPairedDevices(Set<BluetoothDevice> pairedDevices) {

    }

    @Override
    public void loadAvailableDevices(Set<BluetoothDevice> availableDevices) {

    }

    @Override
    public void sendMessageToRemoteDevice(String message) {
        if(sBluetoothConnection != null && sBluetoothConnection.isConnected() && message !=null){
            sBluetoothConnection.sendMessageToRemoteDevice(message);
        }
    }

    @Override
    public void connectToDevice(BluetoothDevice device) {
        if(sBluetoothConnection!=null && device!=null){
            sBluetoothConnection.connectToRemoteDevice(device);
        }
    }

    @Override
    public void notifyMainPresenter(String message) {
        if(sMainPresenter!=null){
            sMainPresenter.sendMessageToUI(message);
        }
    }

    @Override
    public void notifyDiscoveryPresenter(String message) {
        if(sDiscoveryPresenter!=null){
            sDiscoveryPresenter.sendMessageToUI(message);
        }
    }

    @Override
    public void updateDeviceStatus(String newStatus) {
        if(sMainPresenter!=null) {
            sMainPresenter.updateDeviceStatus(newStatus);
        }
    }


    @Override
    public void onBluetoothOff() {
        //TODO implement
    }

    @Override
    public void disableBluetoothFeatures() {
        if(sMainPresenter!=null){
            sMainPresenter.disableBluetoothFeatures();
        }
    }

    @Override
    public void disconnect() {
        if(sBluetoothConnection!=null && sBluetoothConnection.isConnected()){
            sBluetoothConnection.disconnect();
        }

    }

    @Override
    public void cleanup() {

        sMainPresenter = null;
        sDiscoveryPresenter = null;
        //TODO cleanup Bluetooth resources
    }
}
