package com.example.devbox.bluebotcontroller;

/**
 * Created by devbox on 2/13/17.
 */

public class Constants {
    public static final int MESSAGE_CON_STATE_CHANGE= 1;
    public static final int MESSAGE_WRITE = 2;
    public static final int MESSAGE_READ = 3;
    public static final int MESSAGE_TOAST = 4;
    public static final int MESSAGE_FROM_REMOTE_DEVICE = 5;

    public static final String TOAST_STR = "toast";

    //state constants
    public static final int ST_ERROR = -1;
    public static final int ST_NONE = 0;
    public static final int ST_LISTEN = 1;
    public static final int ST_CONNECTING = 2;
    public static final int ST_CONNECTED = 3;
    public static final int ST_DISCONNECTED = 4;




}
