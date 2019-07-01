package com.project.stetoscoph;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class BluetoothManager {

    private static final String TAG = "BluetoothManager";

    private String address;

    // Bluetooth Stuff
    private static BluetoothAdapter btAdapter;
    private BluetoothDevice btDevice;
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    Context mContext;

    private static volatile BluetoothManager btManager;

//    public ConnectThread connectThread = new ConnectThread();
//    public ConnectedThread mConnectedThread;

    private BluetoothManager() {

        //Prevent form the reflection api.
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

//    public class ConnectThread extends Thread {
//        public void run() {
//            boolean fail = false;
//
//            BluetoothDevice device = btDevice;
//
//            try {
//                mBTSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
//            } catch (IOException e) {
//                fail = true;
//                Toast.makeText(mContext, "Socket Creation Failed", Toast.LENGTH_SHORT).show();
//            }
//            // Establish the Bluetooth socket connection.
//            try {
//                mBTSocket.connect();
//            } catch (IOException e) {
//                try {
//                    fail = true;
//                    mBTSocket.close();
//                } catch (IOException e2) {
//                    //insert code to deal with this
//                    Toast.makeText(mContext, "Socket Creation Failed", Toast.LENGTH_SHORT).show();
//                }
//            }
//            if (!fail) {
//                mConnectedThread = new ConnectedThread(mBTSocket);
//                mConnectedThread.start();
//                Toast.makeText(mContext, "Connected to " + btDevice.getName(), Toast.LENGTH_SHORT).show();
//            }
//        }
//
//    }
//
//    private class ConnectedThread extends Thread {
//        private final BluetoothSocket mmSocket;
//        private final InputStream mmInStream;
//
//        public ConnectedThread(BluetoothSocket socket) {
//            mmSocket = socket;
//            InputStream tmpIn = null;
//
//            // Get the input and output streams, using temp objects because
//            // member streams are final
//            try {
//                tmpIn = socket.getInputStream();
//            } catch (IOException e) {
//            }
//
//            mmInStream = tmpIn;
//        }
//
//        public void run() {
//            byte[] buffer = new byte[1024];  // buffer store for the stream
//            int bytes; // bytes returned from read()
//            // Keep listening to the InputStream until an exception occurs
//            while (true) {
//                try {
//                    bytes = mmInStream.read(buffer);
//                    final String incomingMessage = new String(buffer, 0, bytes);
//                    sleep(1000);
//                    Log.d(TAG, "InputStream: " + incomingMessage);
//
//                    Intent intent = new Intent("incomingMessage");
//                    intent.putExtra("theMessage", incomingMessage);
//                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    break;
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                    break;
//                }
//
//            }
//        }
//
//        //* Call this from the main activity to shutdown the connection *//*
//        public void cancel() {
//            try {
//                mmSocket.close();
//            } catch (IOException e) {
//            }
//        }
//    }
}
