package com.example.luegg.oa.base;

import com.alibaba.fastjson.JSON;

/**
 * Created by luegg on 2017/12/2.
 */
public class JsonObject {
    private static final String TAG = "JsonObject";

    public String toString() {
        return JSON.toJSONString(this);
    }

    public static <T extends JsonObject> T fromString(String data, Class<T> clazz) {
        try {
            return JSON.parseObject(data, clazz);
        } catch (Exception e) {
            Logger.e(TAG, "parse json error: [data: " + data + ", class: " + clazz.getSimpleName() + "] e: " + e );
            return null;
        }
    }
}
