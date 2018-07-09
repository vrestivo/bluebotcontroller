package com.example.devbox.bluebotcontroller;

import java.util.UUID;

/**
 * Created by devbox on 2/13/17.
 */

public class Constants {
    //NOTE this is SPP (Serial Port Profile) UUID
    //see the link below
    //https://developer.android.com/reference/android/bluetooth/BluetoothDevice.html#createRfcommSocketToServiceRecord%28java.util.UUID%29
    public static final UUID SPP_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //                           ^^^^
    // UUID section marked by a "^" points to the device type, in this case
    // a serial device
    public static final String STATUS_DISCONNECTED = "Disconnected";
    public static final String STATUS_CONNECTED = "Connected";
    public static final String STATUS_ERROR = "Connection Error";
    public static final String STATUS_NOT_SUPPORTED = "Not supported";
    public static final String MSG_BT_NOT_SUPPORTED = "Bluetooth not supported";
}
