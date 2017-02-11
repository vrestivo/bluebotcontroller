package com.example.devbox.bluebotcontroller;

/**
 * Utility class for misc coversion operations
 */

public class Util {

    /**
     * takes a device description string in form of
     * device name followed by space followed by device address:
     * "DeviceName 00:11:22:33:44:55"
     * splits the name and address and returns the name
     * @param devString
     * @return device address
     */
    public static String getDeviceAddress(String devString){
        if(devString!=null && !devString.isEmpty()){
            String[] splitDevInfo = devString.split("\\s+");
            return splitDevInfo[1];
        }
        return null;
    }

    /**
     * takes a device description string in form of
     * device name followed by space followed by device address:
     * "DeviceName 00:11:22:33:44:55"
     * splits the name and address and returns the address
     * @param devString
     * @return device name
     */
    public static String getDeviceName(String devString){
        if(devString!=null && !devString.isEmpty()){
            String[] splitDevInfo = devString.split("\\s+");
            return splitDevInfo[0];
        }
        return null;
    }


}
