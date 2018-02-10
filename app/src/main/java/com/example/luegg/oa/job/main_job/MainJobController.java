package com.example.luegg.oa.job.main_job;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.example.luegg.oa.R;
import com.example.luegg.oa.base.CommonUtil;
import com.example.luegg.oa.base.Constant;
import com.example.luegg.oa.base.bean.JobBean;
import com.example.luegg.oa.base.http.ApiHttpRequest;
import com.example.luegg.oa.job.all_job.JobDetailActivity;
import com.example.luegg.oa.job.all_job.JobDetailRcViewAdapter;
import com.example.luegg.oa.job.all_job.JobListActivity;

import java.util.List;

/**
 * Created by luegg on 2017/12/11.
 */
public class MainJobController {
    private View container;
    private RecyclerView mainJobRcView;
    private MainJobRcViewAdapter jobAdapter;
    private ApiHttpRequest.ObjectCallback queryRecentCallback;
    private ApiHttpRequest.ObjectCallback querySysMsgCallback;
    private ApiHttpRequest.ObjectCallback queryWaitingCallback;
    private List<JobBean> recentCache;
    private List<JobBean> sysMsgCache;
    private List<JobBean> waitingCache;
    private boolean queryingRecent = false;
    private boolean queryingSysMsg = false;
    private boolean queryingWaiting = false;

    private PtrClassicFrameLayout ptrClassicFrameLayout;
    private boolean refreshing = false;

    public void init(final View container) {
        if (this.container != null) return;

        this.container = container;
        mainJobRcView = (RecyclerView) container.findViewById(R.id.main_job_rc_view);
        mainJobRcView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        jobAdapter = new MainJobRcViewAdapter();
        jobAdapter.setItemClickListener(new MainJobRcViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(JobBean bean) {
                JobDetailActivity.open(container.getContext(), bean.job_id);
            }

            @Override
            public void onMore(int type) {
                switch (type) {
                    case MainJobRcViewAdapter.TYPE_ID_RECENT:
                        JobListActivity.open(container.getContext());
                        break;
                    case MainJobRcViewAdapter.TYPE_ID_WAITING:
                        JobListActivity.open(container.getContext(), Constant.STATUS_JOB_MARK_WAITING);
                        break;
                    case MainJobRcViewAdapter.TYPE_ID_MYSELF:
                        JobListActivity.open(container.getContext(), Constant.STATUS_JOB_INVOKED_BY_MYSELF);
                        break;
                    case MainJobRcViewAdapter.TYPE_ID_COMPLETE:
                        JobListActivity.open(container.getContext(), Constant.STATUS_JOB_MARK_COMPLETED);
                        break;
                    case MainJobRcViewAdapter.TYPE_ID_PROCESSED:
                        JobListActivity.open(container.getContext(), Constant.STATUS_JOB_MARK_PROCESSED);
                        break;
                    case MainJobRcViewAdapter.TYPE_ID_SYSTEM:
                        JobListActivity.open(container.getContext(), Constant.STATUS_JOB_MARK_SYS_MSG);
                        break;
                    default:
                        CommonUtil.showToast("no more");
                }
            }
        });
        mainJobRcView.setAdapter(jobAdapter);


        queryWaitingCallback = new ApiHttpRequest.ObjectCallbackAdapter() {
            @Override
            public <T> void onArray(List<T> objectList) {
                waitingCache = (List<JobBean>) objectList;
                queryingWaiting = false;
                updateWhileAvailable();
            }

        };

        queryRecentCallback = new ApiHttpRequest.ObjectCallbackAdapter() {
            @Override
            public <T> void onArray(List<T> objectList) {
                recentCache = (List<JobBean>) objectList;
                queryingRecent = false;
                updateWhileAvailable();
            }

        };

        initPullToRefresh();
    }

    public void reload() {
        if (this.container == null) return;
        queryWaiting();
        queryRecent();
    }

    private void updateWhileAvailable() {
        boolean querying = (queryingRecent || queryingSysMsg || queryingWaiting);
        if (!querying) {
            if (refreshing) {
                refreshing = false;
                ptrClassicFrameLayout.refreshComplete();
            }
            jobAdapter.setWaitingJobList(waitingCache);
            jobAdapter.setRecentJobList(recentCache);
            jobAdapter.updateView();
        }
    }

    private void queryWaiting() {
        if (queryingRecent) return;

        queryingWaiting = true;
        new ApiHttpRequest.PostBuilder().url(ApiHttpRequest.getApiUrl("query_job_list"))
                .add("status", Constant.STATUS_JOB_MARK_WAITING)
                .executeForArray(JobBean.class, queryWaitingCallback);
    }

    private void queryRecent() {
        if (queryingRecent) return;

        queryingRecent = true;
        new ApiHttpRequest.PostBuilder().url(ApiHttpRequest.getApiUrl("query_job_list"))
                .add("count", 5)
                .executeForArray(JobBean.class, queryRecentCallback);
    }

    private void initPullToRefresh() {
        ptrClassicFrameLayout = (PtrClassicFrameLayout)container.findViewById(R.id.main_job_list_container);
        ptrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (refreshing) return;
                refreshing = true;
                reload();
            }
        });
    }
}
