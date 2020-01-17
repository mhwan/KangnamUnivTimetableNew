package com.mhwan.kangnamunivtimetable.Util;

import android.content.Context;
import android.content.SharedPreferences;

public class BaseSharedPreference {
    private String preferenceName;
    private Context context;
    private SharedPreferences preferences;

    protected BaseSharedPreference() {
    }

    protected BaseSharedPreference(Context context) {
        this.context = context;
        this.preferenceName = "KnuTimeTable";
        getPreference();
    }

    protected BaseSharedPreference(Context context, String preferenceName) {
        this.context = context;
        this.preferenceName = preferenceName;
        getPreference();
    }

    private void getPreference() {
        preferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
    }


    protected void putValue(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    protected String getValue(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    protected void putValue(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    protected boolean getValue(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    protected void putValue(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    protected int getValue(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    protected void removeKey(String key) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public void removeAll() {
        SharedPreferences.Editor editor = this.preferences.edit();
        editor.clear();
        editor.apply();
    }
}
