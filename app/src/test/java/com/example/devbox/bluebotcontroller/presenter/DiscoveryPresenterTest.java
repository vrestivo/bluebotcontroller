package com.example.devbox.bluebotcontroller.presenter;


import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.example.devbox.bluebotcontroller.TestObjectGenerator;
import com.example.devbox.bluebotcontroller.model.IModel;
import com.example.devbox.bluebotcontroller.model.Model;
import com.example.devbox.bluebotcontroller.view.DiscoveryViewActivity;
import com.example.devbox.bluebotcontroller.view.IDiscoveryView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.Set;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DiscoveryViewActivity.class, Context.class, Model.class})
public class DiscoveryPresenterTest {

    private final String TEST_STRING = "TEST_STRING";
    private DiscoveryPresenter mClassUnderTest;
    private DiscoveryViewActivity mMockDiscoveryViewActivity;
    private Context mMockContext;
    private Model mMockModel;


    @Before
    public void testSetup() {
        PowerMockito.mockStatic(Model.class);
        mMockDiscoveryViewActivity = PowerMockito.mock(DiscoveryViewActivity.class);
        mMockContext = PowerMockito.mock(Context.class);
        mClassUnderTest = new DiscoveryPresenter(mMockDiscoveryViewActivity, mMockContext);
    }


    private void insertMockModelAndVerifyInsertion(){
        mMockModel = PowerMockito.mock(Model.class);
        Assert.assertNotNull(mMockModel);

        Whitebox.setInternalState(mClassUnderTest, "mModel", mMockModel);
        Model sameModel = Whitebox.getInternalState(mClassUnderTest, "mModel");
        Assert.assertSame(mMockModel, sameModel);
    }


    @Test
    public void InitializationTest() {
        // given DiscoveryPresenter is initialized

        // discovery view has been passed to presenter
        DiscoveryViewActivity viewToTest = Whitebox.getInternalState(mClassUnderTest, "mDiscoveryView");
        Assert.assertSame(mMockDiscoveryViewActivity, viewToTest);

        // and model was initialized
        PowerMockito.verifyStatic();
        Model.getInstance(Matchers.anyObject(), Matchers.isA(IDiscoveryPresenter.class));
        PowerMockito.verifyNoMoreInteractions(Model.class);
    }


    @Test
    public void onBluetoothOffTest() {
        // given initialized discovery presenter

        // when onBluetoothOff() is called
        mClassUnderTest.onBluetoothOff();

        // the call is propagated to the discovery view
        Mockito.verify(mMockDiscoveryViewActivity, Mockito.only()).onBluetoothOff();
    }


    @Test
    public void lifeCycleCleanupTest() {
        // given initialized discovery presenter
        insertMockModelAndVerifyInsertion();

        // when liceCycleCleanup() is called
        mClassUnderTest.lifecycleCleanup();

        //then view and model are set to null
        IDiscoveryView memberViewMustBeNull = Whitebox.getInternalState(mClassUnderTest, "mDiscoveryView");
        Assert.assertNull(memberViewMustBeNull);
        IModel memberModelMustBeNull = Whitebox.getInternalState(mClassUnderTest, "mModel");
        Assert.assertNull(memberModelMustBeNull);
    }


    @Test
    public void scanForDevicesTest() {
        // given initialized discovery presenter
        insertMockModelAndVerifyInsertion();

        // when bluetooth scan is initiated
        mClassUnderTest.scanForDevices();

        // the call is passed to the model
        Mockito.verify(mMockModel, Mockito.atLeastOnce()).scanForDevices();
    }


    @Test
    public void sendMessageToUITest() {
        // given initialized discovery presenter

        // when message is sent to UI
        mClassUnderTest.sendMessageToUI(TEST_STRING);

        //then
        Mockito.verify(mMockDiscoveryViewActivity, Mockito.atLeastOnce()).displayMessage(TEST_STRING);
    }


    @Test
    public void onDeviceSelectedTest() {
        // given initialized discovery presenter
        insertMockModelAndVerifyInsertion();
        BluetoothDevice mockDevice = PowerMockito.mock(BluetoothDevice.class);

        // when device is selected
        mClassUnderTest.onDeviceSelected(mockDevice);

        // the selection is passed to the model
        Mockito.verify(mMockModel, Mockito.atLeastOnce()).connectToDevice(mockDevice);
    }


    @Test
    public void loadPairedDevicesTest() {
        // given initialized discovery presenter
        Set<BluetoothDevice> mockDeviceSet = TestObjectGenerator.generateMockBluetoothDevices();

        // when loading paired devices
        mClassUnderTest.loadPairedDevices(mockDeviceSet);

        // the devices are passed to the discovery view
        Mockito.verify(mMockDiscoveryViewActivity, Mockito.atLeastOnce()).loadPairedDevices(mockDeviceSet);
    }


    @Test
    public void loadAvailableDevicesTest() {
        // given initialized discovery presenter
        Set<BluetoothDevice> mockDeviceSet = TestObjectGenerator.generateMockBluetoothDevices();

        // when loading available devices
        mClassUnderTest.loadAvailableDevices(mockDeviceSet);

        // the devices are passed to the discovery view
        Mockito.verify(mMockDiscoveryViewActivity, Mockito.atLeastOnce()).loadAvailableDevices(mockDeviceSet);
    }


    @Test
    public void getKnownDevicesTest(){
        // given initialized view, presenter, and model
        insertMockModelAndVerifyInsertion();

        // when known devices are request by the view
        mClassUnderTest.getKnownDevices();

        // the request is passed to the Model
        Mockito.verify(mMockModel, Mockito.atLeastOnce()).getKnownDevices();
    }


    @Test
    public void testName() {
        // given initialized discovery presenter

        // when

        //then
    }


}
