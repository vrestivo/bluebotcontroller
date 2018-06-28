package com.example.devbox.bluebotcontroller.presenter;

import com.example.devbox.bluebotcontroller.model.Model;
import com.example.devbox.bluebotcontroller.view.IMainView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import com.example.devbox.bluebotcontroller.model.IModel;
import com.example.devbox.bluebotcontroller.view.MainViewActivity;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class MainPresenterTest {

    private String mTestString = "Test Message";
    private IMainPresenter mClassUnderTest;

    private IMainView mMainView;
    private IModel mModel;


    @Before
    public void setup(){
        mMainView = PowerMockito.mock(MainViewActivity.class);
        mModel = PowerMockito.mock(Model.class);
        mClassUnderTest = new MainPresenter(mMainView, mModel);
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
        verify(mModel).cleanup();
    }


    @Test
    public void sendMessageToRemoteDeviceTest(){
        // given initialized view, presenter, and model
        // when view send message to bluetooth
        mClassUnderTest.sendMessageToRemoteDevice(mTestString);

        // it is propagated all the way to the model
        verify(mModel).sendMessageToRemoteDevice(mTestString);
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

        verify(mModel).disconnect();
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
    public void onBluetoothOnTest(){
        // given initialized view, presenter, and model
        // when when bluetooth is turned on
        mClassUnderTest.onBluetoothOn();

        // the event is propagated to the main presenter
        Mockito.verify(mMainView, atLeastOnce()).onBluetoothOn();
    }


    @Test
    public void onBluetoothOffTest(){
        // given initialized view, presenter, and model
        // when when bluetooth is turned on
        mClassUnderTest.onBluetoothOff();

        // the event is propagated to the main presenter
        Mockito.verify(mMainView, atLeastOnce()).onBluetoothOff();
    }


    @Test
    public void verifyBluetoothSupportTest(){
        // given initialized view, presenter, and model

        // when asked to verify Bluetooth support
        mClassUnderTest.verifyBluetoothSupport();

        // the request is passed to Model
        verify(mModel, atLeastOnce()).verifyBluetoothSupport();
    }


    @Test
    public void checkBluetoothPermissionsTest(){
        // given initialized view, presenter, and model

        // when asked to verify permissions
        mClassUnderTest.checkBluetoothPermissions();

        //the call is propagated to Model layer
        verify(mModel, atLeastOnce()).checkBluetoothPermissions();
    }


}
