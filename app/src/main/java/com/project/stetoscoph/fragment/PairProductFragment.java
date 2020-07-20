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
    BluetoothManager btManager; // variabel dari class BluetoothManager
    BluetoothAdapter btAdapter;
    Set<BluetoothDevice> mPairedDevices;
    ArrayAdapter<String> mBTArrayAdapter;
    ArrayList<BluetoothDevice> mBTDevicesList = new ArrayList<>();

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

        // inisialisasi array adapter untuk mengatur list
        mBTArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        // inisalisasi btAdapter agar dapat menggunakan method dan fungsi bluetooth
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        // Jika bluetooth menyala maka button on akan hilang dan button off akan muncul
        // hanya untuk mengecek kondisi
        if (btAdapter.isEnabled()) {
            // button on hilang
            btnOn.setVisibility(View.GONE);
            // button of muncul
            btnOff.setVisibility(View.VISIBLE);
            // text status on
            tvStatus.setText("On");
        } else {
            // Jika bluetooth tidak menyala
            // button off hilang
            btnOff.setVisibility(View.GONE);
            // button on muncul
            btnOn.setVisibility(View.VISIBLE);
            // text status off
            tvStatus.setText("Off");
        }

        listView.setAdapter(mBTArrayAdapter); // assign model to view
        listView.setOnItemClickListener(mDeviceClickListener);

        if (btAdapter == null) {
            // Jika device tidak memiliki bluetooth maka akan muncul pop text
            tvStatus.setText("Status: Bluetooth not found");
            Toast.makeText(getActivity().getApplicationContext(), "Bluetooth Device Not Found!", Toast.LENGTH_SHORT).show();
        } else {
            // Jika device terdapat bluetooth

            // Menghandle ketika Button on di klik
            btnOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    turnOnBT();
                }
            });

            // Menghandle ketika Button of di klik
            btnOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    turnOffBT();
                }
            });

            // Menghandle ketika Button show pair devices di klik
            btnPair.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listPairedDevices(v);
                }
            });

            // Menghandle ketika  Button discover di klik
            btnDiscover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    discoverDevices(v);
                }
            });
        }

        return v;
    }

    // method untuk menyalakan bluetooth
    private void turnOnBT() {
        // button on hilang
        btnOn.setVisibility(View.GONE);
        // button off muncul
        btnOff.setVisibility(View.VISIBLE);

        // memunculkan pop up untuk menyalakan bluetooth
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, 1);

        // text status menjadi on
        tvStatus.setText("On");
        // pop up text
        Toast.makeText(getActivity(), "Bluetooth Turned On", Toast.LENGTH_SHORT).show();
    }

    // Method untuk mematikan bluetooth
    private void turnOffBT() {
        // button off hilang
        btnOff.setVisibility(View.GONE);
        // button on muncul
        btnOn.setVisibility(View.VISIBLE);

        // mematikan bluetooth
        btAdapter.disable();

        // text status menjadi off
        tvStatus.setText("Off");
        // pop up text
        Toast.makeText(getActivity(), "Bluetooth Turned Off", Toast.LENGTH_SHORT).show();
        mBTArrayAdapter.clear();
    }

    // Method untuk menampilkan device yang sudah paired
    private void listPairedDevices(View v) {
        // variabel mPairedDevices menampung device yang telah terpair dengan hp kita
        mPairedDevices = btAdapter.getBondedDevices();
        // jika bluetooth sedang mencari bluetooth sekitar maka cancel discovery
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }
        // membersihkan list dan adapter agar dapat diisi data baru
        mBTDevicesList.clear();
        mBTArrayAdapter.clear();

        // jika bluetooth sedang menyala
        if (btAdapter.isEnabled()) {
            // perulangan  untuk memasukkan daftar devices ke dalam list
            for (BluetoothDevice device : mPairedDevices) {
                mBTDevicesList.add(device);
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }

            // pop up text
            Toast.makeText(getActivity(), "Show Paired Devices", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getActivity(), "Bluetooth Not On", Toast.LENGTH_SHORT).show();
    }

    // Method untuk mencari device di sekitar
    private void discoverDevices(View view) {
        // jika sedang melakukan pencarian
        if (btAdapter.isDiscovering()) {
            // hentikan pencarian
            btAdapter.cancelDiscovery();
            getActivity().unregisterReceiver(discoverReceiver);
            Toast.makeText(getContext(), "Discovery Stopped", Toast.LENGTH_SHORT).show();
        } else {
            // jika belum melakukan pencarian
            if (btAdapter.isEnabled()) {
                // kosongkan list dan adapter untuk diisi data baru
                mBTDevicesList.clear();
                mBTArrayAdapter.clear();
                // mulai lakukan pencarian device bluetooth disekitar
                btAdapter.startDiscovery();
                Toast.makeText(getContext(), "Discovery Started", Toast.LENGTH_SHORT).show();

                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                getActivity().registerReceiver(discoverReceiver, filter);
            } else {
                Toast.makeText(getContext(), "Bluetooth Not On", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Receiver ini akan berjalan setiap satu device yang ditemukan disekitar kita
    private final BroadcastReceiver discoverReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // jika berhasil menemukan device di sekitar
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                String name = device.getName();
                if (name == null)
                    name = "Unnamed Device";

                // tambahkan device yang ditemukan ke dalam list
                mBTDevicesList.add(device);
                mBTArrayAdapter.add(name + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    // Ketika salah satu item di list device di klik
    private AdapterView.OnItemClickListener mDeviceClickListener;

    {
        mDeviceClickListener = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

                // jika bluetooth tidak menyala maka akan muncul pop up text
                if (!btAdapter.isEnabled()) {
                    Toast.makeText(getActivity(), "Bluetooth Not On", Toast.LENGTH_SHORT).show();
                    return;
                }

                // saat menekan salah satu device di list maka proses pencarian bluetooth harus dihentikan
                btAdapter.cancelDiscovery();

                try {
                    getActivity().unregisterReceiver(discoverReceiver);
                } catch (Exception e) {
                    e.getMessage();
                }

                // set text status
                tvStatus.setText("Connecting");

                // Menaruh bluetooth device ke dalam class BluetoothManager agar dapat digunakan pada halaman graph fragment
                btManager.setBtDevice(mBTDevicesList.get(arg2));

                if (btManager.getBtDevice() != null) {
                    // memunculkan pop up nama dan address device
                    Toast.makeText(getActivity(), btManager.getBtDevice().getName() + "\n" + btManager.getBtDevice().getAddress(), Toast.LENGTH_SHORT).show();
                    // text status di ubah menjadi berikut
                    tvStatus.setText("Pindah ke halaman graph");
                }
            }
        };
    }
}