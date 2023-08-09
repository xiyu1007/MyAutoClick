package com.example.yu;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    private final SharedPreferences sharedPreferences;

    private final int defaultValueInt = 0;
    private final boolean defaultValueBoolen = false;
    private final String defaultValueString = null;

    public SharedPreferencesHelper(Context context, String preferencesName) {
        sharedPreferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
    }

    // 保存字符串值
    public void saveString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    // 保存布尔值
    public void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    // 保存整数值
    public void saveInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    // 获取整数值
    public int getInt(String key) {
        return sharedPreferences.getInt(key, defaultValueInt);
    }

    // 获取布尔值
    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, defaultValueBoolen);
    }

    // 获取字符串值
    public String getString(String key) {
        return sharedPreferences.getString(key, defaultValueString);
    }


    // ... 可以添加其他类型的保存和获取方法

}

//myAppSharedPreferences = new SharedPreferencesHelper(this, "MyAppPreferences");
//
//// 保存用户设置
//sharedPreferencesHelper.saveString("username", "John");
//sharedPreferencesHelper.saveInt("age", 25);
//sharedPreferencesHelper.saveBoolean("isPremiumUser", true);
//
//// 获取用户设置
//String username = sharedPreferencesHelper.getString("username", "");
//int age = sharedPreferencesHelper.getInt("age", 0);
//boolean isPremiumUser = sharedPreferencesHelper.getBoolean("isPremiumUser", false);
