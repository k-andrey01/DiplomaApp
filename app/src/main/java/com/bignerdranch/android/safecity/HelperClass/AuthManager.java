package com.bignerdranch.android.safecity.HelperClass;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthManager {
    private static final String PREF_NAME = "AuthPrefs";
    private static final String KEY_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";

    private static boolean isLoggedIn = false;
    private static String username;
    private static SharedPreferences sharedPreferences;

    public static void init(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        isLoggedIn = sharedPreferences.getBoolean(KEY_LOGGED_IN, false);
        username = sharedPreferences.getString(KEY_USERNAME, null);
    }

    public static void login(String username) {
        isLoggedIn = true;
        AuthManager.username = username;
        saveLoggedInStatus();
        saveUsername();
    }

    public static void logout() {
        isLoggedIn = false;
        username = null;
        saveLoggedInStatus();
        saveUsername();
    }

    public static boolean isLoggedIn() {
        return isLoggedIn;
    }

    public static String getUsername() {
        return username;
    }

    private static void saveLoggedInStatus() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    private static void saveUsername() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }
}
