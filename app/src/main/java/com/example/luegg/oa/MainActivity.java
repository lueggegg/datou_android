package com.example.luegg.oa;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.example.luegg.oa.base.OaApplication;
import com.example.luegg.oa.base.http.ApiHttpRequest;
import com.example.luegg.oa.base.http.ApiHttpResponse;
import com.example.luegg.oa.base.BaseActivity;
import com.example.luegg.oa.base.Logger;
import com.example.luegg.oa.base.bean.DeptBean;
import com.example.luegg.oa.login.LoginUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends BaseActivity {

    private MainPagerAdapter mainPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initView() {
        final ViewPager viewPager = (ViewPager)findViewById(R.id.main_view_pager);
        mainPagerAdapter = new MainPagerAdapter();
        viewPager.setAdapter(mainPagerAdapter);
        final List<View> children = new ArrayList<>();
        children.add(findViewById(R.id.icon_0));
        children.add(findViewById(R.id.icon_1));
        children.add(findViewById(R.id.icon_2));
        children.get(0).setActivated(true);
        final String [] titles = {
                "工作流",
                "通讯录",
                "我"
        };
        setTitle(titles[0]);
        for (int i = 0; i < 3; ++i) {
            final int index = i;
            children.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager.setCurrentItem(index);
                }
            });
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < 3; ++i) {
                    children.get(i).setActivated(i == position);
                }
                setTitle(titles[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        hideBackButton();
    }

    public void onBackPressed() {
        BaseActivity.home(this);
    }

    @Override
    protected void initData() {
        JPushInterface.resumePush(OaApplication.instance);
        JPushInterface.setAlias(OaApplication.instance, LoginUtil.getMyUid(), "" + LoginUtil.getMyUid());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainPagerAdapter.onResume();
    }
}
