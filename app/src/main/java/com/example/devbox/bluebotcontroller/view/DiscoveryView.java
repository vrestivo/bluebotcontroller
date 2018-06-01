package com.example.devbox.bluebotcontroller.view;

import android.bluetooth.BluetoothAdapter;
import android.support.v7.app.AppCompatActivity;

import com.example.devbox.bluebotcontroller.view.joystick.IDiscoveryView;

public class DiscoveryView extends AppCompatActivity implements IDiscoveryView {

    @Override
    public BluetoothAdapter getBluetoothAdapter() {
        return null;
    }


}
