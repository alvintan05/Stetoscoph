package com.project.stetoscoph;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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

import java.util.List;

import me.aflak.bluetooth.Bluetooth;

public class PairProductFragment extends Fragment implements Bluetooth.DiscoveryCallback, Bluetooth.CommunicationCallback {

    private static final String TAG = "PairProductFragment";

    // widget
    Button btnOn, btnOff, btnPair, btnDiscover;
    TextView tvStatus;
    ListView listView;

    // Bluetooth Stuff
    private Bluetooth bt;
    BluetoothAdapter btAdapter;
    private List<BluetoothDevice> devices;
    ArrayAdapter<String> mBTArrayAdapter;
    private boolean registered = false;

    String btAddress;

    public PairProductFragment() {
        // Required empty public constructor
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
        bt = new Bluetooth(getActivity());

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(mReceiver, filter);
        registered = true;

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

        bt.setCommunicationCallback(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!btAdapter.isEnabled()) {
                    Toast.makeText(getActivity(), "Bluetooth Not On", Toast.LENGTH_SHORT).show();
                    return;
                }

                tvStatus.setText("Connecting...");

                btAddress = devices.get(position).getAddress();

                Toast.makeText(getActivity(), btAddress, Toast.LENGTH_SHORT).show();

                bt.connectToAddress(btAddress);

//                bt.pair(devices.get(position));
//                bt.connectToDevice(devices.get(position));

            }
        });

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
                    turnOnBT();
                }
            });

            btnOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    turnOffBT();
                }
            });

            btnPair.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listPairedDevices();
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
        bt.enableBluetooth();
        tvStatus.setText("On");
        Toast.makeText(getActivity(), "Bluetooth Turned On", Toast.LENGTH_SHORT).show();
    }

    private void turnOffBT() {
        btnOff.setVisibility(View.GONE);
        btnOn.setVisibility(View.VISIBLE);
        bt.disableBluetooth();
        tvStatus.setText("Off");
        Toast.makeText(getActivity(), "Bluetooth Turned Off", Toast.LENGTH_SHORT).show();
        mBTArrayAdapter.clear();
    }

    private void listPairedDevices() {
        devices = bt.getPairedDevices();
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }
        mBTArrayAdapter.clear();
        if (btAdapter.isEnabled()) {
            // put it's one to the adapter
            for (BluetoothDevice device : devices)
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());

            Toast.makeText(getActivity(), "Show Paired Devices", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getActivity(), "Bluetooth Not On", Toast.LENGTH_SHORT).show();
    }

    private void discoverDevices(View view) {
        // Check if the device is already discovering
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
            Toast.makeText(getContext(), "Discovery Stopped", Toast.LENGTH_SHORT).show();
        } else {
            if (btAdapter.isEnabled()) {
                devices.clear();
                mBTArrayAdapter.clear(); // clear items
                bt.setDiscoveryCallback(this);
                bt.scanDevices();
                Toast.makeText(getContext(), "Discovery Started", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Bluetooth Not On", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        getActivity().unregisterReceiver(mReceiver);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listPairedDevices();
                            }
                        });
                        break;
                }
            }
        }
    };

    @Override
    public void onFinish() {
        Toast.makeText(getActivity(), "Scan Finished", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDevice(BluetoothDevice device) {
        final BluetoothDevice tmp = device;
        devices.add(device);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBTArrayAdapter.add(tmp.getAddress() + " \n " + tmp.getName());
                mBTArrayAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onPair(BluetoothDevice device) {
        Toast.makeText(getActivity(), "Paired", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUnpair(BluetoothDevice device) {

    }

    @Override
    public void onConnect(BluetoothDevice device) {
        tvStatus.setText("Connected to " + device.getName());
    }

    @Override
    public void onDisconnect(BluetoothDevice device, String message) {

    }

    @Override
    public void onMessage(String message) {
        tvStatus.setText(message);
    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void onConnectError(BluetoothDevice device, String message) {

    }


}