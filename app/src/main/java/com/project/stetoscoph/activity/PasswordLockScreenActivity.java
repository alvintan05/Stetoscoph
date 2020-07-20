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

    // widget
    Pinview pinview;
    Button btnEnter;

    // variabel
    String getData;
    String nama;

    // variabel yang nanti dibuat objek
    SessionSharedPreference session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_lock_screen);

        // mengambil data yang dikirim saat satu item di halaman report di klik
        // nantinya data ini akan dikirim lagi ke halaman ReportDetailActivity
        getData = getIntent().getStringExtra("data");
        nama = getIntent().getStringExtra("nama");

        // initialization
        pinview = (Pinview) findViewById(R.id.pinview_enter_pw);
        btnEnter = (Button) findViewById(R.id.btn_enter_pw);

        // pembuatan objek session
        session = new SessionSharedPreference(this);

        // untuk merubah judul toolbar dan menambah tombol back
        if (getSupportActionBar() != null) {
            // set judul jadi kosong
            getSupportActionBar().setTitle("");
            // menampilkan tombol back
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Menghandle saat button enter ditekan
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNama();
            }
        });

    }

    // method untuk cek nama dan password
    private void checkNama() {
        // mengecek nama
        switch (nama) {
            // jika benar nama di item adalah alvin maka passwordnya harus 000141
            case "Alvin":
                // mencocokkan password
                if (pinview.getValue().equals("000141")) {
                    pindahDetail();
                } else {
                    // jika password yang dimasukkan salah, akan muncul toast
                    Toast.makeText(this, "Salah", Toast.LENGTH_SHORT).show();
                    // mengosongkan tempat menulis password
                    pinview.clearValue();
                }
                break;
            case "Fikri":
                if (pinview.getValue().equals("000142")) {
                    pindahDetail();
                } else {
                    Toast.makeText(this, "Salah", Toast.LENGTH_SHORT).show();
                    pinview.clearValue();
                }
                break;
            case "Putra":
                if (pinview.getValue().equals("000143")) {
                    pindahDetail();
                } else {
                    Toast.makeText(this, "Salah", Toast.LENGTH_SHORT).show();
                    pinview.clearValue();
                }
                break;
            case "Pak syafrizal":
                if (pinview.getValue().equals("000144")) {
                    pindahDetail();
                } else {
                    Toast.makeText(this, "Salah", Toast.LENGTH_SHORT).show();
                    pinview.clearValue();
                }
                break;
            case "Bu purwanti":
                if (pinview.getValue().equals("000145")) {
                    pindahDetail();
                } else {
                    Toast.makeText(this, "Salah", Toast.LENGTH_SHORT).show();
                    pinview.clearValue();
                }
                break;
            case "Pak dian":
                if (pinview.getValue().equals("000146")) {
                    pindahDetail();
                } else {
                    Toast.makeText(this, "Salah", Toast.LENGTH_SHORT).show();
                    pinview.clearValue();
                }
                break;
            case "Bu dini":
                if (pinview.getValue().equals("000147")) {
                    pindahDetail();
                } else {
                    Toast.makeText(this, "Salah", Toast.LENGTH_SHORT).show();
                    pinview.clearValue();
                }
                break;
            default:
                break;
        }
    }

    // method untuk pindah ke halaman detail
    private void pindahDetail() {
        Intent intent = new Intent(PasswordLockScreenActivity.this, ReportDetailActivity.class);
        // putExtra berfungsi untuk mengirim data ke halaman selanjutnya
        intent.putExtra("data", getData);
        startActivity(intent);
        finish();
    }

    // saat tombol back ditekan maka akan kembali ke halaman sebelumnya
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
