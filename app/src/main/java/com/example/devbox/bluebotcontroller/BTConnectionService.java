package com.example.devbox.bluebotcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by devbox on 2/13/17.
 */

public class BTConnectionService {

    private final String LOG_TAG = "BTConnectionService";

    //NOTE this have to use this the following UUID IOT connect to boards
    //see the link below
    //https://developer.android.com/reference/android/bluetooth/BluetoothDevice.html#createRfcommSocketToServiceRecord%28java.util.UUID%29
    private final UUID APP_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //state constants
    public static final int ST_NONE = 0;
    public static final int ST_LISTEN = 0;
    public static final int ST_CONNECTING = 0;
    public static final int ST_CONNECTED = 0;


    //TODO declare class variables

    private ConnectThread mConnectThread;
    private Handler mHandler;
    private int mState;
    private BluetoothAdapter mBtAdapter;
    private Context mParentContext;

    public BTConnectionService(Context context, Handler handler) {
        mHandler = handler;
        mParentContext = context;
        mState = ST_NONE;
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();


    }

    public synchronized void connect(BluetoothDevice device, boolean secure) {
        //if(mState == ST_CONNECTING){
        if (mConnectThread == null) {
            mConnectThread = new ConnectThread(device);
        }
        else{
            mConnectThread.cancel();
        }
        mConnectThread.start();

        //}
    }


    private class ConnectThread extends Thread {

        private final BluetoothDevice mmBtDevice;
        private final BluetoothSocket mmSocket;
        private InputStream mmInputStream;
        private OutputStream mmOutputStream;

        public ConnectThread(BluetoothDevice device) {

            mmBtDevice = device;
            mmInputStream = null;
            mmOutputStream = null;


            BluetoothSocket tmp = null;

            try {
                tmp = device.createInsecureRfcommSocketToServiceRecord(APP_UUID);
            } catch (IOException ioe) {
                String errorMessage = mParentContext.getString(R.string.bt_conn_error_failed_rfcomm);
                Log.e(LOG_TAG, errorMessage, ioe);
                sendToastToUi(errorMessage);

            }

            mmSocket = tmp;

            mState = ST_CONNECTING;

        }


        @Override
        public void run() {
            //TODO delete logging
            Log.v(LOG_TAG, "starting ConntectThread");

            mBtAdapter.cancelDiscovery();


            try {
                mmSocket.connect();
            } catch (IOException ioe1) {
                try {
                    mmSocket.close();
                } catch (IOException ioe2) {
                    Log.e(LOG_TAG, "closing socket failed", ioe2);
                }
                Log.e(LOG_TAG, "failed to connect", ioe1);
                sendToastToUi("failed to connect");
            }


            try {
                mmOutputStream = mmSocket.getOutputStream();
            } catch (IOException ioe) {
                Log.e(LOG_TAG, "failed to get OutputStream. connection not ready", ioe);
            }


            byte[] testMsg = "TESTING".getBytes();

            write(testMsg);

            cancel();

            synchronized (BTConnectionService.this) {
                mConnectThread = null;
            }

        }

        public void cancel() {

        }

        public void write(byte[] writeBytes) {
            try {
                mmOutputStream.write(writeBytes);
            } catch (IOException ioe) {
                Log.e(LOG_TAG, "failed to write to socked", ioe);
            }
        }


    }

    private class AcceptThread extends Thread {

    }

    void sendToastToUi(String toast){
        if(toast!=null & !toast.isEmpty()){
            Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
            Bundle bundle = new Bundle();
            bundle.putString(Constants.TOAST_STR, toast);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }

    }


}