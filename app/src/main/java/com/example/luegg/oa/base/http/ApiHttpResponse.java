package com.example.luegg.oa.base.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.luegg.oa.base.CommonUtil;
import com.example.luegg.oa.base.Constant;
import com.example.luegg.oa.base.Logger;

import java.util.List;

/**
 * Created by luegg on 2017/12/1.
 */
public class ApiHttpResponse {
    private static final String TAG = "ApiHttpResponse";
    private int status;
    private String data;
    private String msg;

    public void parseString(String resp) {
        try {
            JSONObject object = JSON.parseObject(resp);
            status = object.getInteger("status");
            msg = object.getString("msg");
            if (status == Constant.EC_SUCCESS) {
                data = object.getString("data");
                Logger.i(TAG, data == null ? "no data" : data);
            } else {
                CommonUtil.showToast(msg);
            }
        } catch (Exception e) {
            Logger.e(TAG, "Exception: " + e);
        }
    }

    public int getStatus() {
        return status;
    }

    public <T> List<T> getListResult(Class<T> clazz) {
        return status == Constant.EC_SUCCESS? JSON.parseArray(data, clazz) : null;
    }

    public <T> T getResult(Class<T> clazz) {
        return status == Constant.EC_SUCCESS? JSON.parseObject(data, clazz) : null;
    }

    public static <T> List<T> parseToArray(String resp, Class<T> clazz) {
        try {
            ApiHttpResponse apiHttpResponse = new ApiHttpResponse();
            apiHttpResponse.parseString(resp);
            return apiHttpResponse.getListResult(clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T parseToObject(String resp, Class<T> clazz) {
        try {
            ApiHttpResponse apiHttpResponse = new ApiHttpResponse();
            apiHttpResponse.parseString(resp);
            return apiHttpResponse.getResult(clazz);
        } catch (Exception e) {
            return null;
        }
    }

}
