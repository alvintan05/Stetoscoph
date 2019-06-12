package com.project.stetoscoph;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class PairProductFragment extends Fragment {

    private static final String TAG = "PairProductFragment";
    Button btnOn, btnOff;
    BluetoothAdapter btAdapter;

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mBroadcastRececiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(btAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, btAdapter.ERROR);

                switch (state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "onReceive: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "onReceive: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "onReceive: STATE TURNING ON");
                        break;
                }
            }
        }
    };

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

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        // click
        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Turning On Bluetooth", Toast.LENGTH_SHORT).show();
                enableBT();
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Turning Off Bluetooth", Toast.LENGTH_SHORT).show();
                disableBT();
            }
        });

        return v;
    }

    private void enableBT(){
        if(btAdapter == null) {
            Toast.makeText(getActivity(), "Does not have capabilities", Toast.LENGTH_SHORT).show();
        }
        if (!btAdapter.isEnabled()) {
            Log.d(TAG, "enableBT: Enabling BT");
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBT);

            IntentFilter btIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            getActivity().registerReceiver(mBroadcastRececiver1, btIntent);
        }
    }

    private void disableBT(){
        if (btAdapter.isEnabled()){
            Log.d(TAG, "disableBT: Disabling BT");
            btAdapter.disable();

            IntentFilter btIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            getActivity().registerReceiver(mBroadcastRececiver1, btIntent);
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: OnDestroy Called");
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(mBroadcastRececiver1);
        } catch (Exception e){

        }
    }
}
