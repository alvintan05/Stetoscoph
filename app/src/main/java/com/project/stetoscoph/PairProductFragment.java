package com.project.stetoscoph;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class PairProductFragment extends Fragment {

    private static final String TAG = "PairProductFragment";

    // widget
    Button btnOn, btnOff, btnPair, btnDiscover;
    TextView tvStatus;
    ListView listView;

    // Bluetooth Stuff
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    Set<BluetoothDevice> mPairedDevices;
    ArrayAdapter<String> mBTArrayAdapter;
    ArrayList<BluetoothDevice> mBTDevicesList = new ArrayList<>();

    private Handler mHandler; // Our main handler that will receive callback notifications
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path
    /*private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data*/

    // defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    public PairProductFragment() {
        // Required empty public constructor
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mHandler = new Handler() {
//            public void handleMessage(android.os.Message msg) {
//                if (msg.what == CONNECTING_STATUS) {
//                    if (msg.arg1 == 1)
//                        tvStatus.setText("Connected to Device: " + (msg.obj));
//                    else
//                        tvStatus.setText("Connection Failed");
//                }
//
//                if (msg.what == MESSAGE_READ) {
//                    tvStatus.setText(msg.obj.toString());
//                }
//            }
//        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pair_product, container, false);

        // initialization
        btnOn = (Button) v.findViewById(R.id.btn_bluetooth_on);
        btnOff = (Button) v.findViewById(R.id.btn_bluetooth_off);
        btnPair = (Button) v.findViewById(R.id.btn_show_pair);
        btnDiscover = (Button) v.findViewById(R.id.btn_discover);
        tvStatus = (TextView) v.findViewById(R.id.bluetoothStatus);
        listView = (ListView) v.findViewById(R.id.devicesListView);
        btManager = BluetoothManager.getInstance();

        mBTArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        btAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        // check bluetooth status when open app
        if (btAdapter.isEnabled()) {
            btnOn.setVisibility(View.GONE);
            btnOff.setVisibility(View.VISIBLE);
            tvStatus.setText("On");
        } else {
            btnOff.setVisibility(View.GONE);
            btnOn.setVisibility(View.VISIBLE);
            tvStatus.setText("Off");
        }

        listView.setAdapter(mBTArrayAdapter); // assign model to view
        listView.setOnItemClickListener(mDeviceClickListener);

        if (btAdapter == null) {
            // Device does not support Bluetooth
            tvStatus.setText("Status: Bluetooth not found");
            Toast.makeText(getActivity().getApplicationContext(), "Bluetooth Device Not Found!", Toast.LENGTH_SHORT).show();
        } else {
            // if device support bluetooth
            // click
            btnOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getActivity(), "Turning On Bluetooth", Toast.LENGTH_SHORT).show();
                    turnOnBT();
                }
            });

            btnOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getActivity(), "Turning Off Bluetooth", Toast.LENGTH_SHORT).show();
                    turnOffBT();
                }
            });

            btnPair.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listPairedDevices(v);
                }
            });

            btnDiscover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    discoverDevices(v);
                }
            });
        }

        return v;
    }

    private void turnOnBT() {
        btnOn.setVisibility(View.GONE);
        btnOff.setVisibility(View.VISIBLE);
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        tvStatus.setText("On");
        Toast.makeText(getActivity(), "Bluetooth Turned On", Toast.LENGTH_SHORT).show();
    }

    // Enter here after user selects "yes" or "no" to enabling radio
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                tvStatus.setText("On");
            } else
                tvStatus.setText("Off");
        }
    }

    private void turnOffBT() {
        btnOff.setVisibility(View.GONE);
        btnOn.setVisibility(View.VISIBLE);
        btAdapter.disable(); // turn off

        tvStatus.setText("Off");
        Toast.makeText(getActivity(), "Bluetooth Turned Off", Toast.LENGTH_SHORT).show();
        mBTArrayAdapter.clear();
    }

    private void listPairedDevices(View v) {
        mPairedDevices = btAdapter.getBondedDevices();
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }
        mBTDevicesList.clear();
        mBTArrayAdapter.clear();
        if (btAdapter.isEnabled()) {
            // put it's one to the adapter
            for (BluetoothDevice device : mPairedDevices) {
                mBTDevicesList.add(device);
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }

            Toast.makeText(getActivity(), "Show Paired Devices", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getActivity(), "Bluetooth Not On", Toast.LENGTH_SHORT).show();
    }

    private void discoverDevices(View view) {
        // Check if the device is already discovering
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
            getActivity().unregisterReceiver(discoverReceiver);
            Toast.makeText(getContext(), "Discovery Stopped", Toast.LENGTH_SHORT).show();
        } else {
            if (btAdapter.isEnabled()) {
                mBTDevicesList.clear();
                mBTArrayAdapter.clear(); // clear items
                btAdapter.startDiscovery();
                Toast.makeText(getContext(), "Discovery Started", Toast.LENGTH_SHORT).show();

                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                getActivity().registerReceiver(discoverReceiver, filter);
            } else {
                Toast.makeText(getContext(), "Bluetooth Not On", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private final BroadcastReceiver discoverReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                String name = device.getName();
                if (name == null)
                    name = "Unnamed Device";
                mBTDevicesList.add(device);
                mBTArrayAdapter.add(name + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    private AdapterView.OnItemClickListener mDeviceClickListener;

    {
        mDeviceClickListener = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

                if (!btAdapter.isEnabled()) {
                    Toast.makeText(getActivity(), "Bluetooth Not On", Toast.LENGTH_SHORT).show();
                    return;
                }

                btAdapter.cancelDiscovery();

                try {
                    getActivity().unregisterReceiver(discoverReceiver);
                } catch (Exception e) {
                    e.getMessage();
                }

                tvStatus.setText("Connecting");
                final String deviceName = mBTDevicesList.get(arg2).getName();
                final String deviceAddress = mBTDevicesList.get(arg2).getAddress();

                btManager.setBtDevice(mBTDevicesList.get(arg2));

                if (btManager.getBtDevice() != null) {
                    Toast.makeText(getActivity(), btManager.getBtDevice().getName() + "\n" + btManager.getBtDevice().getAddress(), Toast.LENGTH_SHORT).show();
                    tvStatus.setText("Pindah ke halaman graph");
                }

//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
//                    Toast.makeText(getActivity(), "Trying to pair with " + deviceName, Toast.LENGTH_SHORT).show();
//                    mBTDevicesList.get(arg2).createBond();
//                }

                // Get the device MAC address, which is the last 17 chars in the View
//                String info = ((TextView) v).getText().toString();
//                final String address = info.substring(info.length() - 17);
//                final String name = info.substring(0, info.length() - 17);

                // Spawn a new thread to avoid blocking the GUI one
                /*new Thread() {
                    public void run() {
                        boolean fail = false;

                        BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);

                        try {
                            mBTSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                        } catch (IOException e) {
                            fail = true;
                            Toast.makeText(getActivity().getBaseContext(), "Socket Creation Failed", Toast.LENGTH_SHORT).show();
                        }
                        // Establish the Bluetooth socket connection.
                        try {
                            mBTSocket.connect();
                        } catch (IOException e) {
                            try {
                                fail = true;
                                mBTSocket.close();
                                mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                        .sendToTarget();
                            } catch (IOException e2) {
                                //insert code to deal with this
                                Toast.makeText(getActivity().getBaseContext(), "Socket Creation Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (!fail) {
//                            mConnectedThread = new ConnectedThread(mBTSocket);
//                            mConnectedThread.start();

                            mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, deviceName)
                                    .sendToTarget();
                        }
                    }
                }.start();*/
            }
        };
    }

    /*private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;

        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    final String incomingMessage = new String(buffer, 0 , bytes);
                    sleep(1000);
                    Log.d(TAG, "InputStream: " + incomingMessage);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvStatus.setText(incomingMessage);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                } catch (InterruptedException e){
                    e.printStackTrace();
                    break;
                }

            }
        }

        *//* Call this from the main activity to shutdown the connection *//*
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }*/

}