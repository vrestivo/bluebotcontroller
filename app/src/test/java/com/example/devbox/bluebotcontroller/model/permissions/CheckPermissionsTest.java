package com.example.devbox.bluebotcontroller.model.permissions;


import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Process;

import com.example.devbox.bluebotcontroller.model.Bluetooth.BluetoothBroadcastReceiver;
import com.example.devbox.bluebotcontroller.model.Bluetooth.BluetoothConnection;
import com.example.devbox.bluebotcontroller.model.IModel;
import com.example.devbox.bluebotcontroller.model.Model;
import com.example.devbox.bluebotcontroller.presenter.MainPresenter;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;



@RunWith(PowerMockRunner.class)
@PrepareForTest({BluetoothAdapter.class, BluetoothConnection.class, MainPresenter.class, BluetoothBroadcastReceiver.class, IntentFilter.class, Process.class})
@SuppressStaticInitializationFor("com.example.devbox.bluebotcontroller.model.Model")
public class CheckPermissionsTest {


    private static IModel mModel;
    private static MainPresenter mMockMainPresenter;
    private static Context mMockContext;

    @BeforeClass
    public static void classSetup () throws Exception {
        PowerMockito.mock(BluetoothConnection.class);
        PowerMockito.mockStatic(BluetoothAdapter.class);
        PowerMockito.mockStatic(BluetoothBroadcastReceiver.class);

        IntentFilter mockIntentFilter = PowerMockito.mock(IntentFilter.class);
        PowerMockito.mock(IntentFilter.class);
        PowerMockito.whenNew(IntentFilter.class).withNoArguments().thenReturn(mockIntentFilter);
        PowerMockito.doNothing().when(mockIntentFilter).addAction(Matchers.anyString());

        mMockMainPresenter = PowerMockito.mock(MainPresenter.class);
        mMockContext = PowerMockito.mock(Context.class);
        mModel = Model.getInstance(mMockContext, mMockMainPresenter);
    }


    private void setContextToReturnPermission(int permissionCode) {
        PowerMockito.mockStatic(Process.class);
        PowerMockito.when(mMockContext.checkPermission(Matchers.anyString(), Matchers.anyInt(), Matchers.anyInt()))
                .thenReturn(permissionCode);
    }


    @Test
    public void permissionsGranted() {
        // given initialized model
        setContextToReturnPermission(PackageManager.PERMISSION_GRANTED);

        // permissions are checked and not available
        mModel.checkBluetoothPermissions();

        // bluetooth bluetoothFeatures are not disabled
        Mockito.verify(mMockMainPresenter, Mockito.never()).disableBluetoothFeatures();
    }

    @Test
    public void permissionDeniedTest() {
        // given initialized model
        setContextToReturnPermission(PackageManager.PERMISSION_DENIED);

        // permissions are checked and not available
        mModel.checkBluetoothPermissions();

        // bluetooth features are disabled
        Mockito.verify(mMockMainPresenter, Mockito.atLeastOnce()).disableBluetoothFeatures();
    }
}