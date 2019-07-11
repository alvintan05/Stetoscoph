package com.project.stetoscoph;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.project.stetoscoph.activity.MainActivity;
import com.project.stetoscoph.activity.PasswordLockScreenActivity;

public class SessionSharedPreference {
    // class ini untuk mengatur sesi login

    // vars
    private static final String PREFERENCES_NAME = "MyPrefs";
    public static final String USERNAME_KEY = "usernameKey";
    public static final String CODE_KEY = "codeKey";
    private static final String IS_USER_LOGIN = "UserLogin";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context c;

    public SessionSharedPreference(Context context) {
        c = context;
        pref = c.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // method untuk mengambil username yang ada di shared prefrenc
    public String getUserName() {
        return pref.getString(USERNAME_KEY, "");
    }

    //method untuk menyimpan sesi login, username, dan code ke file sharedpreference
    public void createUserLoginSession(String username, String code) {
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(USERNAME_KEY, username);
        editor.putString(CODE_KEY, code);
        editor.apply();
    }

    // method untuk menghapus data login user, method ini nantinya dipanggil saat proses logout
    public void deleteUserLoginSession() {
        editor.clear();
        editor.apply();

        Intent i = new Intent(c, MainActivity.class);
        c.startActivity(i);

    }

    // fungsi untuk mengecek apakah user sudah login atau belum dan akan menghasilkan nilai balik true atau false
    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

    // fungsi untuk mengecek login user, apabila belum login maka akan kembali ke activity login
    public boolean checkLogin() {

        if (!this.isUserLoggedIn()) {
            Intent i = new Intent(c, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(i);

            return true;
        }

        return false;
    }
}
