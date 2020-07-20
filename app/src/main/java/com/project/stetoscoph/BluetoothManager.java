package com.project.stetoscoph;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public class BluetoothManager {

    // class ini untuk menampung BluetoothDevice dari PairProductFragment agar dapat diakses oleh GraphFragment

    // objek btAdapter dan btDevice
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

    // fungsi untuk mengambil bluetooth device
    public BluetoothDevice getBtDevice() {
        return btDevice;
    }

    // method untuk mengeset bluetooth device
    public void setBtDevice(BluetoothDevice btDevice) {
        this.btDevice = btDevice;
    }

}
