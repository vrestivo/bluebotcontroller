package com.example.devbox.bluebotcontroller.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.devbox.bluebotcontroller.presenter.IDiscoveryPresenter;
import com.example.devbox.bluebotcontroller.presenter.IMainPresenter;
import com.example.devbox.bluebotcontroller.presenter.MainPresenter;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
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
import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import io.reactivex.plugins.RxJavaPlugins;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.spy;


@RunWith(PowerMockRunner.class)
@PrepareForTest({BluetoothAdapter.class})
@SuppressStaticInitializationFor("com.example.devbox.bluebotcontroller.model.Model")
public class BluetoothConnectionTest {

    private final String MOCK_DEV_1_NAME = "MOCK_DEVICE_1_NAME";
    private final String MOCK_DEV_2_NAME = "MOCK_DEV_2_NAME";

    private final String TEST_STRING = "TEST STRING";

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
        mMockMainPresenter = PowerMockito.mock(MainPresenter.class);
        mMockDiscoveryPresenter = PowerMockito.mock(MockDiscoveryPresenter.class);
        mMockAdapter = PowerMockito.mock(BluetoothAdapter.class);
        mMockBluetoothSocket = PowerMockito.mock(BluetoothSocket.class);
        mMockInputStream = PowerMockito.mock(InputStream.class);
        mMockOutputStream = PowerMockito.mock(OutputStream.class);

        PowerMockito.mockStatic(BluetoothAdapter.class);
        PowerMockito.when(BluetoothAdapter.getDefaultAdapter()).thenReturn(mMockAdapter);

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


    @Test
    public void  startDiscoveryTest() {
        //given class under test initialized with Model
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

        //when notifyMainPresenter() is called
        mClassUnderTest.notifyMainPresenter(TEST_STRING);

        //the call is propagated to Model
        Mockito.verify(mMockModel, Mockito.atLeastOnce()).notifyMainPresenter(TEST_STRING);
    }


    @Test
    public void notifyDiscoveryPresenter() {
        //given class under test initialized with Model

        //when notifyDiscoveryPresenter() is called
        mClassUnderTest.notifyDiscoveryPresenter(TEST_STRING);

        //the call is propagated to model
        Mockito.verify(mMockModel, Mockito.atLeastOnce()).notifyDiscoveryPresenter(TEST_STRING);
    }

    @Test
    public void updateDeviceStatusTest() {
        //given class under test initialized with Model

        //when updateDeviceStatus() is called
        mClassUnderTest.updateDeviceStatus(TEST_STRING);

        //the call is propagated to model
        Mockito.verify(mMockModel, Mockito.atLeastOnce()).updateDeviceStatus(TEST_STRING);
    }


    @Test
    public void connectToRemoteDeviceTest() {
        //given initialized connection
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
    public void bluetoothNotSupportedTest(){
        //given initialized connection

    }


    @Test
    public void subscribeToInputAndOutputStreamTest() {
        //given initialized connection
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


}
