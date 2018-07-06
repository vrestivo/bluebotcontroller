package com.example.devbox.bluebotcontroller.view;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.devbox.bluebotcontroller.BuildConfig;
import com.example.devbox.bluebotcontroller.R;
import com.example.devbox.bluebotcontroller.Util;
import com.example.devbox.bluebotcontroller.presenter.MainPresenter;
import com.example.devbox.bluebotcontroller.view.joystick.JoystickHandlerThread;

import org.junit.After;
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
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

@Config(constants = BuildConfig.class)
@RunWith(RobolectricTestRunner.class)
public class MainViewTest {

    private final String TEST_MSG = "TEST";

    private MainViewActivity mClassUnderTest;
    private MainPresenter mMockMainPresenter;
    private ActivityController <MainViewActivity> mClassUnderTestController;
    private String mMainPresenterFieldName = "mMainPresenter";


    @Before
    public void testSetup(){
        mClassUnderTest = Robolectric.setupActivity(MainViewActivity.class);
    }


    private void setupMockMainPresenter(){
        mMockMainPresenter = PowerMockito.mock(MainPresenter.class);
        Whitebox.setInternalState(mClassUnderTest, mMainPresenterFieldName, mMockMainPresenter);
    }


    private void createActivityControllerAndMockPresenter(){
        mMockMainPresenter = PowerMockito.mock(MainPresenter.class);
        mClassUnderTestController = Robolectric.buildActivity(MainViewActivity.class);
    }


    private void createActivityControllerAndMockPresenterInitializedAtOnCreate(){
        createActivityControllerAndMockPresenter();
        Bundle bundle = new Bundle();
        mClassUnderTestController.create();
        Whitebox.setInternalState(mClassUnderTestController.get(), mMainPresenterFieldName, mMockMainPresenter);
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
        Assert.assertNotNull(mClassUnderTest.findViewById(R.id.edit_text));
        Assert.assertNotNull(mClassUnderTest.findViewById(R.id.joystick_view));
        Assert.assertNotNull(mClassUnderTest.findViewById(R.id.con_status));
        Assert.assertNotNull(mClassUnderTest.findViewById(R.id.bt_on));
        Assert.assertNotNull(mClassUnderTest.findViewById(R.id.bt_disconnect));
        Assert.assertNotNull(mClassUnderTest.findViewById(R.id.bt_discover));
        Assert.assertNotNull(mClassUnderTest.findViewById(R.id.bt_send));
        Assert.assertNotNull(mClassUnderTest.findViewById(R.id.edit_text));
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
        mClassUnderTest.enableBluetooth();

        // the request is forwarded to presenter
        Mockito.verify(mMockMainPresenter, Mockito.atLeastOnce()).enableBluetooth();
    }


    @Test
    public void bluetoothOffTest(){
        // given initialized MainViewActivity
        setupMockMainPresenter();

        // when user requested to turn off Bluetooth
        mClassUnderTest.disableBluetooth();

        // the request is forwarded to presenter
        Mockito.verify(mMockMainPresenter, Mockito.atLeastOnce()).disableBluetooth();
    }


    @Test
    public void onBluetoothOffTest(){
        // given initialized MainViewActivity
        setupMockMainPresenter();

        // when bluetooth turns off
        mClassUnderTest.onBluetoothOff();

        // discovery button is disabled
        Assert.assertFalse(mClassUnderTest.findViewById(R.id.bt_discover).isEnabled());

        // disconnect button is disabled
        Assert.assertFalse(mClassUnderTest.findViewById(R.id.bt_disconnect).isEnabled());

        // send button is disabled
        Assert.assertFalse(mClassUnderTest.findViewById(R.id.bt_send).isEnabled());
    }

    @Test
    public void onBluetoothOnTest(){
        // given initialized MainViewActivity
        setupMockMainPresenter();
        mClassUnderTest.onBluetoothOff();

        // when bluetooth turns off
        mClassUnderTest.onBluetoothOn();

        // discovery button is enabled
        Assert.assertTrue(mClassUnderTest.findViewById(R.id.bt_discover).isEnabled());

        // disconnect button is enabled
        Assert.assertTrue(mClassUnderTest.findViewById(R.id.bt_disconnect).isEnabled());

        // send button is enabled
        Assert.assertTrue(mClassUnderTest.findViewById(R.id.bt_send).isEnabled());
    }


    @Test
    public void disableBluetoothFeaturesTest(){
        // given initialized MainViewActivity
        setupMockMainPresenter();

        // when disableBluetoothFeatures is called
        mClassUnderTest.disableBluetoothFeatures();

        // bluetooth on/off button is disabled
        Assert.assertFalse(mClassUnderTest.findViewById(R.id.bt_on).isEnabled());

        // discovery button is disabled
        Assert.assertFalse(mClassUnderTest.findViewById(R.id.bt_discover).isEnabled());

        // disconnect button is disabled
        Assert.assertFalse(mClassUnderTest.findViewById(R.id.bt_disconnect).isEnabled());

        // send button is disabled
        Assert.assertFalse(mClassUnderTest.findViewById(R.id.bt_send).isEnabled());
    }

    @Test
    public void enableBluetoothFeaturesWhenBluetoothIsConnectedTest() {
        // given initialized MainViewActivity
        setupMockMainPresenter();
        mClassUnderTest.disableBluetoothFeatures();
        PowerMockito.when(mMockMainPresenter.isBluetoothEnabled()).thenReturn(true);

        // when disableBluetoothFeatures is called
        mClassUnderTest.enableBluetoothFeatures();

        // bluetooth on/off button is enabled
        Assert.assertTrue(mClassUnderTest.findViewById(R.id.bt_on).isEnabled());

        // discovery button is enabled
        Assert.assertTrue(mClassUnderTest.findViewById(R.id.bt_discover).isEnabled());

        // disconnect button is enabled
        Assert.assertTrue(mClassUnderTest.findViewById(R.id.bt_disconnect).isEnabled());

        // send button is enabled
        Assert.assertTrue(mClassUnderTest.findViewById(R.id.bt_send).isEnabled());
    }

    @Test
    public void enableBluetoothFeaturesWhenBluetoothIsDisabledTest(){
        // given initialized MainViewActivity
        setupMockMainPresenter();
        mClassUnderTest.disableBluetoothFeatures();
        PowerMockito.when(mMockMainPresenter.isBluetoothEnabled()).thenReturn(false);

        // when enableBluetoothFeatures is called
        mClassUnderTest.enableBluetoothFeatures();

        // bluetooth on/off button is enabled
        Assert.assertTrue(mClassUnderTest.findViewById(R.id.bt_on).isEnabled());

        // discovery button is disabled
        Assert.assertFalse(mClassUnderTest.findViewById(R.id.bt_discover).isEnabled());

        // disconnect button is disabled
        Assert.assertFalse(mClassUnderTest.findViewById(R.id.bt_disconnect).isEnabled());

        // send button is disabled
        Assert.assertFalse(mClassUnderTest.findViewById(R.id.bt_send).isEnabled());
    }


    @Test
    public void enableBluetoothFeaturesWhenBluetoothIsEnabledTest(){
        // given initialized MainViewActivity
        setupMockMainPresenter();
        mClassUnderTest.disableBluetoothFeatures();
        PowerMockito.when(mMockMainPresenter.isBluetoothEnabled()).thenReturn(true);

        // when enableBluetoothFeatures is called
        mClassUnderTest.enableBluetoothFeatures();

        // bluetooth on/off button is enabled
        Assert.assertTrue(mClassUnderTest.findViewById(R.id.bt_on).isEnabled());

        // discovery button is enabled
        Assert.assertTrue(mClassUnderTest.findViewById(R.id.bt_discover).isEnabled());

        // disconnect button is enabled
        Assert.assertTrue(mClassUnderTest.findViewById(R.id.bt_disconnect).isEnabled());

        // send button is enabled
        Assert.assertTrue(mClassUnderTest.findViewById(R.id.bt_send).isEnabled());
    }


    @Test
    public void disconnectButtonTest(){
        // given initialized MainViewActivity
        setupMockMainPresenter();

        // when disconnect button is pressed
        mClassUnderTest.findViewById(R.id.bt_disconnect).performClick();

        // disconnect call is passed to the Model
        Mockito.verify(mMockMainPresenter, Mockito.atLeastOnce()).disconnect();
    }


    @Test
    public void turnOnBluetoothTest(){
        // given initialized MainViewActivity
        setupMockMainPresenter();
        PowerMockito.when(mMockMainPresenter.isBluetoothEnabled()).thenReturn(false);

        // when bluetooth is off and user turns is on
        // by pressing bluetooth on/off button
        mClassUnderTest.findViewById(R.id.bt_on).performClick();

        // bluetooth status is checked
        Mockito.verify(mMockMainPresenter, Mockito.atLeastOnce()).isBluetoothEnabled();

        // the request is passed to presenter
        Mockito.verify(mMockMainPresenter, Mockito.atLeastOnce()).enableBluetooth();

        // the button name is set correctly
        Assert.assertEquals(mClassUnderTest.getString(R.string.button_bt_off), ((Button)mClassUnderTest.findViewById(R.id.bt_on)).getText().toString());
    }

    @Test
    public void turnOffBluetooth(){
        // given initialized MainViewActivity
        setupMockMainPresenter();
        PowerMockito.when(mMockMainPresenter.isBluetoothEnabled()).thenReturn(true);

        // when bluetooth is on and user turns is off
        // by pressing bluetooth on/off button
        mClassUnderTest.findViewById(R.id.bt_on).performClick();

        // bluetooth status is checked
        Mockito.verify(mMockMainPresenter, Mockito.atLeastOnce()).isBluetoothEnabled();

        // the request is passed to presenter
        Mockito.verify(mMockMainPresenter, Mockito.atLeastOnce()).disableBluetooth();

        // the button name is set correctly
        Assert.assertEquals(mClassUnderTest.getString(R.string.button_bt_on), ((Button)mClassUnderTest.findViewById(R.id.bt_on)).getText().toString());
    }

    @Test
    public void mainPresenterInitializationTest(){
        // given initialized MainViewActivity

        // when main view activity is started
        Assert.assertNotNull(mClassUnderTest);

        // main presenter is initialized
        Assert.assertNotNull(Whitebox.getInternalState(mClassUnderTest,mMainPresenterFieldName));
        mMainPresenterFieldName = "mMainPresenter";
        Assert.assertEquals(
                MainPresenter.class.getCanonicalName(),
                Whitebox.getInternalState(mClassUnderTest, mMainPresenterFieldName).getClass().getCanonicalName()
        );
    }

    @Test
    public void mainPresenterLifecycleCleanupTest(){
        // given initialized MainViewActivity and MainPresenter
        createActivityControllerAndMockPresenter();
        Bundle bundle = new Bundle();
        mClassUnderTestController.create().start();
        Whitebox.setInternalState(mClassUnderTestController.get(), mMainPresenterFieldName, mMockMainPresenter);
        mClassUnderTestController.restoreInstanceState(bundle).resume().visible();

        // when onStop() is called
        mClassUnderTestController.saveInstanceState(bundle).pause().stop();

        // MainPresenter.cleanup() is called
        // and mMainPresenter is set to null;
        Mockito.verify(mMockMainPresenter, Mockito.atLeastOnce()).cleanup();
        Assert.assertNull(Whitebox.getInternalState(mClassUnderTestController.get(), mMainPresenterFieldName));
        mClassUnderTestController.destroy();
    }

    @Test
    public void checkBluetoothPermissionsWhenBluetoothIsSupportedTest(){
        // given initialized MainViewActivity and MainPresenter
        createActivityControllerAndMockPresenterInitializedAtOnCreate();
        PowerMockito.when(mMockMainPresenter.isBluetoothSupported()).thenReturn(true);

        // when checking if bluetooth is available
        mClassUnderTestController.start();

        // then permissions are checked
        Mockito.verify(mMockMainPresenter, Mockito.atLeastOnce()).bluetoothPermissionsGranted();
    }

    @Test
    public void bluetoothPermissionsAreNotCheckedWhenBluetoothIsNotSupportedTest(){
        // given initialized MainViewActivity and MainPresenter
        createActivityControllerAndMockPresenterInitializedAtOnCreate();
        PowerMockito.when(mMockMainPresenter.isBluetoothSupported()).thenReturn(false);


        // when checking bluetooth is not supported
        mClassUnderTestController.start();

        // then permissions are NOT checked
        Mockito.verify(mMockMainPresenter, Mockito.never()).bluetoothPermissionsGranted();
    }


    @Test
    public void onRequestedPermissionsGrantedTest(){
        // given initialized MainActivity
        createActivityControllerAndMockPresenterInitializedAtOnCreate();
        PowerMockito.when(mMockMainPresenter.isBluetoothEnabled()).thenReturn(false);
        String[] permissions = new String[4];
        Util.generateNeededBluetoothPermissionsList().toArray(permissions);
        int[] granted = {0,0,0,0}; // 0 is permission granted


        // when requested permissions are granted
        mClassUnderTestController.get()
                .onRequestPermissionsResult(MainViewActivity.BT_PERM_REQ_CODE, permissions, granted);


        // the bluetooth features are in enabled as expected
        // bluetooth on/off button is enabled
        Assert.assertTrue(mClassUnderTestController.get().findViewById(R.id.bt_on).isEnabled());

        // discovery button is disabled
        Assert.assertFalse(mClassUnderTestController.get().findViewById(R.id.bt_discover).isEnabled());

        // disconnect button is disabled
        Assert.assertFalse(mClassUnderTestController.get().findViewById(R.id.bt_disconnect).isEnabled());

        // send button is disabled
        Assert.assertFalse(mClassUnderTestController.get().findViewById(R.id.bt_send).isEnabled());
    }

    @Test
    public void onRequestedPermissionsDeniedTest(){
        // given initialized MainActivity
        createActivityControllerAndMockPresenterInitializedAtOnCreate();
        PowerMockito.when(mMockMainPresenter.isBluetoothEnabled()).thenReturn(false);
        String[] permissions = new String[4];
        Util.generateNeededBluetoothPermissionsList().toArray(permissions);
        int[] granted = {-1,-1,-1,-1}; // 0 is permission denied


        // when requested permissions are granted
        mClassUnderTestController.get()
                .onRequestPermissionsResult(MainViewActivity.BT_PERM_REQ_CODE, permissions, granted);


        // the bluetooth features are disabled
        Assert.assertFalse(mClassUnderTestController.get().findViewById(R.id.bt_on).isEnabled());

        // discovery button is disabled
        Assert.assertFalse(mClassUnderTestController.get().findViewById(R.id.bt_discover).isEnabled());

        // disconnect button is disabled
        Assert.assertFalse(mClassUnderTestController.get().findViewById(R.id.bt_disconnect).isEnabled());

        // send button is disabled
        Assert.assertFalse(mClassUnderTestController.get().findViewById(R.id.bt_send).isEnabled());
    }

    @Test
    public void showDeviceStatusTest(){
        // given initialized MainViewActivity and MainPresenter
        createActivityControllerAndMockPresenterInitializedAtOnCreate();

        // when showDeviceStatus() is called
        mClassUnderTestController.get().showDeviceStatus(TEST_MSG);

        // the status indicator has correct text
        Assert.assertEquals(TEST_MSG, ((TextView)mClassUnderTestController.get().findViewById(R.id.con_status)).getText());
    }


    @Test
    public void setConnectionIndicatorWhenConnectedTest(){
        // given initialized MainViewActivity and MainPresenter
        createActivityControllerAndMockPresenterInitializedAtOnCreate();
        PowerMockito.when(mMockMainPresenter.isConnected()).thenReturn(true);
        mClassUnderTestController.start().resume();

        // when connected and setConnectionIndicator() is called

        // the status indicator has correct text
        Assert.assertEquals(mClassUnderTestController.get().getString(R.string.status_connected),
                ((TextView)mClassUnderTestController.get().findViewById(R.id.con_status)).getText());
    }


    @Test
    public void setConnectionIndicatorWhenDisconnectedTest(){
        // given initialized MainViewActivity and MainPresenter
        createActivityControllerAndMockPresenterInitializedAtOnCreate();
        PowerMockito.when(mMockMainPresenter.isConnected()).thenReturn(false);
        mClassUnderTestController.start().resume();

        // when disconnected and setConnectionIndicator() is called

        // the status indicator has correct text
        Assert.assertEquals(mClassUnderTestController.get().getString(R.string.status_default),
                ((TextView)mClassUnderTestController.get().findViewById(R.id.con_status)).getText());
    }


    @Test
    public void sendTextToRemoteDevice(){
        // given initialized MainViewActivity and MainPresenter
        createActivityControllerAndMockPresenterInitializedAtOnCreate();
        PowerMockito.when(mMockMainPresenter.isConnected()).thenReturn(false);
        mClassUnderTestController.start().resume();
        EditText editText = mClassUnderTestController.get().findViewById(R.id.edit_text);
        editText.setText(TEST_MSG);

        // when send button is pressed
        mClassUnderTestController.get().findViewById(R.id.bt_send).performClick();

        // data from text view is retrieved
        Assert.assertEquals(TEST_MSG,
                Whitebox.getInternalState(mClassUnderTestController.get(), "mTextBuffer"));

        // data is sent to remote device
        Mockito.verify(mMockMainPresenter, Mockito.atLeastOnce()).sendMessageToRemoteDevice(TEST_MSG);
    }


    @Test
    public void joystickThreadStartTets(){
        // given initialized MainViewActivity and MainPresenter
        createActivityControllerAndMockPresenterInitializedAtOnCreate();
        PowerMockito.when(mMockMainPresenter.isConnected()).thenReturn(false);

        // when activity is resumed
        mClassUnderTestController.start().resume();

        // joystick thread is not null and alive
        JoystickHandlerThread joystickHandlerThread =
                Whitebox.getInternalState(mClassUnderTestController.get(), "mJoystickThread");

        Assert.assertNotNull(joystickHandlerThread);
        Assert.assertTrue(joystickHandlerThread.isAlive());

        //when activity is paused
        mClassUnderTestController.pause();
        mClassUnderTestController.stop();
    }

    @Test
    public void joystickThreadStopTest() throws InterruptedException {
        // given initialized MainViewActivity and MainPresenter
        createActivityControllerAndMockPresenterInitializedAtOnCreate();
        PowerMockito.when(mMockMainPresenter.isConnected()).thenReturn(false);

        // when activity is paused
        mClassUnderTestController.start().resume().pause();
        // give enough time to clean up
        Thread.sleep(200);

        // joystick thread is not null and stopped
        JoystickHandlerThread joystickHandlerThread =
                Whitebox.getInternalState(mClassUnderTestController.get(), "mJoystickThread");
        Assert.assertNotNull(joystickHandlerThread);
        Assert.assertFalse(joystickHandlerThread.isAlive());
        mClassUnderTestController.stop();
    }

}
