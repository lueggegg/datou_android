package com.example.luegg.oa.my;

import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.example.luegg.oa.LoadingActivity;
import com.example.luegg.oa.R;
import com.example.luegg.oa.base.BaseActivity;
import com.example.luegg.oa.base.CommonUtil;
import com.example.luegg.oa.base.bean.UserBean;
import com.example.luegg.oa.login.LoginUtil;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by luegg on 2017/12/9.
 */
public class MyController {
    private View container;

    public void init(final View container) {
        View logoutBtn = container.findViewById(R.id.logout_btn);
        initData(container);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUtil.logout();
                LoadingActivity.reLogin(container.getContext());
            }
        });
        View alterPsdBtn = container.findViewById(R.id.alter_pwd);
        alterPsdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.start(container.getContext(), AlterPasswordActivity.class);
            }
        });
    }

    private void initData(View container) {
        if (this.container != null) return;

        this.container = container;
        UserBean userBean = LoginUtil.getMyself();
        SimpleDraweeView draweeView = (SimpleDraweeView) container.findViewById(R.id.my_portrait);
        Uri uri = Uri.parse(CommonUtil.getWholeUrl(userBean.portrait));
        draweeView.setImageURI(uri);

        TextView textView;
        textView = (TextView) container.findViewById(R.id.my_account);
        textView.setText(userBean.account);
        textView = (TextView) container.findViewById(R.id.my_name);
        textView.setText(userBean.name);
        textView = (TextView) container.findViewById(R.id.my_dept);
        textView.setText(userBean.dept);
        textView = (TextView) container.findViewById(R.id.my_position);
        textView.setText(userBean.position);
        textView = (TextView) container.findViewById(R.id.my_cellphone);
        textView.setText(userBean.cellphone);

    }
}
