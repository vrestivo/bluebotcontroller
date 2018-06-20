package com.example.devbox.bluebotcontroller.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {

    private BluetoothConnection mBluetoothConnection;

    public BluetoothBroadcastReceiver(BluetoothConnection connection) {
        mBluetoothConnection = connection;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }


}
