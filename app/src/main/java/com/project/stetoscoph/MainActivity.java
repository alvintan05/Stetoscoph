package com.project.stetoscoph;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // widget
    private Button btnSubmit;
    private EditText edtCode, edtUsername;
    private TextInputLayout textInputLayout;

    SessionLogin session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialization
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        edtCode = (EditText) findViewById(R.id.edt_code);
        edtUsername = (EditText) findViewById(R.id.edt_username);
        textInputLayout = (TextInputLayout) findViewById(R.id.text_input_code);

        session = new SessionLogin(getApplicationContext());

        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                validateLength();
                break;
        }
    }

    private void validateLength() {
        int c = edtCode.getText().toString().trim().length();
        if (c < 8)
            textInputLayout.setError("Code must have 8 characters !");
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
                Toast.makeText(this, "Code Invalid", Toast.LENGTH_SHORT).show();
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
