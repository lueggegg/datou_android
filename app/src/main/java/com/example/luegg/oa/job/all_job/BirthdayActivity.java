package com.example.luegg.oa.job.all_job;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.luegg.oa.R;
import com.example.luegg.oa.base.BaseActivity;
import com.example.luegg.oa.base.CommonUtil;
import com.example.luegg.oa.base.Constant;
import com.example.luegg.oa.base.Logger;
import com.example.luegg.oa.base.bean.JobNodeBean;
import com.example.luegg.oa.base.http.ApiHttpRequest;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class BirthdayActivity extends BaseActivity {

    private static final String TAG = "BirthdayActivity";
    private static final String EXTRA_JOB_ID = "EXTRA_JOB_ID";

    private SimpleDraweeView imageView;
    private TextView contentView;
    private TextView timeView;

    private int job_id;
    private JobNodeBean contentBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthday);
        setTitle("生日快乐");
    }

    protected void initView() {
        imageView = (SimpleDraweeView) findViewById(R.id.image);
        contentView = (TextView) findViewById(R.id.content);
        timeView = (TextView) findViewById(R.id.time);
    }

    protected void initData() {
        job_id = getIntent().getIntExtra(EXTRA_JOB_ID, 0);
        if (job_id <= 0) {
            Logger.e(TAG, "job_id=" + job_id + " from intent");
            finish();
            return;
        }
        loadContent();
    }

    private void setView() {
        if (contentBean.has_img && contentBean.img_attachment.size() > 0) {
            Uri uri = Uri.parse(CommonUtil.getWholeUrl(contentBean.img_attachment.get(0).path));
            imageView.setImageURI(uri);
        } else {
            imageView.setVisibility(View.GONE);
        }
        timeView.setText(contentBean.time);
        contentView.setText(contentBean.parseContent());
    }

    private void loadContent() {
        new ApiHttpRequest.PostBuilder().url(ApiHttpRequest.getApiUrl("query_job_info"))
                .add("job_id", job_id)
                .add("type", "node")
                .executeForArray(JobNodeBean.class, new ApiHttpRequest.ObjectCallbackAdapter() {
                    @Override
                    public <T> void onArray(List<T> objectList) {
                        if (objectList.size() > 0) {
                            notifyRead();
                            contentBean = (JobNodeBean) objectList.get(0);
                            setView();
                        }
                    }
                });
    }

    private void notifyRead() {
        new ApiHttpRequest.PostBuilder().url(ApiHttpRequest.getApiUrl("alter_job"))
                .add("job_id", job_id)
                .add("op", "notify_read")
                .executeForStatus(null);
    }

    public static void open(Context context, int job_id) {
        Intent intent = new Intent(context, BirthdayActivity.class);
        intent.putExtra(BirthdayActivity.EXTRA_JOB_ID, job_id);
        context.startActivity(intent);
    }
}
