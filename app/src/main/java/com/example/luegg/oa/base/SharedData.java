package com.example.luegg.oa.base;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by luegg on 2017/12/8.
 */
public class SharedData {

    public static SharedPreferences getSharedPreferences(String key) {
        return OaApplication.instance.getSharedPreferences(key, Context.MODE_PRIVATE);
    }

    public static void clear(String key) {
        SharedPreferences preferences = getSharedPreferences(key);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void saveString(String key, String value) {
        SharedPreferences preferences = getSharedPreferences(key);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("data", value);
        editor.apply();
    }

    public static String getString(String key) {
        SharedPreferences preferences = getSharedPreferences(key);
        return preferences.getString("data", null);
    }

    public static <T> List<T> getArray(String key, Class<T> clazz) {
        SharedPreferences preferences = getSharedPreferences(key);
        String data = preferences.getString("data", null);
        if (data != null) {
            return JSON.parseArray(data, clazz);
        }
        return null;
    }

    public static void saveJsonObject(String key, JsonObject obj) {
        SharedPreferences preferences = getSharedPreferences(key);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("data", obj.toString());
        editor.apply();
    }

    public static <T extends JsonObject> T getJsonObject(String key, Class<T> clazz) {
        SharedPreferences preferences = getSharedPreferences(key);
        String data = preferences.getString("data", null);
        return data == null? null : JsonObject.fromString(data, clazz);
    }
}
