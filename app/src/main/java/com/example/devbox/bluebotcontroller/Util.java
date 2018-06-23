package com.example.devbox.bluebotcontroller;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import com.example.devbox.bluebotcontroller.view.DiscoveryActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    /**
     * takes a set of bluetooth devices, extracts name and address
     * of each device and packages it into ArrayList<String>
     * each string has a format of:
     * "DeviceName 00:11:22:33:44:55"
     * @param devices
     * @return
     */
    public static ArrayList<String> extractDeviceString(Set<BluetoothDevice> devices){
        ArrayList<String> deviceStrings = new ArrayList<>();

        if(!devices.isEmpty()){
            for(BluetoothDevice device: devices){
                deviceStrings.add(device.getName() + " " + device.getAddress());
            }
            return deviceStrings;
        }

        return deviceStrings;
    }

    public static void fillAdapterFromSet(Set<BluetoothDevice> set, DiscoveryActivity.BtScanAdapter adapter){
        ArrayList<BluetoothDevice> arrayList = null;
        if(set!=null && adapter!=null){
            for(BluetoothDevice device: set){
                adapter.add(device);
            }
        }
    }

    public static List<String> generateNeededBluetoothPermissionsList(){
        //needed to enable bluetooth
        List<String> bluetoothPermissions = new ArrayList<>();
        bluetoothPermissions.add(Manifest.permission.BLUETOOTH);
        bluetoothPermissions.add(Manifest.permission.BLUETOOTH_ADMIN);

        //needed for scanning
        bluetoothPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        bluetoothPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

        return bluetoothPermissions;
    }


}
