package com.example.youjurental.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsUtil {
    private static final String PREF_NAME = "youjurental_prefs";
    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_NICKNAME = "nickname";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void saveLoginInfo(Context context, String account) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(KEY_ACCOUNT, account);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public static String getAccount(Context context) {
        return getPrefs(context).getString(KEY_ACCOUNT, "");
    }

    public static boolean isLoggedIn(Context context) {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public static void savePhone(Context context, String phone) {
        getPrefs(context).edit().putString(KEY_PHONE, phone).apply();
    }

    public static String getPhone(Context context) {
        return getPrefs(context).getString(KEY_PHONE, "");
    }

    public static void saveNickname(Context context, String nickname) {
        getPrefs(context).edit().putString(KEY_NICKNAME, nickname).apply();
    }

    public static String getNickname(Context context) {
        return getPrefs(context).getString(KEY_NICKNAME, "");
    }

    public static void logout(Context context) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.clear();
        editor.apply();
    }
}
