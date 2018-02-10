package com.example.luegg.oa.job.all_job;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.example.luegg.oa.R;
import com.example.luegg.oa.base.BaseActivity;
import com.example.luegg.oa.base.Constant;
import com.example.luegg.oa.base.bean.JobBean;
import com.example.luegg.oa.base.http.ApiHttpRequest;
import com.example.luegg.oa.job.OnJobListItemClickListener;

import java.util.List;

public class JobListActivity extends BaseActivity {

    private static final String EXTRA_JOB_LIST_TYPE = "EXTRA_JOB_LIST_TYPE";

    private int jobType;
    private SearchContent searchContent = new SearchContent();
    private RecyclerView jobListView;
    private JobListAdapter listAdapter;
    private boolean refreshing = false;
    PtrClassicFrameLayout ptrClassicFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_list);
    }

    protected void initView() {
        jobListView = (RecyclerView) findViewById(R.id.job_rc_view);
        jobListView.setLayoutManager(new LinearLayoutManager(this));
        listAdapter = new JobListAdapter();
        RecyclerAdapterWithHF recyclerAdapterWithHF = new RecyclerAdapterWithHF(listAdapter);
        jobListView.setAdapter(recyclerAdapterWithHF);
        listAdapter.setItemClickListener(new OnJobListItemClickListener() {
            @Override
            public void onClick(JobBean bean) {
                if (bean.type == Constant.TYPE_JOB_SYSTEM_MSG && bean.sub_type == Constant.TYPE_JOB_SYSTEM_MSG_SUB_TYPE_BIRTHDAY) {
                    BirthdayActivity.open(getActivity(), bean.job_id);
                    return;
                }
                JobDetailActivity.open(getActivity(), bean.job_id);
            }
        });
        initPullToRefresh();
    }

    protected void initData() {
        jobType = getIntent().getIntExtra(EXTRA_JOB_LIST_TYPE, -1);
        String title = "所有工作流";
        switch (jobType) {
            case Constant.STATUS_JOB_INVOKED_BY_MYSELF:
                title = "我发起的工作流";
                break;
            case Constant.STATUS_JOB_MARK_WAITING:
                title = "待办的工作流";
                break;
            case Constant.STATUS_JOB_MARK_PROCESSED:
                title = "已处理的工作流";
                break;
            case Constant.STATUS_JOB_MARK_COMPLETED:
                title = "已归档的工作流";
                break;
            case Constant.STATUS_JOB_MARK_SYS_MSG:
                title = "系统消息";
                break;
        }
        setTitle(title);

        initSearchBar();

        refresh();
    }

    private void initPullToRefresh() {
        ptrClassicFrameLayout = (PtrClassicFrameLayout)findViewById(R.id.job_list_container);
        ptrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (refreshing) return;
                refresh();
            }
        });
        ptrClassicFrameLayout.setLoadMoreEnable(true);
        ptrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {
                JobListActivity.this.loadMore(listAdapter.getItemCount());
            }
        });
    }

    private void refresh() {
        refreshing = true;
        loadMore(0);
    }

    private void loadMore(int offset) {
        loadMore(offset, 10);
    }

    private void loadMore(int offset, int count) {
        ApiHttpRequest.PostBuilder builder = new ApiHttpRequest.PostBuilder();
        builder.url(ApiHttpRequest.getApiUrl("query_job_list"));
        builder.add("offset", offset).add("count", count);
        if (jobType >=0) {
            builder.add("status", jobType);
        }
        if (!TextUtils.isEmpty(searchContent.title)) {
            builder.add("query_content", JSON.toJSONString(searchContent));
        }
        builder.executeForArray(JobBean.class, new ApiHttpRequest.ObjectCallbackAdapter() {
            public <T> void onArray(List<T> objectList) {
                if (refreshing) {
                    refreshing = false;
                    listAdapter.clear();
                }
                List<JobBean> jobBeanList = (List<JobBean>) objectList;
                boolean noMore = jobBeanList == null || jobBeanList.size() == 0;
                ptrClassicFrameLayout.loadMoreComplete(!noMore);
                if (noMore) {
                    showToast("没有更多的数据");
                } else {
                    listAdapter.addJobList(jobBeanList);
                }
                listAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initSearchBar() {
        View container = findViewById(R.id.job_search_container);
        if (jobType == Constant.STATUS_JOB_MARK_SYS_MSG) {
            container.setVisibility(View.GONE);
            return;
        }
        View searchBtn = findViewById(R.id.job_search_btn);
        final View backBtn = findViewById(R.id.job_clear_search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = ((EditText) findViewById(R.id.job_search_edit)).getText().toString();
                if (TextUtils.isEmpty(title)) {
                    backBtn.setVisibility(View.GONE);
                } else {
                    backBtn.setVisibility(View.VISIBLE);
                }
                searchContent.title = title;
                refresh();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backBtn.setVisibility(View.GONE);
                searchContent.title = "";
                ((EditText) findViewById(R.id.job_search_edit)).setText("");
                refresh();
            }
        });
    }

    public static void open(Context context) {
        open(context, -1);
    }

    public static void open(Context context, int type) {
        Intent intent = new Intent(context, JobListActivity.class);
        intent.putExtra(JobListActivity.EXTRA_JOB_LIST_TYPE, type);
        context.startActivity(intent);
    }

    static public class SearchContent {
        public String title;
    }
}
