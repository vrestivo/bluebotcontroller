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

    public static final String TOAST_STR = "TOAST";
    public static final String STATE_STR = "STATE";
    public static final String DEV_INFO_STR = "DEV_INFO";


    //state constants
    public static final int ST_ERROR = -1;
    public static final int ST_NONE = 0;
    public static final int ST_LISTEN = 1;
    public static final int ST_CONNECTING = 2;
    public static final int ST_CONNECTED = 3;
    public static final int ST_DISCONNECTED = 4;
    public static final int ST_DISCONNECTED_BY_USR = 5;


    public static final String STR_ERROR = "Error";
    public static final String STR_NONE = "None";
    public static final String STR_LISTEN = "Listen";
    public static final String STR_CONNECTING = "Connecting";
    public static final String STR_CONNECTED = "Connected";
    public static final String STR_DISCONNECTED = "Disconnected";
    public static final String STR_DISCONNECTED_BY_USR = "Disconnected by user";



    //directional bytes
    public static final String BT_FWD = "W";
    public static final String BT_REV = "S";
    public static final String BT_LFT = "A";
    public static final String BT_RGT = "F";

    //delay for repead action button runnable
    public static final long RUN_DELAY = 45;

    //shared preferences constants
    public static final String MAC_KEY = "MAC";
    public static final String NAME_KEY = "NAME";
    public static final String LAST_CONNECTED_KEY = "MAC";





}
