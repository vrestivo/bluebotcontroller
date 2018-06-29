package com.example.devbox.bluebotcontroller.model;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Process;
import android.support.v4.content.ContextCompat;

import com.example.devbox.bluebotcontroller.Util;
import com.example.devbox.bluebotcontroller.presenter.IDiscoveryPresenter;
import com.example.devbox.bluebotcontroller.presenter.IMainPresenter;

import java.util.List;
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
        if(sBluetoothConnection!=null){
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
    public void getKnownDevices() {
        if(sBluetoothConnection!=null){
            sBluetoothConnection.getKnownDevices();
        }
    }


    @Override
    public void scanForDevices() {
        if(sBluetoothConnection!=null){
            sBluetoothConnection.scanForDevices();
        }
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
        if(sDiscoveryPresenter !=null && pairedDevices!=null){
            sDiscoveryPresenter.loadPairedDevices(pairedDevices);
        }
    }


    @Override
    public void loadAvailableDevices(Set<BluetoothDevice> availableDevices) {
        if(sDiscoveryPresenter!=null && availableDevices != null){
            sDiscoveryPresenter.loadAvailableDevices(availableDevices);
        }
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
    public void enableBluetooth() {
        if(sBluetoothConnection!=null){
            sBluetoothConnection.enableBluetooth();
        }
    }


    @Override
    public void disableBluetooth() {
        if(sBluetoothConnection!=null){
            sBluetoothConnection.disableBluetooth();
        }
    }


    @Override
    public void onBluetoothOn() {
        if(sMainPresenter!=null){
            sMainPresenter.onBluetoothOn();
        }
    }


    @Override
    public void onBluetoothOff() {
        if(sDiscoveryPresenter!=null){
            sDiscoveryPresenter.onBluetoothOff();
        }
        if(sMainPresenter!=null){
            sMainPresenter.onBluetoothOff();
        }
    }


    @Override
    public void checkBluetoothPermissions() {
        if(!bluetoothPermissionsGranted()){
            disableBluetoothFeatures();
            if(sMainPresenter!=null){
                sMainPresenter.requestBluetoothPermissions();
            }
        }
    }

    private boolean hasPermission(String permission){
        int result = sApplicationContext
                .checkPermission(permission, android.os.Process.myPid(), Process.myUid());
        if(result == PackageManager.PERMISSION_DENIED){
            return false;
        }
        return true;
    }

    @Override
    public boolean bluetoothPermissionsGranted(){
        //Needed to turn Turn On/Off Bluetooth
        List<String> neededBluetoothPermissions = Util.generateNeededBluetoothPermissionsList();

        for(String permission : neededBluetoothPermissions){
            if(!hasPermission(permission)){
                return false;
            }
        }
        return true;
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
