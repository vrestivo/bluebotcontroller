package com.example.devbox.bluebotcontroller.presenter;

import com.example.devbox.bluebotcontroller.model.Model;
import com.example.devbox.bluebotcontroller.presenter.IMainPresenter;
import com.example.devbox.bluebotcontroller.presenter.MainPresenter;
import com.example.devbox.bluebotcontroller.view.IMainView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;

import com.example.devbox.bluebotcontroller.model.IModel;
import com.example.devbox.bluebotcontroller.view.MainViewActivity;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

public class MainPresenterTest {

    private String mTestString = "Test Message";
    private IMainPresenter mMainPresenter;

    private IMainView mMainView;
    private IModel mModel;


    @Before
    public void setup(){
        mMainView = PowerMockito.mock(MainViewActivity.class);
        mModel = PowerMockito.mock(Model.class);
        mMainPresenter = new MainPresenter(mMainView, mModel);
    }


    @Test
    public void showMessageToUserTest(){
        //given initialized view, presenter, and model
        //when sendMessageToUI() is called
        mMainPresenter.sendMessageToUI(mTestString);
        //the call is propagated to main view
        verify(mMainView).showMessage(mTestString);
    }


    @Test
    public void mainPresenterCleanupTest(){
        //given initialized presenter
        //when cleanup is called
        mMainPresenter.cleanup();

        //IModel.cleanup() is invoked
        verify(mModel).cleanup();
    }


    @Test
    public void sendMessageToRemoteDeviceTest(){
        //given initialized view, presenter, and model
        //when view send message to bluetooth
        mMainPresenter.sendMessageToRemoteDevice(mTestString);

        //it is propagated all the way to the model
        verify(mModel).sendMessageToRemoteDevice(mTestString);
    }


    @Test
    public void updateDeviceStatusTest(){
        //given initialized view, presenter, and model
        //when updateDeviceStatus() is called
        mMainPresenter.updateDeviceStatus(mTestString);

        //the call is propagated to MainView
        verify(mMainView).showDeviceStatus(mTestString);
    }


    @Test
    public void disconnectTest(){
        //given initialized view, presenter, and model
        //when disconnect() is called
        mMainPresenter.disconnect();

        verify(mModel).disconnect();
    }


    @Test
    public void disableBluetoothFeaturesTest(){
        //given initialized view, presenter, and model
        //when disableBluetoothFeatures() is called
        mMainPresenter.disableBluetoothFeatures();

        //the call is propagated to the view
        verify(mMainView, atLeastOnce()).disableBluetoothFeatures();
    }

}
