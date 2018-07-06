package com.example.devbox.bluebotcontroller.model.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {

    final public static String ACTION_SELF_UNREGISTER = "ACTION_SELF_UNREGISTER";

    private BluetoothConnection mBluetoothConnection;

    public BluetoothBroadcastReceiver(BluetoothConnection connection) {
        mBluetoothConnection = connection;
    }


    public IntentFilter generateIntentFilters() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothBroadcastReceiver.ACTION_SELF_UNREGISTER);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        return intentFilter;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();

            System.out.println("DEBUG: _in BTReceiver: " + action);

            switch (action) {
                case BluetoothBroadcastReceiver.ACTION_SELF_UNREGISTER: {
                    mBluetoothConnection.unregisterReceiver();
                    break;
                }
                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED: {
                    handleConnectionStateChange(intent);
                    break;
                }
                case BluetoothDevice.ACTION_FOUND: {
                    if (intent.hasExtra(BluetoothDevice.EXTRA_DEVICE)) {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (device != null) handleDeviceAdded(device);
                        break;
                    }
                }
                case BluetoothAdapter.ACTION_STATE_CHANGED: {
                    if (intent.hasExtra(BluetoothAdapter.EXTRA_STATE)) {
                        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_ON);
                        handleOnOffState(state);
                    }
                    break;
                }
            }
        }
    }


    private void handleConnectionStateChange(Intent intent) {
        if (intent != null && intent.hasExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE)) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE,
                    BluetoothAdapter.STATE_DISCONNECTED);

            if (mBluetoothConnection != null) {
                mBluetoothConnection.updateConnectionStatus(state);
            }
        }
    }


    private void handleDeviceAdded(BluetoothDevice foundDevice) {
        if (mBluetoothConnection != null) {
            mBluetoothConnection.onDeviceFound(foundDevice);
        }
    }


    private void handleOnOffState(int state) {
        if (mBluetoothConnection != null)
            System.out.println("DEBUG: _in BTReceiver handleState: " + state);

        switch (state) {
                case BluetoothAdapter.STATE_OFF: {
                    mBluetoothConnection.disconnect();
                    mBluetoothConnection.onBluetoothOff();
                    break;
                }
                case BluetoothAdapter.STATE_ON: {
                    mBluetoothConnection.onBluetoothOn();
                    break;
                }
                case BluetoothAdapter.STATE_CONNECTED: {
                    mBluetoothConnection.updateConnectionStatus(BluetoothAdapter.STATE_CONNECTED);
                    break;
                }
                case BluetoothAdapter.STATE_DISCONNECTED: {
                    mBluetoothConnection.updateConnectionStatus(BluetoothAdapter.STATE_DISCONNECTED);
                    break;
                }
                case BluetoothAdapter.ERROR:{
                    mBluetoothConnection.updateConnectionStatus(BluetoothAdapter.ERROR);
                    break;
                }
            }
    }


}
