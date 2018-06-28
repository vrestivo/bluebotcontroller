package com.example.devbox.bluebotcontroller.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.devbox.bluebotcontroller.R;
import com.example.devbox.bluebotcontroller.presenter.MainPresenter;

public class MainViewActivity extends AppCompatActivity implements IMainView {

    private Button mStartDiscoveryButton;
    private MainPresenter mMainPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);
        initializeUIElements();
    }

    private void initializeUIElements(){
        mStartDiscoveryButton = findViewById(R.id.bt_discover);
        mStartDiscoveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBluetoothDiscovery();
            }
        });
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
        //TODO implement
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

    }

    @Override
    public void onBluetoothOn() {

    }

    @Override
    public void onBluetoothOff() {

    }

    @Override
    public void disableBluetoothFeatures() {

    }

    @Override
    public void showDeviceStatus(String status) {

    }

    @Override
    public void requestBluetoothPermissions() {
        //TODO implement
    }


}
