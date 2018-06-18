package com.example.devbox.bluebotcontroller.model;


import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import com.example.devbox.bluebotcontroller.presenter.IMainPresenter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BluetoothAdapter.class})
@SuppressStaticInitializationFor("com.example.devbox.bluebotcontroller.model.Model")
public class BluetoothSupportTest {


    public IModel mMockModel;
    public Context mMockContext;
    public BluetoothConnection mBluetoothConnection;
    public IMainPresenter mMockMainPresenter;
    public BluetoothAdapter mMockBluetoothAdapter;


    @BeforeClass
    public static void classSetup(){

    }

    @AfterClass
    public static void classCleanup(){

    }

    @Before
    public void testSetup(){
        mMockContext = PowerMockito.mock(Context.class);
        mMockModel = PowerMockito.mock(Model.class);
    }

    @After
    public void testCleaup(){


    }

    @Test
    public void bluetoothAvailabilityTest(){
        //given initialized mMockModel

        //when bluetoothAdapter is not available
        PowerMockito.mockStatic(BluetoothAdapter.class);
        PowerMockito.when(BluetoothAdapter.getDefaultAdapter()).thenReturn(null);
        mBluetoothConnection = new BluetoothConnection(mMockModel, mMockContext);

        //bluetoothActions are disabled
        Mockito.verify(mMockModel, Mockito.atLeastOnce()).disableBluetoothFeatures();
    }


}
