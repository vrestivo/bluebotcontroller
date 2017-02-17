package com.example.devbox.bluebotcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static com.example.devbox.bluebotcontroller.Constants.MESSAGE_CON_STATE_CHANGE;
import static com.example.devbox.bluebotcontroller.Constants.ST_CONNECTED;
import static com.example.devbox.bluebotcontroller.Constants.ST_CONNECTING;
import static com.example.devbox.bluebotcontroller.Constants.ST_DISCONNECTED;
import static com.example.devbox.bluebotcontroller.Constants.ST_ERROR;
import static com.example.devbox.bluebotcontroller.Constants.ST_NONE;

/**
 * this class sets up and facilitates communication
 * with a remote Bluetooth device
 */

public class BTConnectionService {

    private final String LOG_TAG = "BTConnectionService";

    //NOTE this have to use this the following UUID IOT connect to boards
    //see the link below
    //https://developer.android.com/reference/android/bluetooth/BluetoothDevice.html#createRfcommSocketToServiceRecord%28java.util.UUID%29
    private final UUID APP_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //                           ^^^^
    // UUID section marked by a "^" points to the device type, in this case
    // a serial device


    private ConnectThread mConnectThread;
    private Handler mHandler;
    private int mState;
    private boolean mIsConnected;
    private BluetoothAdapter mBtAdapter;
    private Context mParentContext;

    public BTConnectionService(Context context, Handler handler) {
        mHandler = handler;
        mParentContext = context;
        mState = ST_NONE;
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    public synchronized void connect(BluetoothDevice device, boolean secure) {
        if (mConnectThread == null) {
            mConnectThread = new ConnectThread(device, mHandler);
        } else {
            mConnectThread.cancel();
        }
        mConnectThread.start();
    }


    /**
     * @return returns connection state
     */
    public int getState() {
        return mState;
    }

    /**
     * this method send a toast message to the UI thread
     *
     * @param toast
     */
    private void sendToastToUi(String toast) {
        if (toast != null & !toast.isEmpty()) {
            Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
            Bundle bundle = new Bundle();
            bundle.putString(Constants.TOAST_STR, toast);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }

    }

    /**
     * send a message to a remote bluetooth device
     *
     * @param message message in string form;
     */
    public void sendToRemoteBt(String message) {
        ConnectThread thread;

        synchronized (this) {
            if (mState != ST_CONNECTED) {
                return;
            }
            thread = mConnectThread;
        }

        if (message != null & !message.isEmpty()) {
            //TODO delete logging
            Log.v(LOG_TAG, "sending message");
            thread.write(message.getBytes());
        }
    }


    /**
     * this method send message to UI handler
     */
    private void handleToUI(int msgWhat, @Nullable BluetoothDevice device) {
        switch (msgWhat) {
            case MESSAGE_CON_STATE_CHANGE: {
                if (mState == ST_CONNECTED && device != null) {
                    String devinfo = device.getName() + " " + device.getAddress();
                    Message msg = mHandler.obtainMessage(msgWhat, mState, -1);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.DEV_INFO_STR, devinfo);
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
                else
                {
                    Message msg = mHandler.obtainMessage(msgWhat, mState, -1);
                    mHandler.sendMessage(msg);
                }
                break;
            }
        }

    }


    /**
     * since we are connecting only to one device this thread
     * will manage connection and communication with the
     * remote bluetooth device
     */
    private class ConnectThread extends Thread {

        private final BluetoothDevice mmBtDevice;
        private final BluetoothSocket mmSocket;
        private InputStream mmInputStream;
        private OutputStream mmOutputStream;
        private BluetoothAdapter mmAdapter = BluetoothAdapter.getDefaultAdapter();


        private byte[] mmATComand = "AT".getBytes();
        private String mmOkResponse = "OK";

        byte[] mmInArray = new byte[1024];
        byte[] mmOutArray;
        int mmBytesAvailable;
        int mmBytesReceived;
        Handler mmHancler;
        Message mmMessage;


        public ConnectThread(BluetoothDevice device, Handler handler) {

            mmBtDevice = device;
            mmInputStream = null;
            mmOutputStream = null;
            mmBytesReceived = 0;
            mmHancler = handler;


            BluetoothSocket tmp = null;

            try {
                tmp = device.createRfcommSocketToServiceRecord(APP_UUID);
                //tmp = device.createInsecureRfcommSocketToServiceRecord(APP_UUID);
                mState = ST_CONNECTING;
            } catch (IOException ioe) {
                String errorMessage = mParentContext.getString(R.string.bt_conn_error_failed_rfcomm);
                Log.e(LOG_TAG, errorMessage, ioe);
                sendToastToUi(errorMessage);

            }

            mmSocket = tmp;

            //making sure discovery is disabled
            mBtAdapter.cancelDiscovery();

            //TODO handle error for already connected socket
            try {
                mmSocket.connect();
                mState = ST_CONNECTED;
            } catch (IOException ioe1) {
                try {
                    mmSocket.close();
                    mState = ST_ERROR;
                } catch (IOException ioe2) {
                    Log.e(LOG_TAG, "closing socket failed", ioe2);
                    mState = ST_ERROR;
                }
                Log.e(LOG_TAG, "failed to connect", ioe1);
                sendToastToUi("failed to connect");
            }


            mmInputStream = switchInputStream(mmInputStream, mmSocket, true);

            //TODO refactor to use swithOutputStream
            //open output stream
            try {
                mmOutputStream = mmSocket.getOutputStream();
                mState = ST_CONNECTED;
                handleToUI(MESSAGE_CON_STATE_CHANGE, mmBtDevice);
            } catch (IOException ioe) {
                Log.v(LOG_TAG, "starting error getting InputStream");
                mState = ST_ERROR;
            }


        }


        @Override
        public void run() {
            String inputStreamError = "starting error reading InputStream";
            //TODO delete logging

            Log.v(LOG_TAG, "starting ConntectThread");

            if (isConnected()) {
                sendToastToUi("looks like we're talking");
            }

            //TODO add reconnection attempts
            while (mState == ST_CONNECTED) {
                try {
                    mmBytesReceived = mmInputStream.read(mmInArray);
                    //TODO pass message to ui
                    //mHandler.obtainMessage().sendToTarget(Constants.MESSAG);
                    sendToastToUi(new String(mmInArray));
                } catch (IOException ioe) {
                    Log.v(LOG_TAG, "starting error reading InputStream");
                    mState = ST_ERROR;
                    break;
                }
            }

            cancel();

            synchronized (BTConnectionService.this) {
                mConnectThread = null;
            }
        }

        /**
         * this smehod terminates the connectionn by
         * closing input and output streams,
         * closing bluettooth socket,
         * and upates the conection status
         */
        public void cancel() {

            if (mmInputStream != null) {
                try {
                    mmInputStream.close();
                } catch (IOException ioe) {
                    Log.e(LOG_TAG, "failed to close IputStream", ioe);
                    mState = ST_ERROR;
                }
            }

            mmOutputStream = switchOutputStream(mmOutputStream, mmSocket, false);
            mmInputStream = switchInputStream(mmInputStream, mmSocket, false);

            //close socket
            try {
                mmSocket.close();
            } catch (IOException ioe2) {
                Log.e(LOG_TAG, "closing socket failed", ioe2);
            }

            mState = ST_DISCONNECTED;

        }

        public void write(byte[] writeBytes) {
            try {
                mmOutputStream.write(writeBytes);
                Log.v(LOG_TAG, "data sent");
            } catch (IOException ioe) {
                Log.e(LOG_TAG, "failed to write to socked", ioe);
                mState = ST_ERROR;
            }
        }


        /**
         * this method opens or closes InputStream
         *
         * @param stream stream to open or close
         * @param socket socket to get the stream from
         * @param open   action true for open, false for close
         */
        public InputStream switchInputStream(InputStream stream, BluetoothSocket socket, boolean open) {
            if (socket != null) {
                try {
                    if (open && socket.isConnected()) {
                        return socket.getInputStream();
                    } else if (!open && stream != null) {
                        stream.close();
                    }
                } catch (IOException ioe) {
                    if (open) {
                        Log.e(LOG_TAG, "failed to get InputStream.", ioe);
                        mState = ST_ERROR;
                    } else {
                        Log.e(LOG_TAG, "failed to close InputStream.", ioe);
                        mState = ST_ERROR;
                    }
                }
            }
            return null;
        }


        /**
         * this method opens or closes OutputStream
         *
         * @param stream stream to open or close
         * @param socket socket to get the stream from
         * @param open   action true for open, false for close
         */
        public OutputStream switchOutputStream(OutputStream stream, BluetoothSocket socket, boolean open) {
            if (socket != null) {
                try {
                    if (open && socket.isConnected()) {
                        return socket.getOutputStream();
                    } else if (!open && stream != null) {
                        stream.close();
                    }
                } catch (IOException ioe) {
                    if (open) {
                        Log.e(LOG_TAG, "failed to get InputStream.", ioe);
                        mState = ST_ERROR;
                    } else {
                        Log.e(LOG_TAG, "failed to close InputStream", ioe);
                        mState = ST_ERROR;
                    }
                }

            }
            return null;

        }


        /**
         * this method validates connection with a remote bluetooth device
         * it sends an "AT" commamd to a remote device
         * if remote device responsd with "OK"
         * (typical behavior for a seiral bluetoodh module)
         * then connection works as expected
         *
         * @return
         */
        public boolean isConnected() {
            String LOG_TAG = "_isConnected";
            byte[] response = null;

            //TODO test this
            boolean connected = false;
            if (mmSocket.isConnected()) {
                if (mmInputStream != null && mmOutputStream != null) {
                    try {
                        mmOutputStream.write(mmATComand);
                    } catch (IOException ioe) {
                        Log.e(LOG_TAG, "failed to write to OutputStream", ioe);
                    }
                    try {
                        if (mmInputStream.available() > 0) {
                            mmInputStream.read(response);
                            if (new String(response).equals(mmOkResponse)) {
                                connected = true;
                            }
                        }
                    } catch (IOException ioe) {
                        Log.e(LOG_TAG, "failed to read from InputStream", ioe);

                    }
                }

            }
            return connected;
        }

    }


}