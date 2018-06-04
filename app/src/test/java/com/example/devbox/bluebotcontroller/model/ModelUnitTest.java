package com.example.devbox.bluebotcontroller.model;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import com.example.devbox.bluebotcontroller.model.IModel;
import com.example.devbox.bluebotcontroller.model.Model;
import com.example.devbox.bluebotcontroller.presenter.IMainPresenter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;

import static org.mockito.Mockito.verify;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ModelUnitTest {


    private IModel sModelUnderTest;

    private String mTestString = "Test String";

    private List<String> mFakeData;

    private int mCoutner;

    private Flowable<String> mFlowable;
    private Disposable mDisposable;

    @Mock
    private Context mockContext;
    @Mock
    private IMainPresenter mockMainPresenter;
    @Mock
    private BluetoothAdapter mockBluetoothAdapter;

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setup(){
        sModelUnderTest = Model.getInstance(mockContext, mockMainPresenter);
        mFakeData = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            mFakeData.add(String.valueOf(i));
        }
    }


    @Test
    public void notifyMainPresenterTest(){
        sModelUnderTest.notifyMainPresenter(mTestString);
        verify(mockMainPresenter).sendMessageToUI(mTestString);
    }

    @Test
    public void updateDeviceStatus(){
        sModelUnderTest.updateDeviceStatus(mTestString);
        verify(mockMainPresenter).updateDeviceStatus(mTestString);
    }

    @Test
    public void flowableTest(){
        FlowableEmitter<String> emitter = new FlowableEmitter<String>() {
            @Override
            public void setDisposable(Disposable s) {

            }

            @Override
            public void setCancellable(Cancellable c) {

            }

            @Override
            public long requested() {
                return 0;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public FlowableEmitter<String> serialize() {
                return null;
            }

            @Override
            public boolean tryOnError(Throwable t) {
                return false;
            }

            @Override
            public void onNext(String value) {

            }

            @Override
            public void onError(Throwable error) {

            }

            @Override
            public void onComplete() {

            }
        };

    }

    public void callMe(String param){
        System.out.println("_in callMe(): " + param);
    }



}