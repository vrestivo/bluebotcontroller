package com.example.devbox.bluebotcontroller;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

@RunWith(PowerMockRunner.class)
public class UtilTest {

    @Test
    public void verifyPermissionsAdded(){
        List<String> permissions = Util.generateNeededBluetoothPermissionsList();
        Assert.assertTrue(permissions.contains(android.Manifest.permission.BLUETOOTH));
        Assert.assertTrue(permissions.contains(android.Manifest.permission.BLUETOOTH_ADMIN));
        Assert.assertTrue(permissions.contains(android.Manifest.permission.ACCESS_FINE_LOCATION));
        Assert.assertTrue(permissions.contains(android.Manifest.permission.ACCESS_COARSE_LOCATION));
    }
}
