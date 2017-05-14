package com.example.n_u.officebotapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class OBSession {
    public static void deletePreference(Context paramContext) {
        PreferenceManager.getDefaultSharedPreferences(paramContext).edit().clear().apply();
    }

    public static String getPreference(String paramString, Context paramContext) {
        return PreferenceManager.getDefaultSharedPreferences(paramContext).getString(paramString, null);
    }

    public static boolean hasPreference(String paramString, Context paramContext) {
        return getPreference(paramString, paramContext) != null;
    }

    public static void putPreference(String key, String value, Context context) {
        SharedPreferences.Editor se = PreferenceManager.getDefaultSharedPreferences(context).edit();
        se.putString(key, value);
        se.apply();
    }
}
