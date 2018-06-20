package com.example.devbox.bluebotcontroller.model;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.example.devbox.bluebotcontroller.BuildConfig;

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
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
@PrepareForTest({BluetoothAdapter.class})
@SuppressStaticInitializationFor("com.example.devbox.bluebotcontroller.model.Model")
public class BroadCastReceiverTest {

    private LocalBroadcastManager mLocalBroadcastManager;

    private BluetoothConnection mClassUnderTest;

    private IModel mMockModel;
    private ShadowApplication mShadowApplication;
    private List<ShadowApplication.Wrapper> mRegisteredReceivers;


    @Before
    public void testSetup(){
        mMockModel = PowerMockito.mock(Model.class);

        mClassUnderTest = new BluetoothConnection(mMockModel, RuntimeEnvironment.application.getApplicationContext());

        //RuntimeEnvironment.application -- application context
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(RuntimeEnvironment.application);
    }

    private void getAndVerifyReceiver(){
        mShadowApplication = ShadowApplication.getInstance();
        mRegisteredReceivers = mShadowApplication.getRegisteredReceivers();

        Assert.assertFalse(mRegisteredReceivers.isEmpty());
        Assert.assertTrue(bluetoothBroadcastReceiverFound(mRegisteredReceivers));
    }

    private boolean bluetoothBroadcastReceiverFound(List<ShadowApplication.Wrapper> registeredReceivers){
        boolean found = false;
        for(ShadowApplication.Wrapper wrapper : registeredReceivers){
            if(wrapper.broadcastReceiver.getClass().getSimpleName()
                    .equals(BluetoothBroadcastReceiver.class.getSimpleName())) {
                found = true;
                break;
            }
        }

        return found;
    }


    @Test
    public void mockModelInteractionTest(){
        //given initialized BluetoothConnection and mock model

        //when the class under test interacts with mock model
        mClassUnderTest.notifyMainPresenter("message");

        //interaction can be tracked
        Mockito.verify(mMockModel, Mockito.atLeastOnce()).notifyMainPresenter("message");
    }


    @Test
    public void verifyBluetoothBroadcastReceiverIsRegisteredAndUnregisterTest(){
        //given initialized BluetoothConnection and mock model

        //when the class is created

        //BluetoothBroadcastReceiver is registered
        ShadowApplication shadowApplication = ShadowApplication.getInstance();
        List<ShadowApplication.Wrapper> registeredReceivers = shadowApplication.getRegisteredReceivers();

        Assert.assertFalse(registeredReceivers.isEmpty());
        Assert.assertTrue(bluetoothBroadcastReceiverFound(registeredReceivers));

        mClassUnderTest.unregisterReceiver();
        registeredReceivers = shadowApplication.getRegisteredReceivers();
        Assert.assertTrue(registeredReceivers.isEmpty());
    }

    @Test
    public void selfUnregisterTest(){
        // given initialized Bluetooth connection and
        // a BluetoothBroadcastReceiver
        getAndVerifyReceiver();

        // when  ACTION_SELF_UNREGISTER is sent
        mShadowApplication.sendBroadcast(new Intent(BluetoothBroadcastReceiver.ACTION_SELF_UNREGISTER));

        // the receiver is unregistered
        mRegisteredReceivers = mShadowApplication.getRegisteredReceivers();
        Assert.assertTrue(mRegisteredReceivers.isEmpty());
    }

    private Intent createConnectionStateIntent(int stateCode){

        Intent intent = new Intent();
        intent.setAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        intent.putExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, stateCode);

        return intent;
    }


    @Test
    public void updateConnectionStatusOnConnectedState(){
        // given initialized Bluetooth connection and
        // a BluetoothBroadcastReceiver
        getAndVerifyReceiver();

        // when "connected" state updates is received
        mShadowApplication.sendBroadcast(createConnectionStateIntent(BluetoothAdapter.STATE_CONNECTED));
        mShadowApplication.sendBroadcast(createConnectionStateIntent(BluetoothAdapter.STATE_DISCONNECTED));


        // Model.updateDeviceStatus() is called
        InOrder inOrder = Mockito.inOrder(mMockModel);
        inOrder.verify(mMockModel, Mockito.atLeastOnce()).updateDeviceStatus(BluetoothConnection.STATUS_CONNECTED);
        inOrder.verify(mMockModel, Mockito.atLeastOnce()).updateDeviceStatus(BluetoothConnection.STATUS_DISCONNECTED);

        mClassUnderTest.unregisterReceiver();
    }

    //Generic broadcast receiver test
    @Test
    public void genericBroadcastReceiverTest(){
        //given initialized local broadcast manager
        Assert.assertNotNull(mLocalBroadcastManager);
        Assert.assertSame(mLocalBroadcastManager, LocalBroadcastManager.getInstance(RuntimeEnvironment.application));

        final boolean[] sent = new boolean[1];
        sent[0] = false;

        final BroadcastReceiver receiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                sent[0] = true;
            }
        };

        mLocalBroadcastManager.registerReceiver(receiver, new IntentFilter("blah"));
        mLocalBroadcastManager.sendBroadcast(new Intent("blah"));
        Assert.assertTrue(sent[0]);
        mLocalBroadcastManager.unregisterReceiver(receiver);
    }



}
