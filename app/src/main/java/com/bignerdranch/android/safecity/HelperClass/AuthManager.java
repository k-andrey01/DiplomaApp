package com.bignerdranch.android.safecity.HelperClass;

import android.content.Context;
import android.content.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthManager {
    private static final String PREF_NAME = "AuthPrefs";
    private static final String KEY_LOGGED_IN = "isLoggedIn";

    private static boolean isLoggedIn = false;
    private static SharedPreferences sharedPreferences;

    public static void init(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        isLoggedIn = sharedPreferences.getBoolean(KEY_LOGGED_IN, false);
    }

    public static void login() {
        isLoggedIn = true;
        saveLoggedInStatus();
    }

    public static void logout() {
        isLoggedIn = false;
        saveLoggedInStatus();
    }

    public static boolean isLoggedIn() {
        return isLoggedIn;
    }

    private static void saveLoggedInStatus() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_LOGGED_IN, isLoggedIn);
        editor.apply();
    }
}
