package com.project.stetoscoph;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;

public class PasswordLockScreenActivity extends AppCompatActivity {

    Pinview pinview;
    Button btnEnter;

    SessionLogin session;
    String pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_lock_screen);

        // initialization
        pinview = (Pinview) findViewById(R.id.pinview_enter_pw);
        btnEnter = (Button) findViewById(R.id.btn_enter_pw);
        session = new SessionLogin(this);

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw = pinview.getValue();

                if (session.checkPassword(pw)) {
                    startActivity(new Intent(PasswordLockScreenActivity.this, HomeActivity.class));
                    Toast.makeText(PasswordLockScreenActivity.this, "Password Correct", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(PasswordLockScreenActivity.this, "Password did not match, try again", Toast.LENGTH_SHORT).show();
                    pinview.setValue("");
                }

            }
        });
    }
}
