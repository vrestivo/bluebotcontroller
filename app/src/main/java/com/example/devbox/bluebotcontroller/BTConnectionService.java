package com.example.devbox.bluebotcontroller;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by devbox on 2/13/17.
 */

public class BTConnectionService extends Service {

    //TODO declare class variables
    private

    //
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    //TODO cleanup
    @Override
    public void onDestroy() {

        //TODO close connections
        //TODO end threads
        super.onDestroy();
    }


    private class ConnectThread extends Thread {

    }

    private class AcceptThread extends Thread {

    }

}