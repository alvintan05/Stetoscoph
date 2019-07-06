package com.project.stetoscoph.fragment;


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

import com.project.stetoscoph.BluetoothManager;
import com.project.stetoscoph.R;

import java.util.ArrayList;
import java.util.Set;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class PairProductFragment extends Fragment {

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

    // defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names

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

                btManager.setBtDevice(mBTDevicesList.get(arg2));

                if (btManager.getBtDevice() != null) {
                    Toast.makeText(getActivity(), btManager.getBtDevice().getName() + "\n" + btManager.getBtDevice().getAddress(), Toast.LENGTH_SHORT).show();
                    tvStatus.setText("Pindah ke halaman graph");
                }
            }
        };
    }
}