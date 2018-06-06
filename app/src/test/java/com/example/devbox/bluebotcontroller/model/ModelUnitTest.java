package com.example.devbox.bluebotcontroller.model;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import com.example.devbox.bluebotcontroller.presenter.IMainPresenter;
import com.example.devbox.bluebotcontroller.presenter.MainPresenter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;

import static org.mockito.Mockito.verify;


@RunWith(PowerMockRunner.class)
@PrepareForTest({BluetoothAdapter.class})
public class ModelUnitTest {

    private final String mTestString = "Test String";

    private IModel sModelUnderTest;
    private Context mMockContext;
    private IMainPresenter mMockMainPresenter;



    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setup(){
        mMockContext = PowerMockito.mock(Context.class);
        mMockMainPresenter = PowerMockito.mock(MainPresenter.class);

        BluetoothAdapter mockBluetoothAdapter = PowerMockito.mock(BluetoothAdapter.class);
        PowerMockito.mockStatic(BluetoothAdapter.class);
        PowerMockito.when(BluetoothAdapter.getDefaultAdapter()).thenReturn(mockBluetoothAdapter);


        sModelUnderTest = Model.getInstance(mMockContext, mMockMainPresenter);

    }


    @Test
    public void notifyMainPresenterTest(){
        sModelUnderTest.notifyMainPresenter(mTestString);
        verify(mMockMainPresenter).sendMessageToUI(mTestString);
    }

    @Test
    public void updateDeviceStatus(){

        sModelUnderTest.updateDeviceStatus(mTestString);
        verify(mMockMainPresenter).updateDeviceStatus(mTestString);
    }



}