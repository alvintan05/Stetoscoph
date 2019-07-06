package com.project.stetoscoph;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class LockFragment extends Fragment {

    TextView tvStatus;
    Button btnSet, btnChange;

    SessionSharedPreference session;

    Boolean isPasswordEnabled;

    public LockFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lock, container, false);

        // initialization
        tvStatus = (TextView) v.findViewById(R.id.tv_pw_status);
        btnSet = (Button) v.findViewById(R.id.btn_set_pw);
        btnChange = (Button) v.findViewById(R.id.btn_change_pw);
        session = new SessionSharedPreference(getActivity());

        checkPasswordStatus();

        // Button click
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PasswordActivity.class));
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordEnabled) startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
                else Toast.makeText(getActivity(), "Set your password first", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    private void checkPasswordStatus(){
        isPasswordEnabled = session.isPasswordEnabled();

        if (isPasswordEnabled) {
            tvStatus.setText("Active");
            btnSet.setText("Remove Password");
        }

        if (!isPasswordEnabled) {
            tvStatus.setText("Not Active");
            btnSet.setText("Set Password");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPasswordStatus();
    }
}
