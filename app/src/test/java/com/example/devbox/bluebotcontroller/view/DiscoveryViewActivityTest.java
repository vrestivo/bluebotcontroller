package com.example.devbox.bluebotcontroller.view;


import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.widget.Toast;

import com.example.devbox.bluebotcontroller.BuildConfig;
import com.example.devbox.bluebotcontroller.R;
import com.example.devbox.bluebotcontroller.TestObjectGenerator;
import com.example.devbox.bluebotcontroller.presenter.DiscoveryPresenter;
import com.example.devbox.bluebotcontroller.view.discovery.BluetoothDeviceListAdapter;
import com.example.devbox.bluebotcontroller.view.discovery.DiscoveryViewActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import java.util.Set;

@Config(constants = BuildConfig.class)
@PrepareForTest({Toast.class})
@RunWith(RobolectricTestRunner.class)
public class DiscoveryViewActivityTest {

    private final String PRESENTER_NAME = "mDiscoveryPresenter";
    private final String MSG_TEST = "TEST";

    private ActivityController<DiscoveryViewActivity> mClassUnderTest;
    private DiscoveryPresenter mMockPresenter;


    @Before
    public void testSetup(){

    }

    private void initializeDiscoveryViewActivityWithMockPresenter(){
        mMockPresenter = PowerMockito.mock(DiscoveryPresenter.class);
        mClassUnderTest = Robolectric.buildActivity(DiscoveryViewActivity.class);
        mClassUnderTest.create().restoreInstanceState(getNewBundle()).start();

        Whitebox.setInternalState(mClassUnderTest.get(), PRESENTER_NAME, mMockPresenter);

        //presenter is initialized
        validateMockPresenterInitialization();
    }

    private Bundle getNewBundle(){
        return new Bundle();
    }

    private void validateMockPresenterInitialization(){
        DiscoveryPresenter discoveryPresenter = Whitebox.getInternalState(mClassUnderTest.get(), PRESENTER_NAME);
        Assert.assertNotNull("Error: in setting mock presenter during initialization ", discoveryPresenter);
    }

    @Test
    public void presenterInitializationTest(){
        //given class under test
        DiscoveryViewActivity classUnderTest;

        //when class is initialized
        mClassUnderTest = Robolectric.buildActivity(DiscoveryViewActivity.class);
        mClassUnderTest.create().restoreInstanceState(getNewBundle()).start();

        //presenter is initialized
        DiscoveryPresenter discoveryPresenter = Whitebox.getInternalState(mClassUnderTest.get(), PRESENTER_NAME);
        Assert.assertNotNull(discoveryPresenter);
        Assert.assertEquals(DiscoveryPresenter.class.getCanonicalName(), discoveryPresenter.getClass().getCanonicalName());
    }


    @Test
    public void lifecyclePresenterCleanupTest(){
        // given initialized DiscoveryViewActivity class
        initializeDiscoveryViewActivityWithMockPresenter();

        // when onStop is called
        mClassUnderTest.stop();

        // presenter.cleanup is called
        Mockito.verify(mMockPresenter, Mockito.atLeastOnce()).lifecycleCleanup();

        // presenter is set to null
        DiscoveryPresenter discoveryPresenter = Whitebox.getInternalState(mClassUnderTest.get(), PRESENTER_NAME);
        Assert.assertNull(discoveryPresenter);
    }

    @Test
    public void deviceListArePresentTest(){
        // given initialized DiscoveryViewActivity class
        initializeDiscoveryViewActivityWithMockPresenter();

        // when initialization is complete

        // all expected device lists are present
        Assert.assertNotNull(mClassUnderTest.get().findViewById(R.id.devices_paired_rv));
        Assert.assertNotNull(mClassUnderTest.get().findViewById(R.id.devices_available_rv));
    }

    @Test
    public void getKnownDevicesTest(){
        // given initialized DiscoveryViewActivity class
        initializeDiscoveryViewActivityWithMockPresenter();

        // when known devices are requested
        mClassUnderTest.get().getKnownDevices();

        // the request is passed to presenter layer
        Mockito.verify(mMockPresenter, Mockito.atLeastOnce()).getKnownDevices();
    }


    @Test
    public void scanForDevicesTest(){
        // given initialized DiscoveryViewActivity class
        initializeDiscoveryViewActivityWithMockPresenter();

        // when device scan is requested
        mClassUnderTest.get().scanForDevices();

        // the request is passed to the presenter
        Mockito.verify(mMockPresenter, Mockito.atLeastOnce()).scanForDevices();
    }


    @Test
    public void displayMessageTest(){
        // given initialized DiscoveryViewActivity class
        initializeDiscoveryViewActivityWithMockPresenter();

        // when displayMessage() is called
        mClassUnderTest.get().displayMessage(MSG_TEST);

        // toast was shown with the same message
        Assert.assertEquals(MSG_TEST, ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void onBluetoothOffTest(){
        // given initialized DiscoveryViewActivity class
        initializeDiscoveryViewActivityWithMockPresenter();

        // when bluetooth is turned off
        mClassUnderTest.get().onBluetoothOff();

        // the scan button is disabled
        Assert.assertFalse(mClassUnderTest.get().findViewById(R.id.button_scan).isEnabled());
    }

    @Test
    public void scanForDevicesButtonTest(){
        // given initialized DiscoveryViewActivity class
        initializeDiscoveryViewActivityWithMockPresenter();

        // when scan for devices is clicket
        mClassUnderTest.get().findViewById(R.id.button_scan).performClick();

        // scan action is passed to presenter
        Mockito.verify(mMockPresenter, Mockito.atLeastOnce()).scanForDevices();
    }

    @Test
    public void isBluetoothEnabledTest(){
        // given initialized DiscoveryViewActivity class
        initializeDiscoveryViewActivityWithMockPresenter();

        // when isBluetoothEnabledIsCalled(0
        mClassUnderTest.get().isBluetoothEnabled();

        // the call is propagated to presenter
        Mockito.verify(mMockPresenter, Mockito.atLeastOnce()).isBluetoothEnabled();
    }


    @Test
    public void onStartDisableScanButtonIfBluetoothIsDisabled(){
        // given initialized DiscoveryViewActivity class
        initializeDiscoveryViewActivityWithMockPresenter();
        PowerMockito.when(mMockPresenter.isBluetoothEnabled()).thenReturn(false);

        // when bluetooth is disabled in
        mClassUnderTest.resume();

        // then scan features are disabled
        Assert.assertFalse(mClassUnderTest.get().findViewById(R.id.button_scan).isEnabled());
    }

    @Test
    public void pairedDevicesInitializationTest(){
        // given initialized DiscoveryViewActivity class
        initializeDiscoveryViewActivityWithMockPresenter();

        // when activity is inflated

        // paired devices recycler view is present
        Assert.assertNotNull(mClassUnderTest.get().findViewById(R.id.devices_paired_rv));

        // paired devices adapter is not null
        BluetoothDeviceListAdapter adapter = Whitebox.getInternalState(mClassUnderTest.get(), "mPairedDevicesAdapter");
        Assert.assertNotNull(adapter);

        // adapter returns device count of zero
        Assert.assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void paredDevicesUpdateDeviceListTest(){
        // given initialized DiscoveryViewActivity class
        initializeDiscoveryViewActivityWithMockPresenter();
        Set<BluetoothDevice> mockDeviceSet = TestObjectGenerator.generateMockBluetoothDevices();

        // when loadPairedDevices() is called
        mClassUnderTest.get().loadPairedDevices(mockDeviceSet);

        // device count is correct
        BluetoothDeviceListAdapter adapter = Whitebox.getInternalState(mClassUnderTest.get(), "mPairedDevicesAdapter");
        Assert.assertEquals(mockDeviceSet.size(), adapter.getItemCount());
    }

    @Test
    public void availableDevicesInitializationTest(){
        // given initialized DiscoveryViewActivity class
        initializeDiscoveryViewActivityWithMockPresenter();

        // when activity is inflated

        // available devices recycler view is present
        Assert.assertNotNull(mClassUnderTest.get().findViewById(R.id.devices_available_rv));

        // available devices adapter is not null
        BluetoothDeviceListAdapter adapter = Whitebox.getInternalState(mClassUnderTest.get(), "mAvailableDevicesAdapter");
        Assert.assertNotNull(adapter);

        // adapter returns device count of zero
        Assert.assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void availableDevicesUpdateDeviceListTest(){
        // given initialized DiscoveryViewActivity class
        initializeDiscoveryViewActivityWithMockPresenter();
        Set<BluetoothDevice> mockDeviceSet = TestObjectGenerator.generateMockBluetoothDevices();

        // when loadAvailableDevices() is called
        mClassUnderTest.get().loadAvailableDevices(mockDeviceSet);

        // device count is correct
        BluetoothDeviceListAdapter adapter = Whitebox.getInternalState(mClassUnderTest.get(), "mAvailableDevicesAdapter");
        Assert.assertEquals(mockDeviceSet.size(), adapter.getItemCount());
    }


}
