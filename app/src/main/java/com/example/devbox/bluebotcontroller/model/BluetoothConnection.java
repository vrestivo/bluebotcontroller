package com.example.devbox.bluebotcontroller.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.support.annotation.VisibleForTesting;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class BluetoothConnection implements IBluetoothConnection {


    //NOTE this is SPP (Serial Port Profile) UUID
    //see the link below
    //https://developer.android.com/reference/android/bluetooth/BluetoothDevice.html#createRfcommSocketToServiceRecord%28java.util.UUID%29
    public static final UUID SPP_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //                           ^^^^
    // UUID section marked by a "^" points to the device type, in this case
    // a serial device


    private BluetoothSocket mBluetoothSocket;
    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> mBondedDevices;
    private IModel mModel;
    private Context mApplicationContext;


    public BluetoothConnection(IModel model, Context context) {
        mModel = model;
        mApplicationContext = context;
        initializeAdapter();
    }


    private void initializeAdapter(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public Set<BluetoothDevice> getBondedDevices() {
        if(mBluetoothAdapter!=null && mBluetoothAdapter.isEnabled()){
            mBondedDevices = mBluetoothAdapter.getBondedDevices();
        }
        return mBondedDevices;
    }

    @Override
    public void startDiscovery() {
        if(mBluetoothAdapter!=null) {
            if(isConnected()){
                disconnect();
            }
            if(mBluetoothAdapter.isDiscovering()){
                mBluetoothAdapter.cancelDiscovery();
            }
            mBluetoothAdapter.startDiscovery();
        }
    }

    @Override
    public void stopDiscovery() {
        if(mBluetoothAdapter!=null){
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    @Override
    public void notifyMainPresenter(String message) {
        if(mModel!=null && message!=null){
            mModel.notifyMainPresenter(message);
        }
    }

    @Override
    public void notifyDiscoveryPresenter(String message) {
        if(mModel != null && message != null){
            mModel.notifyDiscoveryPresenter(message);
        }
    }

    @Override
    public void updateDeviceStatus(String status) {
        //TODO Implement
        if(mModel!=null && status!=null){
            mModel.updateDeviceStatus(status);
        }
    }

    @Override
    public void connectToRemoteDevice(BluetoothDevice remoteDevice) {
        if(remoteDevice!=null){
            try {
                mBluetoothSocket = remoteDevice.createRfcommSocketToServiceRecord(SPP_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sendMessageToRemoteDevice(String message) {
        if(mBluetoothSocket != null && isConnected()){
            //TODO implement reactively

        }

    }

    @Override
    public void disconnect() {
        closeStreams();
        if(mBluetoothSocket!=null) {
            try {
                mBluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isConnected() {
        if(mBluetoothSocket!=null) {
            return mBluetoothSocket.isConnected();
        }
        else {
            return false;
        }
    }

    private void closeStreams(){
        //TODO implement
    }

    private void cleanup(){
        //TODO clean up resources
    }



}
