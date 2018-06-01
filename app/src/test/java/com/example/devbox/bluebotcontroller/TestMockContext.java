package com.example.devbox.bluebotcontroller;

import android.content.Intent;
import android.test.mock.MockContext;

import java.util.ArrayList;

public class TestMockContext extends MockContext {

    ArrayList<Intent> mReceivedIntents = new ArrayList<Intent>();

    @Override
    public void startActivity(Intent intent) {
        mReceivedIntents.add(intent);
    }


    public ArrayList<Intent> getReceivedIntents(){
        return mReceivedIntents;
    }

}
