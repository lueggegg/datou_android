package com.example.luegg.oa.notification;

import android.content.Intent;
import android.os.Bundle;

import com.example.luegg.oa.base.BaseActivity;
import com.example.luegg.oa.job.all_job.JobDetailActivity;

public class DummyActivity extends BaseActivity {

    public static final String EXTRA_INFO = "EXTRA_INFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseIntent();
        finish();
    }

    private void parseIntent() {
        Intent intent = getIntent();
        JobDetailActivity.open(this, intent.getIntExtra(EXTRA_INFO, 0));
    }

    protected void initView() {

    }

    protected void initData() {

    }
}
