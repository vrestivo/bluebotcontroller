package com.example.devbox.bluebotcontroller;

import android.bluetooth.BluetoothDevice;

import org.powermock.api.mockito.PowerMockito;

import java.util.HashSet;
import java.util.Set;

public final class TestObjectGenerator {

    public static Set<BluetoothDevice> generateMockBluetoothDevices(){
        BluetoothDevice mockDevice1 = PowerMockito.mock(BluetoothDevice.class);
        BluetoothDevice mockDevice2 = PowerMockito.mock(BluetoothDevice.class);
        BluetoothDevice mockDevice3 = PowerMockito.mock(BluetoothDevice.class);

        HashSet<BluetoothDevice> mockDeviceSet = new HashSet<>();
        mockDeviceSet.add(mockDevice1);
        mockDeviceSet.add(mockDevice2);
        mockDeviceSet.add(mockDevice3);

        return mockDeviceSet;
    }

    public static BluetoothDevice generateASingleMockBluetoothDevice(){
        BluetoothDevice mockDevice = PowerMockito.mock(BluetoothDevice.class);
        return mockDevice;
    }



}
