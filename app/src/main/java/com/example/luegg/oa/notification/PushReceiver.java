package com.example.luegg.oa.notification;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.example.luegg.oa.base.Logger;
import com.example.luegg.oa.base.OaApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

public class PushReceiver extends BroadcastReceiver {
    private static final String TAG = "PushReceiver";

    public static class PushExtra {
        public int type;
        public int job_id;
        public String title;
        public String content;
        public String sender;
    }

    public PushReceiver() {
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Logger.d(TAG, "onReceive - " + intent.getAction());
            String extra = intent.getStringExtra(JPushInterface.EXTRA_EXTRA);
            PushExtra pushExtra = JSON.parseObject(extra, PushExtra.class);
            Intent toLaunch = new Intent(OaApplication.instance, DummyActivity.class);
            // You'd need this line only if you had shown the notification from a Service
            toLaunch.setAction(Intent.ACTION_MAIN);
            toLaunch.putExtra(DummyActivity.EXTRA_INFO, pushExtra.job_id);
            PendingIntent intentBack = PendingIntent.getActivity(OaApplication.instance, 0,toLaunch, PendingIntent.FLAG_UPDATE_CURRENT);
            NotifyCenter.NotifyParameter parameter = new NotifyCenter.NotifyParameter();
            parameter.title = pushExtra.title;
            parameter.message = pushExtra.sender + "：" + pushExtra.content;
            parameter.id = pushExtra.job_id;
            parameter.pendingIntent = intentBack;
            NotifyCenter.notify(parameter);
        } catch (Exception e) {
            Logger.e(TAG, "" + e);
        }
    }
}
