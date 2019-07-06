package com.project.stetoscoph.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.project.stetoscoph.R;
import com.project.stetoscoph.SessionSharedPreference;

public class PasswordActivity extends AppCompatActivity {

    Pinview pinview;
    Button btnSet, btnRemove;

    SessionSharedPreference session;

    String pw;
    Boolean isPasswordEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        // Initialization
        btnSet = (Button) findViewById(R.id.btn_set);
        btnRemove = (Button) findViewById(R.id.btn_remove);
        pinview = (Pinview) findViewById(R.id.pinview);
        session = new SessionSharedPreference(this);

        isPasswordEnabled = session.isPasswordEnabled();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (isPasswordEnabled) {
                getSupportActionBar().setTitle("Remove Password");
            }
            if (!isPasswordEnabled) {
                getSupportActionBar().setTitle("Set Password");
            }

        }

        if (isPasswordEnabled) {
            btnSet.setVisibility(View.GONE);
            btnRemove.setVisibility(View.VISIBLE);
        }

        if (!isPasswordEnabled) {
            btnSet.setVisibility(View.VISIBLE);
            btnRemove.setVisibility(View.GONE);
        }

        // Button click
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw = pinview.getValue();

                session.createUserPasswordSession(pw);

                Toast.makeText(PasswordActivity.this, "Password Set", Toast.LENGTH_SHORT).show();

                finish();
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw = pinview.getValue();

                if (session.checkPassword(pw)) {
                    session.deleteUserPasswordSession();

                    Toast.makeText(PasswordActivity.this, "Password Deleted", Toast.LENGTH_SHORT).show();

                    finish();
                } else {
                    Toast.makeText(PasswordActivity.this, "Password not match", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
