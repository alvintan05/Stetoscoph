package com.project.stetoscoph;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SessionLogin {

    // vars
    private static final String PREFERENCES_NAME = "MyPrefs";
    public static final String USERNAME_KEY = "usernameKey";
    public static final String CODE_KEY = "codeKey";
    private static final String IS_USER_LOGIN = "UserLogin";

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
        editor.putString(USERNAME_KEY, username);
        editor.putString(CODE_KEY, code);
        editor.commit();
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

    // Check for login
    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }
}
