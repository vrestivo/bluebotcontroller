package com.example.devbox.bluebotcontroller.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.support.annotation.VisibleForTesting;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class BluetoothConnection implements IBluetoothConnection {


    //NOTE this is SPP (Serial Port Profile) UUID
    //see the link below
    //https://developer.android.com/reference/android/bluetooth/BluetoothDevice.html#createRfcommSocketToServiceRecord%28java.util.UUID%29
    public static final UUID SPP_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //                           ^^^^
    // UUID section marked by a "^" points to the device type, in this case
    // a serial device


    private BluetoothSocket mBluetoothSocket;
    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> mBondedDevices;
    private IModel mModel;
    private Context mApplicationContext;

    private InputStream mBluetoothSocketInputStream;
    private OutputStream mBluetoothSocketOutputStream;

    private PublishSubject<String> mInputStreamPublishSubject;
    private PublishSubject<String> mOutputStreamPublisheSubject;
    private Disposable mOutputStreamDisposable;
    private Disposable mInputStreamDisposable;


    public BluetoothConnection(IModel model, Context context) {
        mModel = model;
        mApplicationContext = context;
        initializeAdapter();
        mInputStreamPublishSubject = PublishSubject.create();
        mOutputStreamPublisheSubject = PublishSubject.create();
    }


    private void initializeAdapter() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public Set<BluetoothDevice> getBondedDevices() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            mBondedDevices = mBluetoothAdapter.getBondedDevices();
        }
        return mBondedDevices;
    }

    @Override
    public void startDiscovery() {
        if (mBluetoothAdapter != null) {
            if (isConnected()) {
                disconnect();
            }
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
            mBluetoothAdapter.startDiscovery();
        }
    }

    @Override
    public void stopDiscovery() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    @Override
    public void notifyMainPresenter(String message) {
        if (mModel != null && message != null) {
            mModel.notifyMainPresenter(message);
        }
    }

    @Override
    public void notifyDiscoveryPresenter(String message) {
        if (mModel != null && message != null) {
            mModel.notifyDiscoveryPresenter(message);
        }
    }

    @Override
    public void updateDeviceStatus(String status) {
        //TODO Implement
        if (mModel != null && status != null) {
            mModel.updateDeviceStatus(status);
        }
    }

    @Override
    public void connectToRemoteDevice(BluetoothDevice remoteDevice) {
        if (remoteDevice != null) {
            try {
                if (isConnected()) {
                    disconnect();
                }
                mBluetoothSocket = remoteDevice.createRfcommSocketToServiceRecord(SPP_UUID);
                setupInputOutputStreams();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void setupInputOutputStreams() {
        //TODO implement
        //TODO check if subscribed, if so
        cleanUpStreams();
        //TODO initialize streams
        openStreams();
        //TODO initialize and PublishSubjects and subscribe to them
        subscribeToInputStream();
        subscribeToOutputStream();

    }

    private void cleanUpStreams() {
        if (mOutputStreamDisposable != null && !mOutputStreamDisposable.isDisposed()) {
            mOutputStreamDisposable.dispose();
        }
        if (mInputStreamDisposable != null && !mInputStreamDisposable.isDisposed()) {
            mInputStreamDisposable.dispose();
        }

        try {
            if (mBluetoothSocketInputStream != null) {
                mBluetoothSocketInputStream.close();
            }

            if (mBluetoothSocketOutputStream != null) {
                mBluetoothSocketOutputStream.close();
            }
        } catch (IOException ioException) {
            //TODO handle the excetion
        }
    }


    private void openStreams() {
        if (mBluetoothSocket != null && mBluetoothSocket.isConnected()) {
            try {
                mBluetoothSocketInputStream = mBluetoothSocket.getInputStream();
                mBluetoothSocketOutputStream = mBluetoothSocket.getOutputStream();
            } catch (IOException ioException) {
                //TODO handle  the exception
            }
        }
    }

    private void subscribeToInputStream() {
        if (mBluetoothSocket.isConnected()) {
            mInputStreamDisposable = mInputStreamPublishSubject
                    .subscribeOn(Schedulers.newThread())  //send data on a separate thread
                    .subscribe(
                            message -> {
                                //TODO implement reading


                            }
                    );
        } else {
            disconnect();
        }
    }

    private void subscribeToOutputStream() {
        if (mBluetoothSocket.isConnected()) {
            mOutputStreamDisposable = mOutputStreamPublisheSubject
                    .observeOn(Schedulers.newThread())
                    .subscribeOn(AndroidSchedulers.mainThread()) //FIXME causes initializatin error during unit tests
                    .subscribe(
                            message -> {
                                //TODO implement writing
                            }
                    );
        } else {
            disconnect();
        }
    }

    @Override
    public void sendMessageToRemoteDevice(String message) {
        if (isConnected() && message != null) {
            //TODO implement reactively

        }

    }

    @Override
    public void disconnect() {
        cleanUpStreams();
        if (mBluetoothSocket != null) {
            try {
                mBluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isConnected() {
        if (mBluetoothSocket != null) {
            return mBluetoothSocket.isConnected();
        } else {
            return false;
        }
    }

    private void closeStreams() {
        //TODO implement
        //TODO check if PublishSubjects are subscribed,
        //TODO dispose/unsubscribe from PublisSubjects
        //TODO close streams

    }

    private void cleanup() {
        //TODO clean up resources
    }


}
