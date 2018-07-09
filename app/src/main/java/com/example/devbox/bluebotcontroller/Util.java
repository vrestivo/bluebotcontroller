package com.example.devbox.bluebotcontroller;

import android.Manifest;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for misc coversion operations
 */

public class Util {

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

    public static String[] getStringArrayWithBluetoothPermissions(){
        List<String> permissions = generateNeededBluetoothPermissionsList();
        String[] results = new String[permissions.size()];
        permissions.toArray(results);
        return results;
    }
}
