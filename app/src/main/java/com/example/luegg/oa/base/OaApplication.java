package com.example.luegg.oa.base;

import android.app.Application;

import com.example.luegg.oa.login.LoginUtil;
import com.facebook.drawee.backends.pipeline.Fresco;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by luegg on 2017/11/13.
 */
public class OaApplication extends Application {
    private static final String TAG = "OaApplication";
    public static Application instance;

    @Override
    public void onCreate() {
        Logger.d(TAG, "onCreate");
        super.onCreate();

        instance = this;

        Constant.init();

        JPushInterface.setDebugMode(false); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        JPushInterface.stopPush(this);

        Fresco.initialize(this);

        LoginUtil.init();
    }
}
