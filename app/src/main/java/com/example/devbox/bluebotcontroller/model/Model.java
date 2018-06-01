package com.example.devbox.bluebotcontroller.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.example.devbox.bluebotcontroller.presenter.IMainPresenter;

import java.util.ArrayList;
import java.util.Set;

public class Model implements IModel {

    private static IModel sModelInstance;
    private static IMainPresenter sMainPresenter;
    private static Context sApplicationContext;
    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> mDevices;

    private Model() {

    }


    public static IModel getInstance(Context applicationnContext, IMainPresenter mainPresenter) {
        initModel(applicationnContext);
        return sModelInstance;
    }


    private static void initModel(Context applicationContext){
        if (sModelInstance == null){
            sModelInstance = new Model();
            sApplicationContext = applicationContext;
        }
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
        if(mBluetoothAdapter!=null){
            if (mBluetoothAdapter.isDiscovering()){
                mBluetoothAdapter.cancelDiscovery();
            }
            mBluetoothAdapter.startDiscovery();
        }
    }

    @Override
    public void stopDiscovery() {
        if(mBluetoothAdapter!=null)
        mBluetoothAdapter.cancelDiscovery();
    }

    @Override
    public void sendMessageToRemoteDevice(String message) {

    }

    @Override
    public void connectToDevice(BluetoothDevice device) {

    }

    @Override
    public void notifyPresenter(String message) {

    }

    @Override
    public void updateDeviceStatus() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void cleanup() {
        sMainPresenter = null;
        //TODO cleanup DiscoveryPresenter
        //TODO cleanup Bluetooth resources
    }
}
