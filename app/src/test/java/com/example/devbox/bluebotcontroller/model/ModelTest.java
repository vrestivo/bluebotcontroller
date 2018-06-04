package com.example.devbox.bluebotcontroller.model;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.example.devbox.bluebotcontroller.presenter.IDiscoveryPresenter;
import com.example.devbox.bluebotcontroller.presenter.IMainPresenter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.mockito.Mockito.inOrder;

@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("com.example.devbox.bluebotcontroller.model.Model")
@PrepareForTest({Model.class, BluetoothConnection.class})
public class ModelTest {

    private final String DEVICE_STATUS = "TESTING";
    private final String TEST_MESSAGE = "TEST MESAGE";


    private IModel mClassUnderTest;
    private Context mMockContext;
    private IMainPresenter mMockMainPresenter;
    private IDiscoveryPresenter mMockDiscoveryPresenter;

    @Mock
    public BluetoothConnection mMockBluetoothConnection;


    @Before
    public void testSetup(){
        mMockContext = PowerMockito.mock(Context.class);
        mMockMainPresenter = PowerMockito.mock(MockMainPresenter.class);
        mMockDiscoveryPresenter = PowerMockito.mock(MockDiscoveryPresenter.class);
        mMockBluetoothConnection = PowerMockito.mock(BluetoothConnection.class);


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
        //respecive the call is propagated to BluetoothConnection
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

        //the mesasge is propagated to UI layer
        Mockito.verify(mMockMainPresenter, Mockito.atLeastOnce()).sendMessageToUI(TEST_MESSAGE);
    }

    @Test
    public void discoveryPresenterNotificationTest(){
        //given initialized Model
        //when discovery presenter is notified
        mClassUnderTest.notifyDiscoveryPresenter(TEST_MESSAGE);

        //the message is propagated to the UI layer
        Mockito.verify(mMockDiscoveryPresenter, Mockito.atLeastOnce()).sendMesageToUI(TEST_MESSAGE);
    }

}
