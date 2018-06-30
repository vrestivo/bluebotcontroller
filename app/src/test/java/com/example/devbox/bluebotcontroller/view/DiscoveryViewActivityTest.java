package com.example.devbox.bluebotcontroller.view;


import android.os.Bundle;

import com.example.devbox.bluebotcontroller.BuildConfig;
import com.example.devbox.bluebotcontroller.presenter.DiscoveryPresenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

@Config(constants = BuildConfig.class)
@RunWith(RobolectricTestRunner.class)
public class DiscoveryViewActivityTest {

    private final String PRESENTER_NAME = "mDiscoveryPresenter";

    private ActivityController<DiscoveryViewActivity> mClassUnderTest;
    private DiscoveryPresenter mMockPresenter;


    @Before
    public void testSetup(){

    }

    private void initializeDiscoveryViewActivityWithMockPresenter(){
        mMockPresenter = PowerMockito.mock(DiscoveryPresenter.class);

        mClassUnderTest = Robolectric.buildActivity(DiscoveryViewActivity.class);
        mClassUnderTest.create().restoreInstanceState(getNewBundle()).start();

        Whitebox.setInternalState(mClassUnderTest.get(), PRESENTER_NAME, mMockPresenter);

        //presenter is initialized
        validateMockPresenterInitialization();
    }

    private Bundle getNewBundle(){
        return new Bundle();
    }

    private void validateMockPresenterInitialization(){
        DiscoveryPresenter discoveryPresenter = Whitebox.getInternalState(mClassUnderTest.get(), PRESENTER_NAME);
        Assert.assertNotNull("Error: in setting mock presenter during initialization ", discoveryPresenter);
    }

    @Test
    public void initializationTest(){
        //given class under test
        DiscoveryViewActivity classUnderTest;

        //when class is initialized
        mClassUnderTest = Robolectric.buildActivity(DiscoveryViewActivity.class);
        mClassUnderTest.create().restoreInstanceState(getNewBundle()).start();

        //presenter is initialized
        DiscoveryPresenter discoveryPresenter = Whitebox.getInternalState(mClassUnderTest.get(), PRESENTER_NAME);
        Assert.assertNotNull(discoveryPresenter);
        Assert.assertEquals(DiscoveryPresenter.class.getCanonicalName(), discoveryPresenter.getClass().getCanonicalName());
    }



    @Test
    public void lifecyclePresenterCleanupTest(){
        // given initialized DiscoveryViewActivity class
        initializeDiscoveryViewActivityWithMockPresenter();

        // when onStop is called
        mClassUnderTest.stop();

        // presenter.cleanup is called
        Mockito.verify(mMockPresenter, Mockito.atLeastOnce()).lifecycleCleanup();

        // presenter is set to null
        DiscoveryPresenter discoveryPresenter = Whitebox.getInternalState(mClassUnderTest.get(), PRESENTER_NAME);
        Assert.assertNull(discoveryPresenter);
    }



}
