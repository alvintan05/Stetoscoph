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

// activity ketika halaman login
public class MainActivity extends AppCompatActivity {

    // widget
    private Button btnSubmit;
    private EditText edtCode, edtUsername;
    private TextInputLayout textInputLayout;

    // variabel session
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // apabila belum diberi akses maka akan memunculkan pop up untuk meminta akses permisi lokasi
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // apabila belum diberi akses maka akan memunculkan pop up untuk meminta akses permisi lokasi
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // pembuatan objek sesson agar dapat menggunakan method dan fungsi yang ada pada kelas SessionSharedPreference
        session = new SessionSharedPreference(getApplicationContext());

        /*Menghandle aksi saat user menekan tombol
        saat tombol ditekan akan menjalankan method validateLength()
        * */
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateLength();
            }
        });
    }

    // method ini untuk mengecek apakah panjang karakter sudah lebih dari 8
    private void validateLength() {
        // variabel c dengan tipe data integer yang menampung panjang karakter dari input user pada edit text product code
        int c = edtCode.getText().toString().trim().length();
        if (c < 8)
            // jika kurang dari 8 akan menampilkan pesan error berikut
            textInputLayout.setError("Code minimal 8 karakter !");
        if (c >= 8) {
            // jika lebih dari sama dengan 8 maka akan memanggil method checkCode()
            checkCode();
        }
    }

    // method untuk memvalidasi dan mengecek apakah kode yang dimasukkan sesuai dengan ketentuan
    private void checkCode() {
        // variabel u dengan tipe data string yang menampung input dari edit text username
        String u = edtUsername.getText().toString().trim();
        // variabel c dengan tipe data string yang menampung input dari edit text proudct code
        String c = edtCode.getText().toString().trim();
        // percabangan switch yang akan mengecek apakah c sama dengan case yang dibawah
        switch (c) {
            case "12345678":
                // jika kode cocok maka akan menjalankan method loginUser() dengan parameter text username dan text code
                // parameter ini dikirim agar dapat digunakan pada proses yang ada didalam method loginUSer()
                loginUser(u, c);
                break;
            case "11111111":
                // jika kode cocok maka akan menjalankan method loginUser() dengan parameter text username dan text code
                // parameter ini dikirim agar dapat digunakan pada proses yang ada didalam method loginUSer()
                loginUser(u, c);
                break;
            default:
                // jika kode tidak cocok maka akan menampilkan pesan error berikut
                textInputLayout.setError("Code tidak valid");
        }
    }

    // method ini untuk berpindah halaman ke homeactivity dan menyimpan sesi login pada sharedpreference
    private void loginUser(String username, String code) {
        // Untuk berpindah ke halaman HomeActivity
        /*Jadi membuat objek intent bernama i, lalu
        memanggil method startActivity dengan parameter i untuk menjalankannya
        lalu finish() untuk menutup halaman saat ini yaitu MainActivity
        * */
        Intent i = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(i);
        finish();

        // memanggil method createUserLoginSession() dengan mengirim parameter username dan code
        session.createUserLoginSession(username, code);
        // pop up pemberitahuan
        Toast.makeText(this, "Product Activated", Toast.LENGTH_SHORT).show();
    }
}
