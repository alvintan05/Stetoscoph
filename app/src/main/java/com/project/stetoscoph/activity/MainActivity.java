package com.project.stetoscoph.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project.stetoscoph.R;
import com.project.stetoscoph.SessionSharedPreference;

public class MainActivity extends AppCompatActivity {

    // widget
    private Button btnSubmit;
    private EditText edtCode, edtUsername;
    private TextInputLayout textInputLayout;

    SessionSharedPreference session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialization
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        edtCode = (EditText) findViewById(R.id.edt_code);
        edtUsername = (EditText) findViewById(R.id.edt_username);
        textInputLayout = (TextInputLayout) findViewById(R.id.text_input_code);

        // Mengecek apakah permission lokasi sudah diberikan oleh user, kalau blm maka akan memunculkan dialog untuk meminta akses lokasi
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        session = new SessionSharedPreference(getApplicationContext());

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateLength();
            }
        });
    }

    private void validateLength() {
        int c = edtCode.getText().toString().trim().length();
        if (c < 8)
            textInputLayout.setError("Code minimal 8 karakter !");
        if (c >= 8) {
            checkCode();
        }
    }

    private void checkCode() {
        String u = edtUsername.getText().toString().trim();
        String c = edtCode.getText().toString().trim();
        switch (c) {
            case "12345678":
                loginUser(u, c);
                break;
            case "11111111":
                loginUser(u, c);
                break;
            default:
                textInputLayout.setError("Code tidak valid");
        }
    }

    private void loginUser(String username, String code) {
        Intent i = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(i);
        finish();

        session.createUserLoginSession(username, code);
        Toast.makeText(this, "Product Activated", Toast.LENGTH_SHORT).show();
    }
}
