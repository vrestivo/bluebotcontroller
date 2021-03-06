package com.example.devbox.bluebotcontroller.model.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.example.devbox.bluebotcontroller.model.IModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import static com.example.devbox.bluebotcontroller.Constants.*;

public class BluetoothConnection implements IBluetoothConnection {
    private BluetoothSocket mBluetoothSocket;
    private BluetoothAdapter mBluetoothAdapter;
    private IModel mModel;
    private Context mApplicationContext;
    private InputStream mBluetoothSocketInputStream;
    private OutputStream mBluetoothSocketOutputStream;
    private PublishSubject<String> mInputStreamPublishSubject;
    private PublishSubject<String> mOutputStreamPublishSubject;
    private Disposable mOutputStreamDisposable;
    private Disposable mInputStreamDisposable;
    private BluetoothBroadcastReceiver mBluetoothBroadcastReceiver;
    private Set<BluetoothDevice> mDiscoveredDevices;
    private Set<BluetoothDevice> mPairedDevices;

    private int mConnectionStateCode = BluetoothAdapter.STATE_DISCONNECTED;
    private byte[] mInputByteArray = new byte[1024];


    public BluetoothConnection(IModel model, Context context) {
        mModel = model;
        mApplicationContext = context;
        mDiscoveredDevices = new HashSet<>();
        mPairedDevices = new HashSet<>();
        initializeAdapter();
        mInputStreamPublishSubject = PublishSubject.create();
        mOutputStreamPublishSubject = PublishSubject.create();
        initializedBroadcastReceiver();
    }

    private void initializeAdapter() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            //TODO refactor
            handleBluetoothNotSupported();
        } else {
            mPairedDevices = mBluetoothAdapter.getBondedDevices();
        }
    }

    private void handleBluetoothNotSupported() {
        if (mModel != null) {
            mModel.disableBluetoothFeatures();
            mModel.updateDeviceStatus(STATUS_NOT_SUPPORTED);
            mModel.notifyMainPresenter(MSG_BT_NOT_SUPPORTED);
        }
    }


    private void initializedBroadcastReceiver() {
        mBluetoothBroadcastReceiver = new BluetoothBroadcastReceiver(this);
        mApplicationContext.registerReceiver(mBluetoothBroadcastReceiver, mBluetoothBroadcastReceiver.generateIntentFilters());
    }

    @Override
    public boolean isBluetoothSupported() {
        if (mBluetoothAdapter != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isBluetoothEnabled() {
        if (mBluetoothAdapter == null) {
            //TODO refactor
            handleBluetoothNotSupported();
            return false;
        }
        return mBluetoothAdapter.isEnabled();
    }

    //For lifecycle purposes
    @Override
    public void getKnownDevices() {
        if (mModel != null) {
            if (mPairedDevices != null) mModel.loadPairedDevices(mPairedDevices);
            if (mDiscoveredDevices != null) mModel.loadAvailableDevices(mDiscoveredDevices);
        }
    }

    @Override
    public Set<BluetoothDevice> getBondedDevices() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            mPairedDevices = mBluetoothAdapter.getBondedDevices();
        }
        return mPairedDevices;
    }

    @Override
    public void scanForDevices() {
        if(isConnected()) disconnect();
        mModel.loadPairedDevices(getBondedDevices());
        startDiscovery();
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
    public void onDeviceFound(BluetoothDevice device) {
        if (device != null) {
            mDiscoveredDevices.add(device);
            if (mModel != null) {
                mModel.loadAvailableDevices(mDiscoveredDevices);
            }
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
    public void updateConnectionStatus(int newStatusCode) {
        mConnectionStateCode = newStatusCode;
        switch (mConnectionStateCode) {
            case BluetoothAdapter.STATE_CONNECTED: {
                updateConnectionStatusIndicator(STATUS_CONNECTED);
                break;
            }
            case BluetoothAdapter.STATE_DISCONNECTED: {
                updateConnectionStatusIndicator(STATUS_DISCONNECTED);
                break;
            }
        }
    }

    @Override
    public void updateConnectionStatusIndicator(String status) {
        if (mModel != null && status != null) {
            mModel.updateDeviceStatus(status);
        }
    }

    @Override
    public void connectToRemoteDevice(BluetoothDevice remoteDevice) {
        if (remoteDevice != null) {
            if (isConnected()) disconnect();
            if (mBluetoothAdapter.isDiscovering()) mBluetoothAdapter.cancelDiscovery();
            connectReactively(remoteDevice);
        }
    }

    private void connectReactively(BluetoothDevice remoteDevice) {
        Observable.fromCallable(() -> initializeBluetoothConnection(remoteDevice))
                .observeOn(Schedulers.single())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        success -> {
                            if(success){ updateConnectionStatusIndicator(STATUS_CONNECTED);}
                            //else {updateConnectionStatusIndicator(STATUS_DISCONNECTED);}
                        },
                        error -> {updateConnectionStatusIndicator(STATUS_ERROR);}
                );
    }

    private synchronized boolean initializeBluetoothConnection(BluetoothDevice remoteDevice) throws IOException {
        BluetoothSocket tempSocket = remoteDevice.createRfcommSocketToServiceRecord(SPP_UUID);
        if(tempSocket!=null){
            mBluetoothSocket = tempSocket;
            if(!mBluetoothSocket.isConnected()) mBluetoothSocket.connect();
            setupInputOutputStreams();
            return true;
        }
        return false;
    }

    private void setupInputOutputStreams() throws IOException {
        cleanUpStreams();
        openStreams();
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
            if (mBluetoothSocketInputStream != null) mBluetoothSocketInputStream.close();
            if (mBluetoothSocketOutputStream != null) mBluetoothSocketOutputStream.close();
        } catch (IOException ioException) {
        }
    }

    private void openStreams() throws IOException {
        if (mBluetoothSocket != null && mBluetoothSocket.isConnected()) {
            mBluetoothSocketInputStream = mBluetoothSocket.getInputStream();
            mBluetoothSocketOutputStream = mBluetoothSocket.getOutputStream();
        }
    }

    private void subscribeToInputStream() {
        Observable<String> inputObservable = Observable.create(
                emitter -> {
                    while (mBluetoothSocket.isConnected() && mBluetoothSocketInputStream.available()>0) {
                        try {
                            mBluetoothSocketInputStream.read(mInputByteArray);
                            emitter.onNext(mInputByteArray.toString());
                        } catch (Throwable throwable) {
                            emitter.onError(throwable);
                        }
                    }
                    emitter.onComplete();
                });

        /**
         * Subscribing on newThread due to constant read operation.
         * An infinite loop condition may occur if using Shedulers.io()
         * due to a possibility of being allocated a single thread
         * for running read and write operations
         **/
        if (mBluetoothSocket.isConnected()) {
            mInputStreamDisposable = inputObservable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .toFlowable(BackpressureStrategy.LATEST) //TODO verify
                    .subscribe(bluetoothInputString -> {
                                if (bluetoothInputString != null) {
                                    notifyMainPresenter(bluetoothInputString);
                                }
                            },
                            error -> {
                                disconnect();
                            }
                    );
        } else {
            cleanUpStreams();
        }
    }

    private void subscribeToOutputStream() {
        if (mBluetoothSocket.isConnected()) {
            mOutputStreamDisposable = mOutputStreamPublishSubject
                    .observeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            message -> {
                                if (message != null) {
                                    try {
                                        mBluetoothSocketOutputStream.write(message.getBytes());
                                    } catch (Throwable throwable) {
                                        //Errors are handled downstream
                                    }
                                }
                            },
                            error -> {
                                notifyMainPresenter(STATUS_ERROR);
                                updateConnectionStatusIndicator(STATUS_ERROR);
                                disconnect();
                            });
        } else {
            cleanUpStreams();
        }
    }

    @Override
    public void sendMessageToRemoteDevice(String message) {
        if (isConnected() && message != null) {
            mOutputStreamPublishSubject.onNext(message);
        }
    }

    @Override
    public void enableBluetooth() {
        if (mBluetoothAdapter != null) {
            if (!isBluetoothEnabled()) {
                mBluetoothAdapter.enable();
            }
        }
    }

    @Override
    public void disableBluetooth() {
        if (mBluetoothAdapter != null) {
            if (isBluetoothEnabled()) {
                mBluetoothAdapter.disable();
            }
        }
    }

    @Override
    public void onBluetoothOn() {
        if (mModel != null) {
            mModel.onBluetoothOn();
        }
    }

    @Override
    public void onBluetoothOff() {
        if (mModel != null) {
            mModel.onBluetoothOff();
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
        updateConnectionStatusIndicator(STATUS_DISCONNECTED);
    }

    @Override
    public boolean isConnected() {
        if (mBluetoothSocket != null) {
            return mBluetoothSocket.isConnected();
        } else {
            return false;
        }
    }

    @Override
    public void unregisterReceiver() {
        if (mBluetoothBroadcastReceiver != null) {
            mApplicationContext.unregisterReceiver(mBluetoothBroadcastReceiver);
        }
    }
}
