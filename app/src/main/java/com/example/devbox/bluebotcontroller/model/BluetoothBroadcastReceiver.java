package com.example.devbox.bluebotcontroller.model;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {

    final public static String ACTION_SELF_UNREGISTER = "ACTION_SELF_UNREGISTER";

    private BluetoothConnection mBluetoothConnection;

    public BluetoothBroadcastReceiver(BluetoothConnection connection) {
        mBluetoothConnection = connection;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        switch (action) {
            case BluetoothBroadcastReceiver.ACTION_SELF_UNREGISTER:{
                mBluetoothConnection.unregisterReceiver();
            }
            case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:{
                handleConnectionStateChange(intent);
            }
        }
    }


    private void handleConnectionStateChange(Intent intent){
        if(intent!=null && intent.hasExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE)){
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE,
                    BluetoothAdapter.STATE_DISCONNECTED);

            mBluetoothConnection.updateConnectionStatus(state);
        }
    }



}
