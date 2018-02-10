package com.example.luegg.oa.login;

import com.alibaba.fastjson.JSON;
import com.example.luegg.oa.base.CommonUtil;
import com.example.luegg.oa.base.Constant;
import com.example.luegg.oa.base.Logger;
import com.example.luegg.oa.base.OaApplication;
import com.example.luegg.oa.base.SharedData;
import com.example.luegg.oa.base.bean.UserBean;
import com.example.luegg.oa.base.http.ApiHttpRequest;

import java.util.LinkedList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by luegg on 2017/12/8.
 */
public class LoginUtil {
    public static final String TAG = "LoginUtil";

    public interface OnLoginListener {
        void onSuccess();
        void onFailed();
    }


    private static boolean initialized = false;
    private static UserBean loginUser;
    private static List<String> historyUser;

    private static boolean loggingIn = false;

    public static void init() {
        if (initialized) return;

        if (Constant.ALWAYS_LOGIN) {
            clearLoginUser();
        }
        loadLoginUser();

        historyUser = SharedData.getArray(Constant.SHARED_KEY_HISTORY_USER, String.class);
        if (historyUser == null) {
            historyUser = new LinkedList<>();
        }
        initialized = true;
    }

    public static List<String> getHistoryUser() {
        return historyUser;
    }

    private static void addHistoryUser(String newUser) {
        historyUser.remove(newUser);
        historyUser.add(0, newUser);
        SharedData.saveString(Constant.SHARED_KEY_HISTORY_USER, JSON.toJSONString(historyUser));
    }

    public static UserBean getMyself() {
        return loginUser;
    }

    public static int getMyUid() {
        return loginUser != null? loginUser.id : 0;
    }

    public static void saveLoginUser(UserBean bean) {
        loginUser = bean;
        SharedData.saveJsonObject(Constant.SHARED_KEY_CUR_USER, bean);
    }

    private static void clearLoginUser() {
        loginUser = null;
        SharedData.clear(Constant.SHARED_KEY_CUR_USER);
    }

    public static void loadLoginUser() {
        loginUser = SharedData.getJsonObject(Constant.SHARED_KEY_CUR_USER, UserBean.class);
    }

    public static boolean isLoggedIn() {
        return loginUser != null;
    }

    public static void login(final String account, String password, final OnLoginListener loginListener) {
        if (loggingIn) return;

        loggingIn = true;
        password = CommonUtil.getHash(password);
        Logger.i(TAG, "password: " + password);
        new ApiHttpRequest.PostBuilder().url(Constant.HOST + "/login.html")
                .add("account", account)
                .add("password", password)
//                .add("without_psd", 1)
                .executeForObject(UserBean.class, new ApiHttpRequest.ObjectCallbackAdapter() {
                    public <T> void onObject(T object) {
                        if (object == null) {
                            onFailed();
                            return;
                        }
                        Logger.w(TAG, "" + object.toString());
                        addHistoryUser(account);
                        saveLoginUser((UserBean)object);
                        loginListener.onSuccess();
                        loggingIn = false;
                    }

                    public void onFailed() {
                        loginListener.onFailed();
                        loggingIn = false;
                    }
                });
    }

    public static boolean isLoggingIn() {
        return  loggingIn;
    }

    public static void logout() {
        JPushInterface.stopPush(OaApplication.instance);
        clearLoginUser();
        new ApiHttpRequest.PostBuilder()
                .url(Constant.HOST + "/logout.html")
                .executeForStatus(new ApiHttpRequest.ObjectCallbackAdapter(){
                    public void onStatus(int status) {

                    }
                });
    }
}
