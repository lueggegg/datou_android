package com.example.luegg.oa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.luegg.oa.base.BaseActivity;
import com.example.luegg.oa.base.bean.UserBean;
import com.example.luegg.oa.base.http.ApiHttpRequest;
import com.example.luegg.oa.login.LoginActivity;
import com.example.luegg.oa.login.LoginUtil;

public class LoadingActivity extends Activity {

    private static final String EXTRA_DELAY = "EXTRA_DELAY";
    private static boolean loginTop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        int delay = getIntent().getIntExtra(EXTRA_DELAY, 1000);
        if (delay > 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    jump();
                }
            }, delay);
        } else {
            jump();
        }
    }

    private void jump() {
        if (LoginUtil.isLoggedIn()) {
            checkLoginUser();
        } else {
            jumpToLogin();
        }
    }

    private void checkLoginUser() {
        new ApiHttpRequest.PostBuilder().url(ApiHttpRequest.getApiUrl("fetch_my_info"))
                .executeForObject(UserBean.class, new ApiHttpRequest.ObjectCallbackAdapter() {
                    @Override
                    public <T> void onObject(T object) {
                        UserBean myself = (UserBean) object;
                        if (myself != null && myself.id == LoginUtil.getMyUid()) {
                            LoginUtil.saveLoginUser(myself);
                            enterActivity(MainActivity.class);
                        } else {
                            LoginUtil.logout();
                            jumpToLogin();
                        }
                    }
                });
    }

    private void jumpToLogin() {
        loginTop = true;
        enterActivity(LoginActivity.class);
        finish();
    }

    private <T extends Activity> void enterActivity(Class<T> clazz) {
        Intent intent = new Intent(this, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public static void reLogin(Context context) {
        if (loginTop) {
            BaseActivity.start(context, LoginActivity.class);
        } else {
            Intent intent = new Intent(context, LoadingActivity.class);
            intent.putExtra(EXTRA_DELAY, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }
}
