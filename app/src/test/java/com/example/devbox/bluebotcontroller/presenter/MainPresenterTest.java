package com.example.devbox.bluebotcontroller.presenter;

import android.content.Context;
import android.os.Process;

import com.example.devbox.bluebotcontroller.model.BluetoothBroadcastReceiver;
import com.example.devbox.bluebotcontroller.model.Model;
import com.example.devbox.bluebotcontroller.view.IMainView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.example.devbox.bluebotcontroller.model.IModel;
import com.example.devbox.bluebotcontroller.view.MainViewActivity;

import junit.framework.Assert;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Model.class, Process.class})
@SuppressStaticInitializationFor("com/example/devbox/bluebotcontroller/model/Model.java")
public class MainPresenterTest {

    private String mTestString = "Test Message";
    private IMainPresenter mClassUnderTest;

    private IMainView mMainView;
    private IModel mMockModel;


    @Before
    public void setup() throws Exception {
        PowerMockito.mockStatic(Process.class);
        PowerMockito.mockStatic(BluetoothBroadcastReceiver.class);
        PowerMockito.mockStatic(Model.class);
        Context mockContext = PowerMockito.mock(Context.class);
        mMainView = PowerMockito.mock(MainViewActivity.class);
        mClassUnderTest = new MainPresenter(mMainView, mockContext);

        mMockModel = PowerMockito.mock(Model.class);
        Whitebox.setInternalState(mClassUnderTest, "mModel", mMockModel);
    }


    @Test
    public void showMessageToUserTest(){
        // given initialized view, presenter, and model
        // when sendMessageToUI() is called
        mClassUnderTest.sendMessageToUI(mTestString);
        // the call is propagated to main view
        verify(mMainView).showMessage(mTestString);
    }


    @Test
    public void mainPresenterCleanupTest(){
        // given initialized presenter
        // when cleanup is called
        mClassUnderTest.cleanup();

        // IModel.cleanup() is invoked
        verify(mMockModel).cleanup();
    }


    @Test
    public void sendMessageToRemoteDeviceTest(){
        // given initialized view, presenter, and model
        // when view send message to bluetooth
        mClassUnderTest.sendMessageToRemoteDevice(mTestString);

        // it is propagated all the way to the model
        verify(mMockModel).sendMessageToRemoteDevice(mTestString);
    }


    @Test
    public void updateDeviceStatusTest(){
        // given initialized view, presenter, and model
        // when updateDeviceStatus() is called
        mClassUnderTest.updateDeviceStatus(mTestString);

        // the call is propagated to MainView
        verify(mMainView).showDeviceStatus(mTestString);
    }


    @Test
    public void disconnectTest(){
        // given initialized view, presenter, and model
        // when disconnect() is called
        mClassUnderTest.disconnect();

        verify(mMockModel).disconnect();
    }


    @Test
    public void disableBluetoothFeaturesTest(){
        // given initialized view, presenter, and model
        // when disableBluetoothFeatures() is called
        mClassUnderTest.disableBluetoothFeatures();

        // the call is propagated to the view
        verify(mMainView, atLeastOnce()).disableBluetoothFeatures();
    }


    @Test
    public void enableBluetoothTest(){
        // given initialized view, presenter, and model
        // when user requests to turn on bluetooth
        mClassUnderTest.enableBluetooth();

        // the request is passed to model layer
        verify(mMockModel, atLeastOnce()).enableBluetooth();
    }


    @Test
    public void disableBluetoothTest(){
        // given initialized view, presenter, and model
        // when user requests to turn on bluetooth
        mClassUnderTest.disableBluetooth();

        // the request is passed to model layer
        verify(mMockModel, atLeastOnce()).disableBluetooth();
    }


    @Test
    public void onBluetoothOnTest(){
        // given initialized view, presenter, and model
        // when onBluetoothOn() is called
        mClassUnderTest.onBluetoothOn();

        // the event is propagated to the main presenter
        Mockito.verify(mMainView, atLeastOnce()).onBluetoothOn();
    }


    @Test
    public void onBluetoothOffTest(){
        // given initialized view, presenter, and model
        // when onBluetoothOff() is called
        mClassUnderTest.onBluetoothOff();

        // the event is propagated to the main presenter
        Mockito.verify(mMainView, atLeastOnce()).onBluetoothOff();
    }


    @Test
    public void isBluetoothEnabledTest(){
        // given initialized view, presenter, and model

        // when checking if bluetooth is enables
        mClassUnderTest.isBluetoothEnabled();

        // call is passed to model
        verify(mMockModel, atLeastOnce()).isBluetoothEnabled();
    }


    @Test
    public void verifyBluetoothSupportTest(){
        // given initialized view, presenter, and model

        // when asked to verify Bluetooth support
        mClassUnderTest.verifyBluetoothSupport();

        // the request is passed to Model
        verify(mMockModel, atLeastOnce()).verifyBluetoothSupport();
    }


    @Test
    public void checkBluetoothPermissionsTest(){
        // given initialized view, presenter, and model
        PowerMockito.mockStatic(Process.class);
        PowerMockito.when(Process.myPid()).thenReturn(1);

        // when asked to verify permissions
        mClassUnderTest.checkBluetoothPermissions();

        //the call is propagated to Model layer
        verify(mMockModel, atLeastOnce()).checkBluetoothPermissions();
    }

    @Test
    public void requestBluetoothPermissionsTest(){
        // given initialized view, presenter, and model

        // when Models requests permissions
        mClassUnderTest.requestBluetoothPermissions();

        // the call is propagated to the View layer
        verify(mMainView, atLeastOnce()).requestBluetoothPermissions();
    }

    @Test
    public void isBluetoothSupportedTest(){
        // given initialized view, presenter, and model

        // when isBluetoothSupported() is called
        mClassUnderTest.isBluetoothSupported();

        // the call is propagated to the Model
        verify(mMockModel, atLeastOnce()).isBluetoothSupported();
    }

    @Test
    public void isBluetoothSupportedModelReturnsFalseTest(){
        // given initialized view, presenter, and model
        PowerMockito.when(mMockModel.isBluetoothSupported()).thenReturn(false);

        // when isBluetoothSupported() is called
        boolean result = mClassUnderTest.isBluetoothSupported();

        // correct values are passed
        Assert.assertFalse(result);
    }

    @Test
    public void isBluetoothSupportedModelReturnsTrueTest(){
        // given initialized view, presenter, and model
        PowerMockito.when(mMockModel.isBluetoothSupported()).thenReturn(true);

        // when isBluetoothSupported() is called
        boolean result = mClassUnderTest.isBluetoothSupported();

        // correct values are passed
        Assert.assertTrue(result);
    }

    @Test
    public void isBluetoothSupportedOnNullModelReturnsFalseTest(){
        // given initialized view, presenter, and model
        mMockModel = null; // avoids method abmiguity in setInternalState();
        Whitebox.setInternalState(mClassUnderTest, "mModel", mMockModel);

        // when isBluetoothSupported() is called
        boolean result = mClassUnderTest.isBluetoothSupported();

        // correct values are passed
        Assert.assertFalse(result);
    }

}
