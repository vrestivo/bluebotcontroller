package com.example.devbox.bluebotcontroller.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public class RxJavaTest {


    private PublishSubject<String> mSendPublishSubject;
    private PublishSubject<String> mReceivePublishSubject;
    private Disposable mReceiverDisposable;
    private Disposable mSendDisposable;



    @Mock
    private IModel mMockModel;


    @Mock
    private BluetoothSocket mMockBluetoothSocket;
    @Mock
    private BluetoothAdapter mMockBluetoothAdapter;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Test
    public void testSchedulerTest(){

    }



}
