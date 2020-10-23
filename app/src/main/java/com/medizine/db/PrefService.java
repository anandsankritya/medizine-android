package com.medizine.db;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.medizine.MedizineApp;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

public enum PrefService {
    INSTANCE;
    @Inject
    SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    PrefService() {
        MedizineApp.getStorageComponent().inject(this);
        editor = sharedPreferences.edit();
    }

    @NonNull
    public static PrefService getInstance() {
        return INSTANCE;
    }

    public void saveData(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public void saveData(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void saveData(String key, float value) {
        editor.putFloat(key, value);
        editor.apply();
    }

    public void saveData(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public void saveData(String key, long value) {
        editor.putLong(key, value);
        editor.apply();
    }

    public void saveData(String key, Set<String> value) {
        editor.putStringSet(key, value);
        editor.apply();
    }

    @Nullable
    public String getString(String key, @Nullable String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public float getFloat(String key, float defaultValue) {
        return sharedPreferences.getFloat(key, defaultValue);
    }

    @Nullable
    public Set<String> getStringSet(String key, @Nullable Set<String> defaultValue) {
        return new HashSet<>(sharedPreferences.getStringSet(key, defaultValue));
    }

    public void deleteAll() {
        editor.clear();
        editor.apply();
    }

}
