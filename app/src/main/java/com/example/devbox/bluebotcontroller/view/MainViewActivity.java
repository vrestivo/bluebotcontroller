package com.example.devbox.bluebotcontroller.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.devbox.bluebotcontroller.R;
import com.example.devbox.bluebotcontroller.presenter.IMainPresenter;

public class MainViewActivity extends AppCompatActivity implements IMainView {

    private final String BT_DISABLE = "BT OFF";
    private final String BT_ENABLE = "BT ON";

    private IMainPresenter mMainPresenter;
    private Button mBluetoothOnOffButton;
    private Button mStartDiscoveryButton;
    private Button mSendButton;
    private Button mDisconnectButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);
        initializeUIElements();
    }

    private void initializeUIElements(){
        mBluetoothOnOffButton = findViewById(R.id.bt_on);
        mBluetoothOnOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO implement
            }
        });

        mStartDiscoveryButton = findViewById(R.id.bt_discover);
        mStartDiscoveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBluetoothDiscovery();
            }
        });

        mDisconnectButton = findViewById(R.id.bt_disconnect);

        mSendButton = findViewById(R.id.bt_send);
    }


    @Override
    public void checkBluetoothPermissions() {
        if(mMainPresenter!=null){
            mMainPresenter.checkBluetoothPermissions();
        }
    }


    @Override
    public void startBluetoothDiscovery() {
        Intent discoveryActivityIntent =  new Intent(this, DiscoveryViewActivity.class);
        startActivity(discoveryActivityIntent);
    }


    @Override
    public void disconnect() {
        if(mMainPresenter!=null){
            mMainPresenter.disconnect();
        }
    }


    @Override
    public void cleanup() {
        //TODO implement
    }


    @Override
    public void showMessage(String message) {

    }

    @Override
    public void sendMessageToRemoteDevice(String message) {
        if(mMainPresenter != null && message != null){
            mMainPresenter.sendMessageToRemoteDevice(message);
        }
    }

    @Override
    public void enableBluetooth() {
        if(mMainPresenter!=null){
            mMainPresenter.enableBluetooth();
        }
    }

    @Override
    public void disableBluetooth() {
        if(mMainPresenter!=null){
            mMainPresenter.disableBluetooth();
        }
    }

    @Override
    public void onBluetoothOn() {
        bluetoothOnUI();
    }

    private void bluetoothOnUI(){
        mStartDiscoveryButton.setEnabled(true);
        mDisconnectButton.setEnabled(true);
        mSendButton.setEnabled(true);

    }

    @Override
    public void onBluetoothOff() {
        bluetoothOffUI();
    }

    private void bluetoothOffUI(){
        mStartDiscoveryButton.setEnabled(false);
        mDisconnectButton.setEnabled(false);
        mSendButton.setEnabled(false);
    }


    @Override
    public void enableBluetoothFeatures() {
        // TODO enable bluetooth on/off button
        // TODO enable the rest of the buttons
    }


    /**
     * called when Bluetooth permissions are denies
     * and when bluetooth is not supported
     */
    @Override
    public void disableBluetoothFeatures() {
        // TODO disable on/off button
        // TODO disable the rest of the buttons
    }


    private boolean bluetoothEnabled(){
        if(mMainPresenter!=null){
            return mMainPresenter.isBluetoothEnabled();
        }
        return false;
    }

    @Override
    public void showDeviceStatus(String status) {

    }

    @Override
    public void requestBluetoothPermissions() {
        //TODO implement
    }

}
