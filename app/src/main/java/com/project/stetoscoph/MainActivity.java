package com.project.stetoscoph;

import android.content.Intent;
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
    private EditText edtCode;
    private TextInputLayout textInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialization
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        edtCode = (EditText) findViewById(R.id.edt_code);
        textInputLayout = (TextInputLayout) findViewById(R.id.text_input_code);

        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                //validateLength();
                intent();
                break;
        }
    }

    private void validateLength() {
        int c = edtCode.getText().toString().trim().length();
        if (c < 8)
            textInputLayout.setError("Minimal 8 Karakter !");
        if (c >= 8) {
            checkCode();
        }
    }

    private void checkCode() {
        String c = edtCode.getText().toString().trim();
        switch (c) {
            case "12345678":
                intent();
                break;
            case "11111111":
                intent();
                break;
            case "22222222":
                intent();
                break;
            case "33333333":
                intent();
                break;
            case "44444444":
                intent();
                break;
            default:
                Toast.makeText(this, "Code not valid", Toast.LENGTH_SHORT).show();
        }
    }

    private void intent() {
        Toast.makeText(this, "Code Activated", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, HomeActivity.class));
        finish();
    }
}
