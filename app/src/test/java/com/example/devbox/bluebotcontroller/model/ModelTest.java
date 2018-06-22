package com.example.devbox.bluebotcontroller.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;

import com.example.devbox.bluebotcontroller.presenter.IDiscoveryPresenter;
import com.example.devbox.bluebotcontroller.presenter.IMainPresenter;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("com.example.devbox.bluebotcontroller.model.Model")
@PrepareForTest({Model.class, BluetoothConnection.class, BluetoothAdapter.class, BluetoothBroadcastReceiver.class})
public class ModelTest {

    private final String DEVICE_STATUS = "TESTING";
    private final String TEST_MESSAGE = "TEST MESAGE";


    private IModel mClassUnderTest;
    private Context mMockContext;
    private IMainPresenter mMockMainPresenter;
    private IDiscoveryPresenter mMockDiscoveryPresenter;
    public BluetoothConnection mMockBluetoothConnection;


    @Before
    public void testSetup(){
        mMockContext = PowerMockito.mock(Context.class);
        mMockMainPresenter = PowerMockito.mock(MockMainPresenter.class);
        mMockDiscoveryPresenter = PowerMockito.mock(MockDiscoveryPresenter.class);
        mMockBluetoothConnection = PowerMockito.mock(BluetoothConnection.class);

        // prevents method not mocket errors
        PowerMockito.mock(BluetoothBroadcastReceiver.class);
        IntentFilter mockIntentFilter = PowerMockito.mock(IntentFilter.class);
        try {
            PowerMockito.whenNew(IntentFilter.class).withNoArguments().thenReturn(mockIntentFilter);
        }
        catch (Exception e){
            e.printStackTrace();
        }


        // assist with model initialization
        BluetoothAdapter mockAdapter = PowerMockito.mock(BluetoothAdapter.class);
        PowerMockito.mockStatic(BluetoothAdapter.class);
        Mockito.when(BluetoothAdapter.getDefaultAdapter()).thenReturn(mockAdapter);

        mClassUnderTest = Model.getInstance(mMockContext, mMockMainPresenter);
        mClassUnderTest = Model.getInstance(mMockContext, mMockDiscoveryPresenter);

        //set static Model.sBluetoothConnection member
        Whitebox.setInternalState(Model.class, mMockBluetoothConnection);
    }

    @After
    public void testCleanup(){

    }


    @Test
    public void startDiscoveryWhileNotConnected(){
        //Given initialized model
        PowerMockito.when(mMockBluetoothConnection.isConnected()).thenReturn(false);

        //when starting discovery while connected
        mClassUnderTest.startDiscovery();

        //the connection is checked then call is propagated
        InOrder inOrder = inOrder(mMockBluetoothConnection);
        inOrder.verify(mMockBluetoothConnection, Mockito.atLeastOnce()).isConnected();
        inOrder.verify(mMockBluetoothConnection, Mockito.atLeastOnce()).startDiscovery();

    }

    @Test
    public void startDiscoveryWhileConnectedTest(){
        //Given initialized model
        PowerMockito.when(mMockBluetoothConnection.isConnected()).thenReturn(true);

        //when starting discovery while connected
        mClassUnderTest.startDiscovery();

        //the connection is checked
        InOrder inOrder = inOrder(mMockBluetoothConnection);
        inOrder.verify(mMockBluetoothConnection, Mockito.atLeastOnce()).isConnected();
        //if connected connection is stopped
        inOrder.verify(mMockBluetoothConnection, Mockito.atLeastOnce()).disconnect();
        //then discovery is initiated
        inOrder.verify(mMockBluetoothConnection, Mockito.atLeastOnce()).startDiscovery();
    }

    @Test
    public void stopDiscoveryTest(){
        //Given initialized model

        //when stopDiscovery() is called
        mClassUnderTest.stopDiscovery();

        //the call is propagated
        Mockito.verify(mMockBluetoothConnection, Mockito.atLeastOnce()).stopDiscovery();
    }


    @Test
    public void disconnectTest(){
        //Given initialized model
        PowerMockito.when(mMockBluetoothConnection.isConnected()).thenReturn(true);

        //when disconnect is called
        mClassUnderTest.disconnect();

        //the call is propagated to BluetoothConnection
        Mockito.verify(mMockBluetoothConnection, Mockito.atLeastOnce()).disconnect();
    }

    @Test
    public void sendMessageToRemoteDevice(){
        //Given initialized model
        PowerMockito.when(mMockBluetoothConnection.isConnected()).thenReturn(true);

        //when sendMessageToRemoteDevice
        mClassUnderTest.sendMessageToRemoteDevice(TEST_MESSAGE);
        //the call is propagated to BluetoothConnection
        Mockito.verify(mMockBluetoothConnection, Mockito.atLeastOnce()).sendMessageToRemoteDevice(TEST_MESSAGE);
    }

    @Test
    public void connectToRemoteDeviceTest(){
        //Given initialized model
        //Whitebox.setInternalState(Model.class, mMockBluetoothConnection);
        BluetoothDevice mockDevice  = PowerMockito.mock(BluetoothDevice.class);

        //when connectToDevice() is called
        mClassUnderTest.connectToDevice(mockDevice);
        //respective the call is propagated to BluetoothConnection
        Mockito.verify(mMockBluetoothConnection, Mockito.atLeastOnce()).connectToRemoteDevice(mockDevice);
    }

    @Test
    public void statusUpdateTest(){
        //given initialized Model
        //when device status update is triggered
        mClassUnderTest.updateDeviceStatus(DEVICE_STATUS);

        //the call is propagated to the MainPresenter
        Mockito.verify(mMockMainPresenter, Mockito.atLeastOnce())
                .updateDeviceStatus(DEVICE_STATUS);
    }

    @Test
    public void mainPresenterNotificationTest(){
        //given initialized Model
        //when main notifyMainPresenter() is called
        mClassUnderTest.notifyMainPresenter(TEST_MESSAGE);

        //the message is propagated to UI layer
        Mockito.verify(mMockMainPresenter, Mockito.atLeastOnce()).sendMessageToUI(TEST_MESSAGE);
    }

    @Test
    public void discoveryPresenterNotificationTest(){
        //given initialized Model
        //when discovery presenter is notified
        mClassUnderTest.notifyDiscoveryPresenter(TEST_MESSAGE);

        //the message is propagated to the UI layer
        Mockito.verify(mMockDiscoveryPresenter, Mockito.atLeastOnce()).sendMessageToUI(TEST_MESSAGE);
    }

    @Test
    public void disableBluetoothFeaturesTest(){
        //given initialized Model

        //when disableBluetoothFeatures() is called
        mClassUnderTest.disableBluetoothFeatures();

        //the call propagated to the MainPresenter
        verify(mMockMainPresenter, atLeastOnce()).disableBluetoothFeatures();
    }

    @Test
    public void isBluetoothEnabledWhenEnabledTest(){
        //given initialized Model
        PowerMockito.when(mMockBluetoothConnection.isBluetoothEnabled()).thenReturn(true);

        //when isBluetoothEnabled() is called

        //the returned value is true
        Assert.assertTrue(mClassUnderTest.isBluetoothEnabled());
    }

    @Test
    public void isBluetoothEnabledWhenDisabledTest(){
        //given initialized Model
        PowerMockito.when(mMockBluetoothConnection.isBluetoothEnabled()).thenReturn(false);

        //when isBluetoothEnabled() is called

        //the returned value is false
        Assert.assertFalse(mClassUnderTest.isBluetoothEnabled());
    }

    @Test
    public void onBluetoothOffTest(){
        // given initialized Model

        // when onBluetoothOff is called
        mClassUnderTest.onBluetoothOff();

        // the call is propagated to main and detail presenters
        verify(mMockMainPresenter, atLeastOnce()).onBluetoothOff();
        verify(mMockDiscoveryPresenter, atLeastOnce()).onBluetoothOff();
    }


}
