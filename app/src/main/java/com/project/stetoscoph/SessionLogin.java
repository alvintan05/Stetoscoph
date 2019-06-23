package com.project.stetoscoph;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SessionLogin {
    // class ini untuk mengatur sesi login

    // vars
    private static final String PREFERENCES_NAME = "MyPrefs";
    public static final String USERNAME_KEY = "usernameKey";
    public static final String CODE_KEY = "codeKey";
    public static final String PASSWORD_KEY = "passwordKey";
    private static final String IS_USER_LOGIN = "UserLogin";
    private static final String IS_PASSWORD_ENABLED = "PasswordEnabled";
    private static final String IS_PASSWORD_ENTERED = "PasswordEntered";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context c;

    public SessionLogin(Context context) {
        c = context;
        pref = c.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createUserLoginSession(String username, String code) {
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putBoolean(IS_PASSWORD_ENABLED, false);
        editor.putString(USERNAME_KEY, username);
        editor.putString(CODE_KEY, code);
        editor.apply();
    }

    public void createUserPasswordSession(String password) {
        editor.putBoolean(IS_PASSWORD_ENABLED, true);
        editor.putBoolean(IS_PASSWORD_ENTERED, true);
        editor.putString(PASSWORD_KEY, password);
        editor.apply();
    }

    public void passwordEntered() {
        editor.putBoolean(IS_PASSWORD_ENTERED, true);
        editor.apply();
    }

    public void passwordOut() {
        editor.putBoolean(IS_PASSWORD_ENTERED, false);
        editor.apply();
    }

    public void deleteUserLoginSession() {
        editor.clear();
        editor.apply();

        Intent i = new Intent(c, MainActivity.class);
        c.startActivity(i);

    }

    public void deleteUserPasswordSession() {
        editor.remove(PASSWORD_KEY);
        editor.putBoolean(IS_PASSWORD_ENABLED, false);
        editor.apply();
    }

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

    public boolean checkPasswordEnter() {
        if (this.isPasswordEnabled() && !this.isPasswordEntered()) {
            Intent i = new Intent(c, PasswordLockScreenActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(i);

            return true;
        }

        return false;
    }

    public boolean checkPassword(String password) {
        if (this.isPasswordEnabled()) {
            String pw = pref.getString(PASSWORD_KEY, "");
            if (pw.equals(password)) {
                return true;
            }
        }

        return false;
    }

    // Check for login
    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

    // Check for password
    public boolean isPasswordEnabled() {
        return pref.getBoolean(IS_PASSWORD_ENABLED, false);
    }

    // Check for password enter
    public boolean isPasswordEntered() {
        return pref.getBoolean(IS_PASSWORD_ENTERED, false);
    }
}
