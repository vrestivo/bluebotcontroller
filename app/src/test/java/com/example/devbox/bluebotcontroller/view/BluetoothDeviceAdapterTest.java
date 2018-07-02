package com.example.devbox.bluebotcontroller.view;


import android.bluetooth.BluetoothDevice;

import com.example.devbox.bluebotcontroller.BuildConfig;
import com.example.devbox.bluebotcontroller.TestObjectGenerator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class BluetoothDeviceAdapterTest {

    private BluetoothDeviceAdapter mClassUnderTest;
    private BluetoothDeviceAdapter.OnDeviceSelected mMockCallback;


    @Before
    public void testSetup(){

    }

    private void initializeAdapterWithMockCallback() {
        mMockCallback = PowerMockito.mock(BluetoothDeviceAdapter.OnDeviceSelected.class);
        mClassUnderTest = new BluetoothDeviceAdapter(mMockCallback);
    }

    private void initializeAdapterWithMockCallbackAndOneDevice() {
        mMockCallback = PowerMockito.mock(BluetoothDeviceAdapter.OnDeviceSelected.class);
        mClassUnderTest = new BluetoothDeviceAdapter(mMockCallback);
        Set<BluetoothDevice> singleDeviceSet = new HashSet<>();
        singleDeviceSet.add(TestObjectGenerator.generateASingleMockBluetoothDevice());
        mClassUnderTest.updateDeviceDataSet(singleDeviceSet);
        Assert.assertEquals(singleDeviceSet.size(), mClassUnderTest.getItemCount());
    }


    @Test
    public void initializationTest(){
        // given initialized adapter with mock callback
        initializeAdapterWithMockCallback();

        // when in the initial state

        // the list in adapter should not be null
        List<BluetoothDevice> deviceList = Whitebox.getInternalState(mClassUnderTest, "mDeviceList");
        Assert.assertNotNull(deviceList);

        // item count should be zero
        Assert.assertEquals(0, mClassUnderTest.getItemCount());
    }

    @Test
    public void updateDataSetTest(){
        // given initialized adapter with mock callback
        // and a single mock device
        initializeAdapterWithMockCallbackAndOneDevice();
        Set<BluetoothDevice> mockDevices = TestObjectGenerator.generateMockBluetoothDevices();

        // when the list is updated
        mClassUnderTest.updateDeviceDataSet(mockDevices);

        // count reflects the new element amount
        Assert.assertEquals(mockDevices.size(), mClassUnderTest.getItemCount());

        // all elements are present in the new list
        List<BluetoothDevice> devicesUnderTest = Whitebox.getInternalState(mClassUnderTest, "mDeviceList");
        for(BluetoothDevice device: mockDevices){
            Assert.assertTrue(devicesUnderTest.contains(device));
        }
    }

}
