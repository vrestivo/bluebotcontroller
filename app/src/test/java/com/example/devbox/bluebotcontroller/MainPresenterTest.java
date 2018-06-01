package com.example.devbox.bluebotcontroller;

import com.example.devbox.bluebotcontroller.presenter.IMainPresenter;
import com.example.devbox.bluebotcontroller.presenter.MainPresenter;
import com.example.devbox.bluebotcontroller.view.IMainView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.example.devbox.bluebotcontroller.model.IModel;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class MainPresenterTest {

    private String mTestString = "Test Message";
    private IMainPresenter mMainPresenter;

    @Mock
    private IMainView mMainView;
    @Mock
    private IModel mModel;


    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();


    @Before
    public void setup(){
        reset(mMainView);
        reset(mModel);
        mMainPresenter = new MainPresenter(mMainView, mModel);
    }

    @Test
    public void showMessageToUserTest(){
        //given intialized view, presenter, and model
        //when sendMessageToUI() is called
        mMainPresenter.sendMessageToUI(mTestString);
        //the call is propagatd to main view
        verify(mMainView).showMessage(mTestString);
    }


    @Test
    public void mainPresenterCleaupTest(){
        //given initialized presenter
        //when cleanup is called
        mMainPresenter.cleanup();

        //IModel.cleaup() is invoked
        verify(mModel).cleanup();
    }

    @Test
    public void sendMessageToRemoteDeviceTest(){
        //given intialized view, presenter, and model
        //when view send message to bluetooth
        mMainPresenter.sendMessageToRemoteDevice(mTestString);

        //it is propagated all the way to the model
        verify(mModel).sendMessageToRemoteDevice(mTestString);
    }


    @Test
    public void updateDeviceStatusTest(){
        //given intialized view, presenter, and model
        //when updateDeviceStatus() is called
        mMainPresenter.updateDeviceStatus(mTestString);

        //the call is propagated to MainView
        verify(mMainView).showDeviceStatus(mTestString);
    }

    @Test
    public void disconnectTest(){
        //given intialized view, presenter, and model
        //when disconnect() is called
        mMainPresenter.disconnect();

        verify(mModel).disconnect();
    }

}
