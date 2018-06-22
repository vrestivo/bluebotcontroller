package com.example.devbox.bluebotcontroller.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.devbox.bluebotcontroller.presenter.IMainPresenter;

public class MainViewActivity extends AppCompatActivity implements IMainView {

    public static final int MY_PERMISSION_REQUEST = 777;


    private IMainPresenter mMainPresenter;
    private TextView mBlueToothStatus;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO set contentView
    }

    @Override
    public void startBluetoothDiscovery() {
        Intent discoveryIntent = new Intent(this, DiscoveryView.class);
        startActivity(discoveryIntent);
    }

    @Override
    public void disconnect() {
        //mMainPresenter.
    }

    @Override
    public void cleanup() {
        if(mMainPresenter!=null){
            mMainPresenter.cleanup();
        }
        mMainPresenter = null;
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void sendMessageToRemoteDevice(String message) {
        if(mMainPresenter!=null) {
            mMainPresenter.sendMessageToRemoteDevice(message);
        }
    }

    @Override
    public void showDeviceStatus(String status) {
        if(mBlueToothStatus!=null){
            mBlueToothStatus.setText(status);
        }
    }

    public boolean checkBluetoothPermissions(){
        //Needed to turn Turn On/Off Bluetooth
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_DENIED){
            return false;
        }
        if ( ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_DENIED) {
            return false;
        }

        //Needed for scanning
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED){
            return false;
        }
        if ( ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            return false;
        }


        return true;
    }

    @Override
    public void requestBluetoothPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN
                }, MY_PERMISSION_REQUEST
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //TODO exit if permissions were not granted
        if(grantResults.length > 0){
            for (int result: grantResults){
                if(result == PackageManager.PERMISSION_DENIED){
                    notifyUserAndExitTheApp();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void notifyUserAndExitTheApp() {
        //TODO Implement;
    }


    @Override
    public void disableBluetoothFeatures() {
        // TODO disable bluetooth related buttons
        // TODO display message to user notifying
        // that the bluetooth is not supported
    }


    @Override
    public void onBluetoothOff() {
        //TODO implement
        //TODO disable discovery and send buttons
    }


    


}
