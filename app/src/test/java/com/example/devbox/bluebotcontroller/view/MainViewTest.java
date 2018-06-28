package com.example.devbox.bluebotcontroller.view;


import android.content.Intent;
import android.widget.Button;

import com.example.devbox.bluebotcontroller.BuildConfig;
import com.example.devbox.bluebotcontroller.R;
import com.example.devbox.bluebotcontroller.model.Model;
import com.example.devbox.bluebotcontroller.presenter.MainPresenter;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

@Config(constants = BuildConfig.class)
@RunWith(RobolectricTestRunner.class)
public class MainViewTest {

    private final String TEST_MSG = "TEST";

    private MainViewActivity mClassUnderTest;
    private MainPresenter mMockMainPresenter;


    @Before
    public void testSetup(){
        mClassUnderTest = Robolectric.setupActivity(MainViewActivity.class);
    }


    private void setupMockMainPresenter(){
        mMockMainPresenter = PowerMockito.mock(MainPresenter.class);
        Whitebox.setInternalState(mClassUnderTest, "mMainPresenter", mMockMainPresenter);
    }

    @Test
    public void mainViewActivityTest(){
        Assert.assertNotNull(mClassUnderTest);
        Assert.assertEquals(MainViewActivity.class.getSimpleName(), mClassUnderTest.getClass().getSimpleName());
    }


    @Test
    public void checkForPresenceOfExpectedButtonsArePresent(){
        // given initialized MainViewActivity

        // when the activity is started

        // all expected elements are present
        Assert.assertNotNull(mClassUnderTest.findViewById(R.id.bt_on));
        Assert.assertNotNull(mClassUnderTest.findViewById(R.id.bt_disconnect));
        Assert.assertNotNull(mClassUnderTest.findViewById(R.id.bt_discover));
        Assert.assertNotNull(mClassUnderTest.findViewById(R.id.bt_send));
        Assert.assertNotNull(mClassUnderTest.findViewById(R.id.exit_text));
    }

    @Test
    public void verifyDiscoveryActivityLaunchedTest(){
        // given initialized MainViewActivity
        Button discoveryButton = mClassUnderTest.findViewById(R.id.bt_discover);
        Assert.assertNotNull(discoveryButton);

        // when start discovery button is pressed
        discoveryButton.performClick();

        // discovery activity is started
        Intent discoveryIntent = Shadows.shadowOf(mClassUnderTest).peekNextStartedActivity();
        Assert.assertEquals(DiscoveryViewActivity.class.getCanonicalName(), discoveryIntent.getComponent().getClassName());
    }


    @Test
    public void checkBluetoothPermissionsTestTest(){
        // given initialized MainViewActivity
        setupMockMainPresenter();

        // when check for bluetooth permissions is requested
        mClassUnderTest.checkBluetoothPermissions();

        //the call is passed to presenter
        Mockito.verify(mMockMainPresenter, Mockito.atLeastOnce()).checkBluetoothPermissions();
    }

    @Test
    public void sendMessageToRemoteDeviceTest(){
        // given initialized MainViewActivity
        setupMockMainPresenter();

        // message is sent to remote device
        mClassUnderTest.sendMessageToRemoteDevice(TEST_MSG);

        // the call is propagated to the presenter layer
        Mockito.verify(mMockMainPresenter, Mockito.atLeastOnce()).sendMessageToRemoteDevice(TEST_MSG);
    }


    @Test
    public void disconnectTest(){
        // given initialized MainViewActivity
        setupMockMainPresenter();

        // when user requests to disconnect from remote device
        mClassUnderTest.disconnect();

        // the request is forwarded to presenter
        Mockito.verify(mMockMainPresenter, Mockito.atLeastOnce()).disconnect();
    }


    @Test
    public void bluetoothOnTest(){
        // given initialized MainViewActivity
        setupMockMainPresenter();

        // when
        mClassUnderTest.onBluetoothOn();

        // the request is forwarded to presenter
        Mockito.verify(mMockMainPresenter, Mockito.atLeastOnce()).onBluetoothOn();
    }


    @Test
    public  void bluetoothOffTest(){
        // given initialized MainViewActivity
        setupMockMainPresenter();

        // when user requested to turn off Bluetooth
        mClassUnderTest.onBluetoothOff();

        // the request is forwarded to presenter
        Mockito.verify(mMockMainPresenter, Mockito.atLeastOnce()).onBluetoothOff();
    }

}
