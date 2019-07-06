package com.project.stetoscoph;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;

public class ChangePasswordActivity extends AppCompatActivity {

    Pinview pinviewOld, pinviewNew;
    Button btnChange;

    SessionSharedPreference session;
    String oldPw, newPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // initialization
        pinviewOld = (Pinview) findViewById(R.id.pinview_change_old);
        pinviewNew = (Pinview) findViewById(R.id.pinview_change_new);
        btnChange = (Button) findViewById(R.id.btn_change);

        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Change Password");

        session = new SessionSharedPreference(this);

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPw = pinviewOld.getValue();
                newPw = pinviewNew.getValue();

                if (session.checkPassword(oldPw)) {
                    session.deleteUserPasswordSession();
                    session.createUserPasswordSession(newPw);
                    Toast.makeText(ChangePasswordActivity.this, "Password Changed", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Old Password did not match, try again", Toast.LENGTH_SHORT).show();
                    pinviewOld.setValue("");
                    pinviewNew.setValue("");
                }

            }
        });
    }
}
