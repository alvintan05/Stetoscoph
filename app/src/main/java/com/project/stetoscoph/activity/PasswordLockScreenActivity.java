package com.project.stetoscoph.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.project.stetoscoph.R;
import com.project.stetoscoph.SessionSharedPreference;

public class PasswordLockScreenActivity extends AppCompatActivity {

    Pinview pinview;
    Button btnEnter;

    SessionSharedPreference session;
    String pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_lock_screen);

        // initialization
        pinview = (Pinview) findViewById(R.id.pinview_enter_pw);
        btnEnter = (Button) findViewById(R.id.btn_enter_pw);
        session = new SessionSharedPreference(this);
    }
}
