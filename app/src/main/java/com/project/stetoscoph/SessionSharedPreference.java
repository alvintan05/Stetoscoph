package com.project.stetoscoph;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.project.stetoscoph.activity.MainActivity;
import com.project.stetoscoph.activity.PasswordLockScreenActivity;

public class SessionSharedPreference {
    // class ini untuk mengatur sesi login

    // variabel untuk nama dari shared preference file
    private static final String PREFERENCES_NAME = "MyPrefs";
    // variabel yang akan digunakan sebagai key
    public static final String USERNAME_KEY = "usernameKey";
    public static final String CODE_KEY = "codeKey";
    private static final String IS_USER_LOGIN = "UserLogin";

    // objek pref dan editor
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    Context c;

    // constructor, ketika class ini dibuat objek di kelas lain maka akan menjalankan proses didalam
    public SessionSharedPreference(Context context) {
        c = context;
        // membuat file shared preference dengan nama yang terdapat pada variabel PREFERENCES_NAM
        pref = c.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        // memberi hak edit pada objek editor. objek editor ini nantinya digunakan untuk memasukkan data.
        editor = pref.edit();
    }

    // fungsi untuk mengambil username yang ada di shared prefrence
    public String getUserName() {
        // mengembalikan nilai string username
        return pref.getString(USERNAME_KEY, " ");
    }

    //method untuk menyimpan sesi login, username, dan code ke file shared preference
    public void createUserLoginSession(String username, String code) {
        // menggunakan konsep key-value. Sebelah kiri adalah key dan kanan adalah value
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(USERNAME_KEY, username);
        editor.putString(CODE_KEY, code);
        editor.apply();
    }

    // method untuk menghapus data login user, method ini nantinya dipanggil saat proses logout
    public void deleteUserLoginSession() {
        editor.clear();
        editor.apply();

        // setelah menghapus lalu pindah ke halaman MainActivity
        Intent i = new Intent(c, MainActivity.class);
        c.startActivity(i);

    }

    // fungsi untuk mengecek apakah user sudah login atau belum dan akan menghasilkan nilai balik true atau false
    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

    // fungsi untuk mengecek login user, apabila belum login maka akan kembali ke activity login
    public boolean checkLogin() {

        // cek kondisi
        if (!this.isUserLoggedIn()) {
            // apabila belum login maka akan berpindah ke halaman MainActivity
            Intent i = new Intent(c, MainActivity.class);
            c.startActivity(i);

            return true;
        }

        return false;
    }
}
