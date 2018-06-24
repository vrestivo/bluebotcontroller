package com.example.devbox.bluebotcontroller.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.IntentFilter;
import android.support.annotation.NonNull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import io.reactivex.plugins.RxJavaPlugins;

import static org.mockito.Matchers.anySet;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.spy;


@RunWith(PowerMockRunner.class)
@PrepareForTest({BluetoothAdapter.class, BluetoothConnection.class, BluetoothBroadcastReceiver.class})
@SuppressStaticInitializationFor("com.example.devbox.bluebotcontroller.model.Model")
public class BluetoothConnectionTest {

    private final String MOCK_DEV_1_NAME = "MOCK_DEVICE_1_NAME";
    private final String MOCK_DEV_2_NAME = "MOCK_DEV_2_NAME";

    private final String TEST_STRING = "TEST STRING";

    private BluetoothAdapter mMockAdapter;
    private BluetoothSocket mMockBluetoothSocket;
    private BluetoothDevice mMockSelectedBTRemoteDevice;
    private IntentFilter mMockIntentFilter;
    private BluetoothConnection mClassUnderTest;
    private Context mMockContext;
    private Model mMockModel;
    private InputStream mMockInputStream;
    private OutputStream mMockOutputStream;


    private byte[] mTestInputArray = new byte[1024];
    private byte[] mTestReturnedByteArray = new byte[]{'T', 'e', 's', 't', '\0'};


    @BeforeClass
    public static void classSetup() {
        setupRxStreams();
    }

    /**
     * This fixes initialization issues in RxJava tests
     * <p>
     * solution from:
     * https://stackoverflow.com/questions/43356314/android-rxjava-2-junit-test-getmainlooper-in-android-os-looper-not-mocked-runt
     */
    private static void setupRxStreams() {
        Scheduler immediate = new Scheduler() {
            @Override
            public Disposable scheduleDirect(@NonNull Runnable run, long delay, @NonNull TimeUnit unit) {
                // this prevents StackOverflowErrors when scheduling with a delay
                return super.scheduleDirect(run, 0, unit);
            }

            @Override
            public Worker createWorker() {

                //TODO check instantiation
                //Executors.newSingleThreadExecutor().
                return new ExecutorScheduler.ExecutorWorker(Runnable::run);
            }
        };

        RxJavaPlugins.setInitIoSchedulerHandler(schedulerCallable -> immediate);
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> immediate);
    }


    @Before
    public void setup() {
        mMockContext = PowerMockito.mock(Context.class);
        mMockModel = PowerMockito.mock(Model.class);
        mMockAdapter = PowerMockito.mock(BluetoothAdapter.class);
        mMockBluetoothSocket = PowerMockito.mock(BluetoothSocket.class);
        mMockInputStream = PowerMockito.mock(InputStream.class);
        mMockOutputStream = PowerMockito.mock(OutputStream.class);
        mMockIntentFilter = PowerMockito.mock(IntentFilter.class);

        // prevents method not mocked errors
        PowerMockito.mock(BluetoothBroadcastReceiver.class);
        try {
            PowerMockito.whenNew(IntentFilter.class).withNoArguments().thenReturn(mMockIntentFilter);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    private void normalBluetoothAdapterInitialization(){
        PowerMockito.mockStatic(BluetoothAdapter.class);
        PowerMockito.when(BluetoothAdapter.getDefaultAdapter()).thenReturn(mMockAdapter);

        mClassUnderTest = new BluetoothConnection(mMockModel, mMockContext);
        setupBluetoothConnectionSocketAndStreamMocks();
    }


    private void setupReturnNullBluetoothAdapter(){
        PowerMockito.mockStatic(BluetoothAdapter.class);
        PowerMockito.when(BluetoothAdapter.getDefaultAdapter()).thenReturn(null);

        mClassUnderTest = new BluetoothConnection(mMockModel, mMockContext);
        setupBluetoothConnectionSocketAndStreamMocks();
    }

    private void setupBluetoothConnectionSocketAndStreamMocks() {
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


    private void verifyUnsupportedDeviceIsHandledCorrectly(){
        verify(mMockModel, atLeastOnce()).disableBluetoothFeatures();
        verify(mMockModel, atLeastOnce()).notifyMainPresenter(BluetoothConnection.MSG_BT_NOT_SUPPORTED);
        verify(mMockModel, atLeastOnce()).updateDeviceStatus(BluetoothConnection.STATUS_NOT_SUPPORTED);
    }

    @Test
    public void  startDiscoveryTest() {
        //given class under test initialized with Model
        normalBluetoothAdapterInitialization();

        Whitebox.setInternalState(mClassUnderTest, "mBluetoothAdapter", mMockAdapter);
        PowerMockito.when(mMockAdapter.isDiscovering()).thenReturn(true);

        BluetoothConnection spyConnection = spy(mClassUnderTest);

        //when bluetooth adapter is discovering and
        //discovery is requested
        spyConnection.startDiscovery();

        // connection status is checked
        Mockito.verify(spyConnection, atLeastOnce()).isConnected();

        InOrder adapterOrder = inOrder(mMockAdapter);
        // discovery status is checked
        adapterOrder.verify(mMockAdapter, atLeastOnce()).isDiscovering();
        // if active - discovery is cancelled
        adapterOrder.verify(mMockAdapter, atLeastOnce()).cancelDiscovery();
        // then discovery is initialized
        adapterOrder.verify(mMockAdapter, atLeastOnce()).startDiscovery();
    }


    @Test
    public void notifyMainPresenterTest() {
        //given class under test initialized with Model
        normalBluetoothAdapterInitialization();


        //when notifyMainPresenter() is called
        mClassUnderTest.notifyMainPresenter(TEST_STRING);

        //the call is propagated to Model
        Mockito.verify(mMockModel, Mockito.atLeastOnce()).notifyMainPresenter(TEST_STRING);
    }


    @Test
    public void notifyDiscoveryPresenter() {
        //given class under test initialized with Model
        normalBluetoothAdapterInitialization();


        //when notifyDiscoveryPresenter() is called
        mClassUnderTest.notifyDiscoveryPresenter(TEST_STRING);

        //the call is propagated to model
        Mockito.verify(mMockModel, Mockito.atLeastOnce()).notifyDiscoveryPresenter(TEST_STRING);
    }


    @Test
    public void updateDeviceStatusTest() {
        //given class under test initialized with Model
        normalBluetoothAdapterInitialization();

        //when updateDeviceStatus() is called
        mClassUnderTest.updateConnectionStatusIndicator(TEST_STRING);

        //the call is propagated to model
        Mockito.verify(mMockModel, Mockito.atLeastOnce()).updateDeviceStatus(TEST_STRING);
    }


    @Test
    public void connectToRemoteDeviceTest() {
        //given initialized connection
        normalBluetoothAdapterInitialization();
        PowerMockito.when(mMockBluetoothSocket.isConnected()).thenReturn(true, true, true, true, true, false);

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
    }


    @Test
    public void bluetoothUnsupportedOnInitializationTest(){
        //given initialized connection
        setupReturnNullBluetoothAdapter();

        //when Bluetooth is not supported:
        //BluetoothAdapter.getDefaultAdapter() returned null

        //main presenter is notified and
        //bluetooth features are disabled
        verifyUnsupportedDeviceIsHandledCorrectly();
    }


    @Test
    public void bluetoothNotSupportedOnRequestTest() {
        //given initialized connection
        setupReturnNullBluetoothAdapter();

        //when Bluetooth is not supported:

        //main presenter is notified and
        //bluetooth features are disabled
        verifyUnsupportedDeviceIsHandledCorrectly();
    }


    @Test
    public void isBluetoothSupportedTest(){
        //given initialized connection
        normalBluetoothAdapterInitialization();

        //when Bluetooth is supported:

        //disableBuetoothFeatures() is not called
        verify(mMockModel, never()).disableBluetoothFeatures();
    }


    @Test
    public void isBluetoothEnabledReturnsTrueTest(){
        //given initialized connection
        normalBluetoothAdapterInitialization();
        PowerMockito.when(mMockAdapter.isEnabled()).thenReturn(true);

        //when BluetoothAdapter.isEnabled() returns true

        //mClassUnderTest.isBluetoothEnabled() returns true
        Assert.assertTrue(mClassUnderTest.isBluetoothEnabled());
    }


    @Test
    public void isBluetoothEnabledReturnsFalse(){
        //given initialized connection
        normalBluetoothAdapterInitialization();
        PowerMockito.when(mMockAdapter.isEnabled()).thenReturn(false);

        //when BluetoothAdapter.isEnabled() returns false

        //mClassUnderTest.isBluetoothEnabled() returns true
        Assert.assertFalse(mClassUnderTest.isBluetoothEnabled());
    }


    @Test
    public void subscribeToInputAndOutputStreamTest() {
        //given initialized connection
        normalBluetoothAdapterInitialization();
        PowerMockito.when(mMockBluetoothSocket.isConnected()).thenReturn(true);

        //when connect is called
        mClassUnderTest.connectToRemoteDevice(mMockSelectedBTRemoteDevice);
        mClassUnderTest.sendMessageToRemoteDevice(TEST_STRING);

        //allow thread time to finish work
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mClassUnderTest.disconnect();

        try {
            verify(mMockBluetoothSocket, atLeastOnce()).getInputStream();
            verify(mMockBluetoothSocket, atLeastOnce()).getOutputStream();
            verify(mMockBluetoothSocket, atLeastOnce()).close();
            verify(mMockInputStream, atLeastOnce()).read(mTestInputArray);
            verify(mMockInputStream, atLeastOnce()).close();
            verify(mMockOutputStream, atLeastOnce()).close();
            verify(mMockOutputStream, atLeastOnce()).write(TEST_STRING.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getKnownDevicesTest(){
        // given initialized connection
        normalBluetoothAdapterInitialization();

        // when known devices are requested
        mClassUnderTest.getKnownDevices();

        // the list of paired and available devices
        // is passed to the model
        verify(mMockModel, atLeastOnce()).loadAvailableDevices(anySet());
        verify(mMockModel, atLeastOnce()).loadPairedDevices(anySet());
    }


    @Test
    public void testName(){
        // given initialized connection
        normalBluetoothAdapterInitialization();

        // when

        // the
    }



}
