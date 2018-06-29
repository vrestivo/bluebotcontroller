package com.example.devbox.bluebotcontroller.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.devbox.bluebotcontroller.R;
import com.example.devbox.bluebotcontroller.presenter.IMainPresenter;
import com.example.devbox.bluebotcontroller.presenter.MainPresenter;

public class MainViewActivity extends AppCompatActivity implements IMainView {

    public static final int BT_PERM_REQ_CODE = 87;

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
                toggleBluetooth();
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
        mDisconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMainPresenter!=null){
                    mMainPresenter.disconnect();
                }
            }
        });

        mSendButton = findViewById(R.id.bt_send);
    }


    @Override
    protected void onStart() {
        super.onStart();
        initializeMainPresenter();
        initializeBluetoothFeatures();
    }


    private void initializeBluetoothFeatures(){
        if(mMainPresenter!=null){
            if(mMainPresenter.isBluetoothSupported()){
                // check permissions
                if(mMainPresenter.bluetoothPermissionsGranted()){
                    enableBluetoothFeatures();
                }
                else {
                    disableBluetoothFeatures();
                    requestBluetoothPermissions();
                }
            }
            disableBluetoothFeatures();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
        cleanup();
    }

    @Override
    public void cleanup() {
        presenterLifecycleCleanup();
        //TODO clean up joystick thread
    }



    private void initializeMainPresenter(){
        if(mMainPresenter==null){
            mMainPresenter = new MainPresenter(this, getApplicationContext());
        }
    }

    private void presenterLifecycleCleanup(){
        if(mMainPresenter!=null){
            mMainPresenter.cleanup();
            mMainPresenter = null;
        }
    }


    private void toggleBluetooth(){
        //TODO implement
        if(mMainPresenter!=null){
            if(mMainPresenter.isBluetoothEnabled()){
                mMainPresenter.disableBluetooth();
                mBluetoothOnOffButton.setText(R.string.button_bt_on);
            }
            else {
                mMainPresenter.enableBluetooth();
                mBluetoothOnOffButton.setText(R.string.button_bt_off);
            }
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
    public void showMessage(String message) {
        if(message!=null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT);
        }
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
        mBluetoothOnOffButton.setText(R.string.button_bt_on);
        mStartDiscoveryButton.setEnabled(false);
        mDisconnectButton.setEnabled(false);
        mSendButton.setEnabled(false);
    }


    @Override
    public void enableBluetoothFeatures() {
        mBluetoothOnOffButton.setEnabled(true);
        if(mMainPresenter!=null) {
            if(mMainPresenter.isBluetoothEnabled()){
                bluetoothOnUI();
            }
            else {
                bluetoothOffUI();
            }
        }
    }


    @Override
    public void disableBluetoothFeatures() {
        mBluetoothOnOffButton.setEnabled(false);
        bluetoothOffUI();
    }


    @Override
    public void showDeviceStatus(String status) {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //TODO check if all permissions are granted
        //TODO if granted bluetooth features
        //TODO else disable bluetooth features
    }


    @Override
    public void requestBluetoothPermissions() {
        //TODO request permissions
    }

}
