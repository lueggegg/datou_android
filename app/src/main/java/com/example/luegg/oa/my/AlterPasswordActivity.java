package com.example.luegg.oa.my;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.example.luegg.oa.LoadingActivity;
import com.example.luegg.oa.R;
import com.example.luegg.oa.base.BaseActivity;
import com.example.luegg.oa.base.CommonUtil;
import com.example.luegg.oa.base.Constant;
import com.example.luegg.oa.base.http.ApiHttpRequest;
import com.example.luegg.oa.login.LoginUtil;

public class AlterPasswordActivity extends BaseActivity {

    private int tryCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter_password);
        setTitle("修改密码");
    }

    protected void initView() {
        View commitBtn = findViewById(R.id.commit_btn);
        commitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alterPassword();
            }
        });
    }

    protected void initData() {

    }

    private void alterPassword() {
        String oldPsd = ((EditText)findViewById(R.id.old_psd)).getText().toString();
        if (TextUtils.isEmpty(oldPsd)) {
            CommonUtil.showToast("原密码不能为空");
            return;
        }
        oldPsd = CommonUtil.getHash(oldPsd);
        if (!LoginUtil.getMyself().password.equals(oldPsd)) {
            tryCount++;
            if (tryCount >= 3) {
                CommonUtil.showToast("原密码错误，超过尝试次数！！");
                logout();
            } else {
                CommonUtil.showToast("原密码错误");
            }
            return;
        }
        String newPsd = ((EditText)findViewById(R.id.new_psd)).getText().toString();
        if (TextUtils.isEmpty(newPsd)) {
            CommonUtil.showToast("新密码不能为空");
            return;
        }

        Argument arg = new Argument();
        arg.password = CommonUtil.getHash(newPsd);

        new ApiHttpRequest.PostBuilder().url(ApiHttpRequest.getApiUrl("alter_account"))
                .add("op", "update")
                .add("uid", LoginUtil.getMyUid())
                .add("account_info", JSON.toJSONString(arg))
                .executeForStatus(new ApiHttpRequest.ObjectCallbackAdapter() {
                    @Override
                    public void onStatus(int status) {
                        if (status == Constant.EC_SUCCESS) {
                            logout();
                        }
                    }
                });
    }

    private void logout() {
        LoginUtil.logout();
        LoadingActivity.reLogin(this);
    }

    static public class Argument {
        public String password;
    }
}
