package com.project.stetoscoph;


import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class PairProductFragment extends Fragment {

    Button btnOn, btnOff;


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

        final BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();

        // click
        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bAdapter == null)
                    Toast.makeText(getActivity(), "Bluetooth not supported", Toast.LENGTH_SHORT).show();
                else {
                    if (!bAdapter.isEnabled()) {
                        startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1);
                        Toast.makeText(getActivity(), "Bluetooth turned on", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bAdapter.disable();
                Toast.makeText(getActivity(), "Bluetooth turned off", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

}
