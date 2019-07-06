package com.project.stetoscoph;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public class BluetoothManager {

    // Bluetooth Stuff
    private static BluetoothAdapter btAdapter;
    private BluetoothDevice btDevice;

    private static volatile BluetoothManager btManager;

    private BluetoothManager() {
        if (btManager != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static BluetoothManager getInstance() {
        if (btManager == null) { //if there is no instance available... create new one
            synchronized (BluetoothManager.class) {
                if (btManager == null) {
                    btManager = new BluetoothManager();
                    btAdapter = BluetoothAdapter.getDefaultAdapter();
                }
            }
        }

        return btManager;
    }

    public BluetoothDevice getBtDevice() {
        return btDevice;
    }

    public void setBtDevice(BluetoothDevice btDevice) {
        this.btDevice = btDevice;
    }

}
