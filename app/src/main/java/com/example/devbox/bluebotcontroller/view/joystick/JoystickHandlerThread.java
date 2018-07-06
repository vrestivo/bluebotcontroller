package com.example.devbox.bluebotcontroller.view.joystick;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.example.devbox.bluebotcontroller.model.BluetoothThread;
import com.example.devbox.bluebotcontroller.view.IMainView;

/**
 * Class to handle continuous press input
 * from the JoystickView.
 */

public class JoystickHandlerThread extends HandlerThread {

    public static String NAME = "JoystickHandlerThread_name";
    public static int SEND_MSG = 999;
    public static final int SEND_DELAY = 20;
    private Handler mHandler;
    private IMainView mMainViewActivity;
    private boolean mKeepSending;
    private String LOG_TAG = getClass().getSimpleName();
    private String mDataToSend;
    private boolean mSending;

    public JoystickHandlerThread(String name, IMainView mainView) {
        super(name);
        mMainViewActivity = mainView;
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
        while (mKeepSending && mMainViewActivity != null) {
            mMainViewActivity.sendMessageToRemoteDevice(mDataToSend);

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

    public Handler getHandler() {
        return mHandler;
    }

    @Override
    public boolean quit() {
        setKeepSending(false);
        mMainViewActivity = null;
        return super.quit();
    }

}
