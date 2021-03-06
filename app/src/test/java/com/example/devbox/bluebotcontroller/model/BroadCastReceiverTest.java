package com.example.devbox.bluebotcontroller.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import com.example.devbox.bluebotcontroller.BuildConfig;
import com.example.devbox.bluebotcontroller.model.Bluetooth.BluetoothBroadcastReceiver;
import com.example.devbox.bluebotcontroller.model.Bluetooth.BluetoothConnection;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import java.util.List;
import static com.example.devbox.bluebotcontroller.Constants.*;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
@PrepareForTest({BluetoothAdapter.class, BluetoothConnection.class})
@SuppressStaticInitializationFor("com.example.devbox.bluebotcontroller.model.Model")
public class BroadCastReceiverTest {

    private final String EXTRA_TEST_DEVICE = "TEST_DEVICE";
    private final String TEST_MAC = "00:11:22:33:AA:BB";
    private BluetoothConnection mBluetoothConnection;
    private BluetoothConnection mMockBluetoothConnection;
    private BluetoothBroadcastReceiver mBluetoothBroadcastReceiver;
    private IModel mMockModel;
    private ShadowApplication mShadowApplication;
    private List<ShadowApplication.Wrapper> mRegisteredReceivers;


    @After
    public void testCleanup() {
        if(mShadowApplication!= null) {
            cleanupIntegrated();
            cleanupIsolated();
        }
    }


    private void cleanupIntegrated(){
        if (mBluetoothConnection != null && bluetoothBroadcastReceiverFound(mShadowApplication.getRegisteredReceivers())) {
            mBluetoothConnection.unregisterReceiver();
        }
    }


    private void cleanupIsolated(){
        if (mBluetoothBroadcastReceiver!=null && bluetoothBroadcastReceiverFound(mShadowApplication.getRegisteredReceivers())){
            mShadowApplication.getApplicationContext().unregisterReceiver(mBluetoothBroadcastReceiver);
        }
    }


    private void initializeIntegrated() {
        mShadowApplication = ShadowApplication.getInstance();
        mMockModel = PowerMockito.mock(Model.class);
        //RuntimeEnvironment.application -- application context
        mBluetoothConnection = new BluetoothConnection(mMockModel, RuntimeEnvironment.application.getApplicationContext());
    }


    private void initializeIsolated() {
        mShadowApplication = ShadowApplication.getInstance();
        mMockBluetoothConnection = PowerMockito.mock(BluetoothConnection.class);
        mBluetoothBroadcastReceiver = new BluetoothBroadcastReceiver(mMockBluetoothConnection);

        mShadowApplication
                .registerReceiver(mBluetoothBroadcastReceiver, mBluetoothBroadcastReceiver.generateIntentFilters());
    }


    private void verifyReceiverIsRegistered() {
        mRegisteredReceivers = mShadowApplication.getRegisteredReceivers();
        Assert.assertFalse(mRegisteredReceivers.isEmpty());
        Assert.assertTrue(bluetoothBroadcastReceiverFound(mRegisteredReceivers));
    }


    private boolean bluetoothBroadcastReceiverFound(List<ShadowApplication.Wrapper> registeredReceivers) {
        boolean found = false;
        for (ShadowApplication.Wrapper wrapper : registeredReceivers) {
            if (wrapper.broadcastReceiver.getClass().getSimpleName()
                    .equals(BluetoothBroadcastReceiver.class.getSimpleName())) {
                found = true;
                break;
            }
        }
        return found;
    }


    private BluetoothDevice createATestRemoteDevice(String deviceMAC) {
        BluetoothDevice testDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceMAC);
        Assert.assertNotNull(deviceMAC);
        Assert.assertEquals(deviceMAC, testDevice.getAddress());
        return testDevice;
    }


    private Intent createDeviceFoundIntent(BluetoothDevice testDevice) {
        Intent intent = new Intent();
        intent.setAction(BluetoothDevice.ACTION_FOUND);
        intent.putExtra(BluetoothDevice.EXTRA_DEVICE, testDevice);
        return intent;
    }


    private Intent createConnectionStateIntent(int stateCode) {
        Intent intent = new Intent();
        intent.setAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        intent.putExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, stateCode);
        return intent;
    }


    private void sendActionStateChangeIntent(int state){
        Intent intent = new Intent();
        intent.setAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intent.putExtra(BluetoothAdapter.EXTRA_STATE, state);
        mShadowApplication.getApplicationContext().sendBroadcast(intent);
    }


    @Test
    public void mockModelInteractionTest() {
        // given initialized BluetoothConnection and mock model
        initializeIntegrated();

        // when the class under test interacts with mock model
        mBluetoothConnection.notifyMainPresenter("message");

        // interaction is valid
        Mockito.verify(mMockModel, Mockito.atLeastOnce()).notifyMainPresenter("message");
    }


    @Test
    public void verifyBluetoothBroadcastReceiverIsRegisteredAndUnregisterTest() {
        //given initialized BluetoothConnection and mock model
        //when the class is created
        initializeIntegrated();

        // The BluetoothBroadcastReceiver is registered
        verifyReceiverIsRegistered();

        // The BluetoothBroadcastReceiver can be unregistered
        mBluetoothConnection.unregisterReceiver();
        mRegisteredReceivers = mShadowApplication.getRegisteredReceivers();
        Assert.assertTrue(mRegisteredReceivers.isEmpty());
        Assert.assertFalse(bluetoothBroadcastReceiverFound(mRegisteredReceivers));
    }


    @Test
    public void selfUnregisterTest() {
        // given initialized Bluetooth connection and
        // a BluetoothBroadcastReceiver
        initializeIntegrated();
        verifyReceiverIsRegistered();

        // when  ACTION_SELF_UNREGISTER is sent
        mShadowApplication.sendBroadcast(new Intent(BluetoothBroadcastReceiver.ACTION_SELF_UNREGISTER));

        // the receiver is unregistered
        mRegisteredReceivers = mShadowApplication.getRegisteredReceivers();
        Assert.assertTrue(mRegisteredReceivers.isEmpty());
    }


    @Test
    public void updateConnectionStatusOnConnectedState() {
        // given initialized Bluetooth connection and
        // a BluetoothBroadcastReceiver
        initializeIntegrated();
        verifyReceiverIsRegistered();

        // when "connected" state updates is received
        mShadowApplication.sendBroadcast(createConnectionStateIntent(BluetoothAdapter.STATE_CONNECTED));
        mShadowApplication.sendBroadcast(createConnectionStateIntent(BluetoothAdapter.STATE_DISCONNECTED));


        // Model.updateDeviceStatus() is called
        InOrder inOrder = Mockito.inOrder(mMockModel);
        inOrder.verify(mMockModel, Mockito.atLeastOnce()).updateDeviceStatus(STATUS_CONNECTED);
        inOrder.verify(mMockModel, Mockito.atLeastOnce()).updateDeviceStatus(STATUS_DISCONNECTED);
    }


    @Test
    public void genericBroadcastReceiverTest() {
        //given initialized local broadcast manager
        initializeIntegrated();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(RuntimeEnvironment.application);
        Assert.assertNotNull(localBroadcastManager);
        Assert.assertSame(localBroadcastManager, LocalBroadcastManager.getInstance(RuntimeEnvironment.application));

        final boolean[] sent = new boolean[1];
        sent[0] = false;

        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                sent[0] = true;
            }
        };

        localBroadcastManager.registerReceiver(receiver, new IntentFilter("blah"));
        localBroadcastManager.sendBroadcast(new Intent("blah"));
        Assert.assertTrue(sent[0]);
        localBroadcastManager.unregisterReceiver(receiver);
    }


    @Test
    public void deviceFoundTest() {
        // given initialized BroadcastReceiver
        // and a mock BluetoothConnection
        initializeIsolated();
        verifyReceiverIsRegistered();

        // when device is found
        BluetoothDevice testDevice = createATestRemoteDevice(TEST_MAC);
        Intent deviceFoundIntent = createDeviceFoundIntent(testDevice);
        mShadowApplication.getApplicationContext().sendBroadcast(deviceFoundIntent);

        // is it passed to BluetoothConnection
        Mockito.verify(mMockBluetoothConnection, Mockito.atLeastOnce()).onDeviceFound(testDevice);
    }


    @Test
    public void foundDevicesPropagatedToModelTest() {
        // given initialized Bluetooth connection and
        // a BluetoothBroadcastReceiver
        initializeIntegrated();
        verifyReceiverIsRegistered();

        // when bluetooth device is found
        BluetoothDevice remoteTestDevice = createATestRemoteDevice(TEST_MAC);
        Intent deviceFoundIntent = createDeviceFoundIntent(remoteTestDevice);
        mShadowApplication.getApplicationContext().sendBroadcast(deviceFoundIntent);

        // the update is propagated to the model
        Mockito.verify(mMockModel, Mockito.atLeastOnce()).loadAvailableDevices(Matchers.anySet());
    }


    @Test
    public void onReceivedActionOffTest() {
        // given initialized mock BluetoothConnection,
        // a BluetoothBroadcastReceiver
        initializeIsolated();
        verifyReceiverIsRegistered();

        // when bluetooth is turned off
        sendActionStateChangeIntent(BluetoothAdapter.STATE_OFF);

        // the connection is closed
        Mockito.verify(mMockBluetoothConnection, Mockito.atLeastOnce()).onBluetoothOff();
        Mockito.verify(mMockBluetoothConnection, Mockito.atLeastOnce()).disconnect();
    }


    @Test
    public void receivedActionOffPropagatedToModelTest(){
        // given initialized Bluetooth connection and
        // a BluetoothBroadcastReceiver
        initializeIntegrated();
        verifyReceiverIsRegistered();

        // when bluetooth is turned off
        sendActionStateChangeIntent(BluetoothAdapter.STATE_OFF);

        // the event is propagated to the Model
        Mockito.verify(mMockModel, Mockito.atLeastOnce()).onBluetoothOff();
    }


    @Test
    public void onReceivedActionOnTest(){
        // given initialized mock BluetoothConnection,
        // a BluetoothBroadcastReceiver
        initializeIsolated();
        verifyReceiverIsRegistered();

        // when bluetooth is turned on
        sendActionStateChangeIntent(BluetoothAdapter.STATE_ON);

        // the connection is closed
        Mockito.verify(mMockBluetoothConnection, Mockito.atLeastOnce()).onBluetoothOn();
    }


    @Test
    public void receivedActionOnPropagatedToModelTest(){
        // given initialized Bluetooth connection and
        // a BluetoothBroadcastReceiver
        initializeIntegrated();
        verifyReceiverIsRegistered();

        // when bluetooth is turned on
        sendActionStateChangeIntent(BluetoothAdapter.STATE_ON);

        // the event is propagated to the Model
        Mockito.verify(mMockModel, Mockito.atLeastOnce()).onBluetoothOn();
    }

}
