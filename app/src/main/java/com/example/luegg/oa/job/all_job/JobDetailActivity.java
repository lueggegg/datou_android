package com.example.luegg.oa.job.all_job;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.example.luegg.oa.R;
import com.example.luegg.oa.base.BaseActivity;
import com.example.luegg.oa.base.CommonUtil;
import com.example.luegg.oa.base.Constant;
import com.example.luegg.oa.base.Logger;
import com.example.luegg.oa.base.SharedData;
import com.example.luegg.oa.base.bean.BaseBean;
import com.example.luegg.oa.base.bean.JobBean;
import com.example.luegg.oa.base.bean.JobNodeBean;
import com.example.luegg.oa.base.bean.UserBean;
import com.example.luegg.oa.base.http.ApiHttpRequest;
import com.example.luegg.oa.contact.MemberSelectorController;
import com.example.luegg.oa.login.LoginUtil;

import java.util.List;

public class JobDetailActivity extends BaseActivity {

    private static final String TAG = "JobDetailActivity";
    private static final String EXTRA_JOB_ID = "EXTRA_JOB_ID";

    private int job_id;
    private RecyclerView detailRcView;
    private JobDetailRcViewAdapter detailAdapter;
    boolean fetchingMainInfo = false;
    private JobBean mainInfoCache;
    boolean fetchingNodes = false;
    private List<JobNodeBean> nodeListCache;
    boolean fetchingRecSet = false;
    private List<UserBean> recSetCache;

    private boolean autoJob;
    private boolean canReply = true;
    private boolean invokedByMyself;
    private View replyBar;
    private View operationBar;
    private boolean displayingOperationBar = false;
    private View loadingView;
    private ApiHttpRequest.ObjectCallback operationCallback;
    private MemberSelectorController memberSelectorController;
    private View memberSelectorContainer;
    List<Integer> selectedMember;

    public static class AutoJobNextProcessor extends BaseBean {
        public int uid;
        public String dept;
        public String account;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);
    }

    protected void initView() {
        setTitle("工作流详情");
        replyBar = findViewById(R.id.edit_bar);
        operationBar = findViewById(R.id.operation_bar);
        loadingView = findViewById(R.id.waiting_page);

        detailRcView = (RecyclerView) findViewById(R.id.job_node_rc_view);
        detailRcView.setLayoutManager(new LinearLayoutManager(this));
        detailAdapter = new JobDetailRcViewAdapter();
        detailRcView.setAdapter(detailAdapter);
    }

    protected void initData() {
        job_id = getIntent().getIntExtra(EXTRA_JOB_ID, 0);
        if (job_id <= 0) {
            Logger.e(TAG, "job_id=" + job_id + " from intent");
            finish();
            return;
        }

        refresh();
    }

    private void loadMainInfo() {
        if (fetchingMainInfo) return;

        fetchingMainInfo = true;
        new ApiHttpRequest.PostBuilder().url(ApiHttpRequest.getApiUrl("query_job_info"))
                .add("job_id", job_id)
                .add("type", "base")
                .executeForObject(JobBean.class, new ApiHttpRequest.ObjectCallbackAdapter() {
                    @Override
                    public <T> void onObject(T object) {
                        mainInfoCache = (JobBean) object;
                        fetchingMainInfo = false;
                        notifyRead();
                        checkData();
                    }
                });
    }

    private void loadNodeList() {
        if (fetchingNodes) return;

        fetchingNodes = true;
        new ApiHttpRequest.PostBuilder().url(ApiHttpRequest.getApiUrl("query_job_info"))
                .add("job_id", job_id)
                .add("type", "node")
                .executeForArray(JobNodeBean.class, new ApiHttpRequest.ObjectCallbackAdapter() {
                    @Override
                    public <T> void onArray(List<T> objectList) {
                        nodeListCache = (List<JobNodeBean>) objectList;
                        fetchingNodes = false;
                        if (nodeListCache.size() > 0) {
                            JobNodeBean firstNode = nodeListCache.get(0);
                            if (firstNode.rec_set > 0) {
                                loadRecSet(firstNode.rec_set);
                            } else {
                                checkData();
                            }
                        }
                    }
                });
    }

    private void loadRecSet(int setId) {
        if (fetchingRecSet) return;

        fetchingRecSet = true;
        new ApiHttpRequest.PostBuilder().url(ApiHttpRequest.getApiUrl("query_job_info"))
                .add("job_id", job_id)
                .add("type", "rec_set")
                .add("set_id", setId)
                .executeForArray(UserBean.class, new ApiHttpRequest.ObjectCallbackAdapter() {
                    @Override
                    public <T> void onArray(List<T> objectList) {
                        recSetCache = (List<UserBean>) objectList;
                        fetchingRecSet = false;
                        checkData();
                    }
                });
    }

    private void refresh() {
        loadingView.setVisibility(View.VISIBLE);
        loadMainInfo();
        loadNodeList();
    }

    private void checkData() {
        if (fetchingMainInfo || fetchingNodes || fetchingRecSet) {
            return;
        }
        initOperation();
    }

    private void initOperation() {
        autoJob = mainInfoCache.type != Constant.TYPE_JOB_OFFICIAL_DOC && mainInfoCache.type != Constant.TYPE_JOB_DOC_REPORT;
        invokedByMyself = mainInfoCache.invoker == LoginUtil.getMyUid();
        if (mainInfoCache.job_status == Constant.STATUS_JOB_PROCESSING) {
            replyBar.setVisibility(View.VISIBLE);
            initCommonOperation();
            if (autoJob) {
                initAutoJob();
            } else {
                setViewData();
            }
        } else {
            setNoReply();
            setViewData();
        }
    }

    private void initAutoJob() {
        new ApiHttpRequest.PostBuilder().url(ApiHttpRequest.getApiUrl("process_auto_job"))
                .add("job_id", job_id)
                .add("op", "query_cur")
                .add("cur_path_index", mainInfoCache.cur_path_index)
                .executeForArray(AutoJobNextProcessor.class, new ApiHttpRequest.ObjectCallbackAdapter() {
                    public <T> void onArray(List<T> object) {
                        List<AutoJobNextProcessor> userList = (List<AutoJobNextProcessor>) object;
                        canReply = false;
                        boolean hasProcessor = userList != null && userList.size() > 0;
                        if (hasProcessor) {
                            for (AutoJobNextProcessor processor : userList) {
                                if (LoginUtil.getMyUid() == processor.uid) {
                                    canReply = true;
                                    break;
                                }
                            }
                        }
                        View rejectWarning = findViewById(R.id.reject_warning);
                        if (canReply) {
                            rejectWarning.setVisibility(View.VISIBLE);
                            rejectWarning.animate().setDuration(2000).alpha(0).setStartDelay(2000).start();
                            initRejectOperation();
                        }
                         else if (hasProcessor) {
                            rejectWarning.setVisibility(View.GONE);

                            String content = "{*【以下员工，待处理该工作流：】*}\n";
                            String divider = "    ";
                            for (AutoJobNextProcessor processor : userList) {
                                content += divider + processor.dept + divider + processor.account + divider + processor.name + "\n";
                            }
                            JobNodeBean nodeBean = new JobNodeBean();
                            nodeBean.content = CommonUtil.wrapJobContent(content);
                            nodeBean.id = -1;
                            nodeBean.account = "system";
                            nodeBean.dept = "后台";
                            nodeBean.sender = "system";
                            nodeBean.has_attachment = false;
                            nodeBean.has_img = false;
                            nodeBean.time = "待定";
                            nodeListCache.add(nodeBean);
                        }
                        setViewData();
                    }
                });
    }

    private void setViewData() {
        detailAdapter.setJobNodeData(mainInfoCache, nodeListCache, recSetCache);
        loadingView.setVisibility(View.GONE);
    }

    private void setNoReply() {
        replyBar.setVisibility(View.GONE);
        operationBar.setVisibility(View.GONE);
    }

    private void initCommonOperation() {
        final View operationBtn = findViewById(R.id.operation_btn);
        operationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayingOperationBar = !displayingOperationBar;
                operationBar.setVisibility(displayingOperationBar? View.VISIBLE : View.GONE);
                operationBtn.setActivated(displayingOperationBar);
            }
        });

        initReplyOperation();
        initNewMemberOperation();
        initCancelOperation();
        initCompleteOperation();
        operationCallback = new ApiHttpRequest.ObjectCallbackAdapter() {
            @Override
            public void onStatus(int status) {
                if (status == Constant.EC_SUCCESS) {
                    if (selectedMember != null && memberSelectorController != null) {
                        memberSelectorController.filterUid(selectedMember);
                    }
                    ((EditText)findViewById(R.id.reply_edit)).setText("");
                    refresh();
                }
            }
        };
    }

    private void initReplyOperation() {
        View replyBtn = findViewById(R.id.reply_btn);
        replyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canReply) {
                    showToast("您没有权限回复。");
                    return;
                }
                String content = ((EditText)findViewById(R.id.reply_edit)).getText().toString();
                ApiHttpRequest.PostBuilder builder = new ApiHttpRequest.PostBuilder();
                if (autoJob) {
                    builder.url(ApiHttpRequest.getApiUrl("process_auto_job"))
                            .add("job_id", job_id)
                            .add("op", "reply")
                            .add("content", CommonUtil.wrapJobContent(content))
                            .add("job_type", mainInfoCache.type)
                            .executeForStatus(operationCallback);
                } else {
                    builder.url(ApiHttpRequest.getApiUrl("send_official_doc"));
                    builder.add("op", "reply")
                            .add("job_id", job_id)
                            .add("has_img", 0)
                            .add("has_attachment", 0)
                            .add("content", CommonUtil.wrapJobContent(content));
                    selectedMember = memberSelectorController.getSelectedMember();
                    builder.add("rec_set", JSON.toJSONString(selectedMember));
                    builder.executeForStatus(operationCallback);
                }
            }
        });
    }

    private void initRejectOperation() {
        View replyBtn = findViewById(R.id.reply_btn);
        replyBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CommonUtil.showCommonDialog(getActivity(), "提示", "拒绝并归档该申请？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String content = ((EditText)findViewById(R.id.reply_edit)).getText().toString();
                        new ApiHttpRequest.PostBuilder().url(ApiHttpRequest.getApiUrl("process_auto_job"))
                                .add("job_id", job_id)
                                .add("job_type", mainInfoCache.type)
                                .add("op", "reject")
                                .add("content", CommonUtil.wrapJobContent(content))
                                .executeForStatus(operationCallback);
                    }
                });
                return true;
            }
        });
    }

    private void initNewMemberOperation() {
        View newMemberBtn = findViewById(R.id.new_member_btn);
        if (autoJob) {
            newMemberBtn.setVisibility(View.GONE);
        } else {
            if (memberSelectorController == null) {
                memberSelectorContainer = findViewById(R.id.member_selector_container);
                memberSelectorController = new MemberSelectorController();
                memberSelectorController.init(memberSelectorContainer, recSetCache);
                View btn = findViewById(R.id.close_member_selector_btn);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        memberSelectorContainer.setVisibility(View.GONE);
                    }
                });
            }
            newMemberBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    memberSelectorContainer.setVisibility(View.VISIBLE);
                    int def = 5;
                    int index = SharedData.getInt(Constant.SHARED_KEY_SHOW_MEMBER_SELECTOR_GUIDE, def);
                    if (index > 0) {
                        index--;
                        SharedData.saveInt(Constant.SHARED_KEY_SHOW_MEMBER_SELECTOR_GUIDE, index);
                        View view = memberSelectorContainer.findViewById(R.id.member_selector_prompt);
                        view.setVisibility(View.VISIBLE);
                        view.setAlpha(1);
                        view.animate().setDuration(2000).alpha(0).setStartDelay(2000).start();
                    }
                }
            });
        }
    }

    private void initCancelOperation() {
        View cancelBtn = findViewById(R.id.cancel_btn);
        if (invokedByMyself) {
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonUtil.showCommonDialog(getActivity(), "提示", "确认撤回该工作流？", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ApiHttpRequest.PostBuilder builder = new ApiHttpRequest.PostBuilder();
                            builder.add("op", "cancel").add("job_id", job_id);
                            if (autoJob) {
                                builder.url(ApiHttpRequest.getApiUrl("process_auto_job"))
                                        .add("job_type", mainInfoCache.type);
                            } else {
                                new ApiHttpRequest.PostBuilder().url(ApiHttpRequest.getApiUrl("alter_job"));
                            }
                            builder.executeForStatus(operationCallback);
                        }
                    });
                }
            });
        } else {
            cancelBtn.setVisibility(View.GONE);
        }
    }

    private void initCompleteOperation() {
        View completeBtn = findViewById(R.id.complete_btn);
        if (!autoJob && (invokedByMyself || CommonUtil.isAuthorized(Constant.OPERATION_MASK_QUERY_REPORT))) {
            completeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonUtil.showCommonDialog(getActivity(), "提示", "归档后，不可再操作该工作流。确认归档？", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new ApiHttpRequest.PostBuilder().url(ApiHttpRequest.getApiUrl("alter_job"))
                                    .add("job_id", job_id)
                                    .executeForStatus(operationCallback);
                            dialog.dismiss();
                        }
                    });
                }
            });
        } else {
            completeBtn.setVisibility(View.GONE);
        }
    }

    private void notifyRead() {
        if (mainInfoCache != null && mainInfoCache.sub_type == Constant.TYPE_JOB_SUB_TYPE_GROUP) {
            new ApiHttpRequest.PostBuilder().url(ApiHttpRequest.getApiUrl("alter_job"))
                    .add("job_id", job_id)
                    .add("op", "group_doc_read")
                    .executeForStatus(null);
        }
    }

    public static void open(Context context, int job_id) {
        Intent intent = new Intent(context, JobDetailActivity.class);
        intent.putExtra(JobDetailActivity.EXTRA_JOB_ID, job_id);
        context.startActivity(intent);
    }
}
