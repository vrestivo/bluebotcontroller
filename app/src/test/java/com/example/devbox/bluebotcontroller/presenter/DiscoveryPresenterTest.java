package com.example.devbox.bluebotcontroller.presenter;


import android.content.Context;

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

@RunWith(PowerMockRunner.class)
@PrepareForTest({DiscoveryViewActivity.class, Context.class, Model.class})
public class DiscoveryPresenterTest {


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


    @Test
    public void InitializationTest() {
        // given all needed components

        // DiscoveryPresenter is initialized

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
        mMockModel = PowerMockito.mock(Model.class);
        Assert.assertNotNull(mMockModel);

        Whitebox.setInternalState(mClassUnderTest, "mModel", mMockModel);
        Model sameModel = Whitebox.getInternalState(mClassUnderTest, "mModel");
        Assert.assertSame(mMockModel, sameModel);

        // when liceCycleCleanup() is called
        mClassUnderTest.lifecycleCleanup();

        //then view and model are set to null
        IDiscoveryView memberViewMustBeNull = Whitebox.getInternalState(mClassUnderTest, "mDiscoveryView");
        Assert.assertNull(memberViewMustBeNull);
        IModel memberModelMustBeNull = Whitebox.getInternalState(mClassUnderTest, "mModel");
        Assert.assertNull(memberModelMustBeNull);
    }


    @Test
    public void testName() {
        // given initialized discovery presenter

        // when

        //then
    }


}
