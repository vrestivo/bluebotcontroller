package com.example.devbox.bluebotcontroller.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.example.devbox.bluebotcontroller.presenter.IDiscoveryPresenter;
import com.example.devbox.bluebotcontroller.presenter.IMainPresenter;
import com.example.devbox.bluebotcontroller.presenter.MainPresenter;

import junit.framework.Assert;

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
import org.robolectric.shadows.ShadowBluetoothAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.subjects.PublishSubject;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.inOrder;
import static org.powermock.api.mockito.PowerMockito.spy;


@RunWith(PowerMockRunner.class)
@PrepareForTest({BluetoothAdapter.class})
@SuppressStaticInitializationFor("com.example.devbox.bluebotcontroller.model.Model")
public class BluetoothConnectionTest {

    private final String MOCK_DEV_1_NAME = "MOCK_DEVICE_1_NAME";
    private final String MOCK_DEV_2_NAME = "MOCK_DEV_2_NAME";

    private final String TEST_STRING = "STEST STRING";

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice1;
    private ShadowBluetoothAdapter mShadowBluetoothAdapter;
    private BluetoothAdapter mMockAdapter;
    private BluetoothSocket mMockBluetoothSocket;
    private BluetoothDevice mMockSelectedBTRemoteDevice;

    private BluetoothConnection mClassUnderTest;
    private Context mMockContext;
    private Model mMockModel;
    private IMainPresenter mMockMainPresenter;
    private IDiscoveryPresenter mMockDiscoveryPresenter;

    private InputStream mMockInputStream;
    private OutputStream mMockOutputStream;


    @Before
    public void setup(){

        mMockContext = PowerMockito.mock(Context.class);
        mMockModel = PowerMockito.mock(Model.class);
        mMockMainPresenter = PowerMockito.mock(MainPresenter.class);
        mMockDiscoveryPresenter = PowerMockito.mock(MockDiscoveryPresenter.class);
        mMockAdapter = PowerMockito.mock(BluetoothAdapter.class);
        mMockBluetoothSocket = PowerMockito.mock(BluetoothSocket.class);
        mMockInputStream = PowerMockito.mock(InputStream.class);
        mMockOutputStream = PowerMockito.mock(OutputStream.class);

        //TODO delete if not needed
        //Whitebox.setInternalState(Model.class, mMockMainPresenter);
        //Whitebox.setInternalState(Model.class, mMockDiscoveryPresenter);

        PowerMockito.mockStatic(BluetoothAdapter.class);
        PowerMockito.when(BluetoothAdapter.getDefaultAdapter()).thenReturn(mMockAdapter);

        mClassUnderTest = new BluetoothConnection(mMockModel, mMockContext);

    }

    @Test
    public void startDiscoveryWhenNotDiscoveringAndDisconnectedTest(){
        //given class under test initialized with Model
        Whitebox.setInternalState(mClassUnderTest, "mBluetoothAdapter", mMockAdapter);
        PowerMockito.when(mMockAdapter.isDiscovering()).thenReturn(true);
        BluetoothConnection spyConnection = spy(mClassUnderTest);

        //when bluetooth adapter is not discovering and
        //discovery is requested
        spyConnection.startDiscovery();

        Mockito.verify(spyConnection, atLeastOnce()).isConnected();

        InOrder adapterOrder = inOrder(mMockAdapter);

        //discovery status is checked
        adapterOrder.verify(mMockAdapter, atLeastOnce()).isDiscovering();

        //then discovery is initialized
        adapterOrder.verify(mMockAdapter, atLeastOnce()).startDiscovery();


    }

    @Test
    public void notifyMainPresenterTest(){
        //given class under test initialized with Model

        //when notifyMainPresenter() is called
        mClassUnderTest.notifyMainPresenter(TEST_STRING);

        //the call is propagated to Model
        Mockito.verify(mMockModel, Mockito.atLeastOnce()).notifyMainPresenter(TEST_STRING);
    }


    @Test
    public void notifyDiscoveryPresenter(){
        //given class under test initialized with Model

        //when notifyDiscoveryPresenter() is called
        mClassUnderTest.notifyDiscoveryPresenter(TEST_STRING);

        //the call is propagated to model
        Mockito.verify(mMockModel, Mockito.atLeastOnce()).notifyDiscoveryPresenter(TEST_STRING);

    }

    @Test
    public void updateDeviceStatusTest(){
        //given class under test initialized with Model


        //when updateDeviceStatus() is called
        mClassUnderTest.updateDeviceStatus(TEST_STRING);

        //the call is propagated to model
        Mockito.verify(mMockModel, Mockito.atLeastOnce()).updateDeviceStatus(TEST_STRING);
    }



    //TODO move to Roboelectric due to RxJava and AndrooidSchedules
/*
    @Test
    public void connectToRemoteDeviceTest(){
        //given initialized connection
        setupBluetoothConectionSocketAndStreamMocks();
        PowerMockito.when(mMockBluetoothSocket.isConnected()).thenReturn(true);

        //when connect is called
        mClassUnderTest.connectToRemoteDevice(mMockSelectedBTRemoteDevice);

        //a BluetoothSocket is returned and streams are open
        try {
            Mockito.verify(mMockSelectedBTRemoteDevice, atLeastOnce())
                    .createRfcommSocketToServiceRecord(BluetoothConnection.SPP_UUID);

            Mockito.verify(mMockBluetoothSocket, atLeastOnce()).getInputStream();
            Mockito.verify(mMockBluetoothSocket, atLeastOnce()).getOutputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //device Status is updated

    }
*/


    private void setupBluetoothConectionSocketAndStreamMocks(){
         mMockSelectedBTRemoteDevice = PowerMockito.mock(BluetoothDevice.class);

        try {
            PowerMockito.when(mMockSelectedBTRemoteDevice
                    .createRfcommSocketToServiceRecord(BluetoothConnection.SPP_UUID))
                    .thenReturn(mMockBluetoothSocket);

            PowerMockito.when(mMockBluetoothSocket.getInputStream())
                    .thenReturn(mMockInputStream);

            PowerMockito.when(mMockBluetoothSocket.getOutputStream())
                    .thenReturn(mMockOutputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    //TODO move to Roboelectric
    @Test
    public void sendMessageToRemoteDeviceReactivelyTest(){

        PowerMockito.when(mMockAdapter.isEnabled()).thenReturn(true);



        mBluetoothDevice1 = PowerMockito.mock(BluetoothDevice.class);
        PowerMockito.when(mBluetoothDevice1.getName()).thenReturn(MOCK_DEV_1_NAME);

        PowerMockito.mockStatic(BluetoothAdapter.class);
        PowerMockito.when(BluetoothAdapter.getDefaultAdapter()).thenReturn(mMockAdapter);

        //TODO delete when test is complete
        Assert.fail();


        Assert.assertEquals(MOCK_DEV_1_NAME, mBluetoothDevice1.getName());
        Assert.assertTrue(BluetoothAdapter.getDefaultAdapter().isEnabled());
    }



}
