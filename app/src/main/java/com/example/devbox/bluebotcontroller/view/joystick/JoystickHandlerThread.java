package com.example.devbox.bluebotcontroller.view.joystick;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import model.BluetoothThread;

/**
 * Created by devbox on 12/9/17.
 */

public class JoystickHandlerThread extends HandlerThread {

    public static String NAME = "JoystickHandlerThread_name";
    public static int SEND_MSG = 999;
    public int SEND_DELAY = 15;
    private Handler mHandler;
    private boolean mKeepSending;
    private BluetoothThread mBluetoothThread;
    private String LOG_TAG = getClass().getSimpleName();
    private String mDataToSend;
    private boolean mSending;

    public JoystickHandlerThread(String name) {
        super(name);
        mKeepSending = false;
        mSending = false;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mHandler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                mDataToSend = (String) msg.obj;
                if(!isSending()) {
                    sendData();
                }
            }
        };
    }


    private void sendData() {
        mSending = true;
        while (mKeepSending && mBluetoothThread != null && mBluetoothThread.isConnected()) {
            mBluetoothThread.sendToRemoteBt(mDataToSend);

            //without the delay the serial buffer gets clogged
            try {
                sleep(SEND_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mSending = false;
        Log.v(LOG_TAG, "_in handleMessage() sending: " + mDataToSend + "DONE!");
    }


    public void setDataToSend(String data){
        mDataToSend = data;
    }


    public void setKeepSending(boolean flag) {
        mKeepSending = flag;
    }


    public boolean isSending(){
        return mSending;
    }


    public void setBluetoothThread(BluetoothThread bluetoothThread) {
        mBluetoothThread = bluetoothThread;
    }


    public Handler getHandler() {
        return mHandler;
    }

    @Override
    public boolean quit() {
        setKeepSending(false);
        mBluetoothThread = null;
        return super.quit();
    }

}
