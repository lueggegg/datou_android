package com.example.luegg.oa.job.main_job;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.luegg.oa.R;
import com.example.luegg.oa.base.Constant;
import com.example.luegg.oa.base.bean.JobBean;
import com.example.luegg.oa.base.bean.UserBean;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by luegg on 2017/12/11.
 */
public class MainJobRcViewAdapter extends RecyclerView.Adapter<MainJobRcViewAdapter.ViewHolder>{

    private static final int TYPE_LABEL_ITEM = 0;
    private static final int TYPE_JOB_ITEM = 1;

    public static final int TYPE_ID_SYSTEM = 1;
    public static final int TYPE_ID_WAITING = 2;
    public static final int TYPE_ID_RECENT = 3;
    public static final int TYPE_ID_MYSELF = 4;
    public static final int TYPE_ID_PROCESSED = 5;
    public static final int TYPE_ID_COMPLETE = 6;

    public interface OnItemClickListener {
        void onClick(JobBean bean);
        void onMore(int type);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView typeView;
        public TextView timeView;
        public TextView statusView;
        public View statusIconView;
        public TextView titleView;
        public TextView senderView;
        public TextView lastOperatorView;

        public ViewHolder(View itemView, int type) {
            super(itemView);
            if (type == TYPE_JOB_ITEM) {
                setAsDetailHolder();
            }
        }

        private void setAsDetailHolder() {
            typeView = (TextView) itemView.findViewById(R.id.job_type);
            timeView = (TextView) itemView.findViewById(R.id.job_mod_time);
            statusView = (TextView) itemView.findViewById(R.id.job_status);
            statusIconView = itemView.findViewById(R.id.job_status_icon);
            titleView = (TextView) itemView.findViewById(R.id.job_title);
            senderView = (TextView) itemView.findViewById(R.id.sender);
            lastOperatorView = (TextView) itemView.findViewById(R.id.last_reply_user);
        }
    }

    private class TypeItem {
        public String type;
        public boolean empty = true;

        public TypeItem() {}
        public TypeItem(String type) {
            this.type = type;
        }
    }

    private class JobItem {
        public int typeId;
        public TypeItem typeItem;
        public JobBean jobBean;

        public JobItem(int typeId, TypeItem typeItem) {
            this.typeId = typeId;
            this.typeItem = typeItem;
            this.jobBean = null;
        }

        public JobItem(int typeId, JobBean jobBean) {
            this.typeId = typeId;
            this.typeItem = null;
            this.jobBean = jobBean;
        }

        public boolean isTypeItem() {
            return typeItem != null;
        }
    }

    private List<JobItem> jobItemList = new ArrayList<>();
    private TypeItem systemTypeItem = new TypeItem("系统消息");
    private List<JobBean> systemJobList;
    private TypeItem waitingTypeItem = new TypeItem("待办工作流");
    private List<JobBean> waitingJobList;
    private TypeItem recentTypeItem = new TypeItem("最近工作流");
    private List<JobBean> recentJobList;
    private TypeItem myselfTypeItem = new TypeItem("我发起的工作流");
    private TypeItem processedTypeItem = new TypeItem("已处理的工作流");
    private TypeItem completeTypeItem = new TypeItem("已归档的工作流");

    private OnItemClickListener itemClickListener;

    public void setItemClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }

    public void updateView() {
        parseJobItemList();
    }

    public void setSystemJobList(List<JobBean> jobList) {
        systemJobList = jobList;
    }

    public void setWaitingJobList(List<JobBean> jobList) {
        waitingJobList = jobList;
    }

    public void setRecentJobList(List<JobBean> jobList) {
        recentJobList = jobList;
    }

    private void parseJobItemList() {
        jobItemList.clear();
        addJobListToJobItemList(TYPE_ID_WAITING, waitingTypeItem, waitingJobList);
        addJobListToJobItemList(TYPE_ID_RECENT, recentTypeItem, recentJobList);
        addJobListToJobItemList(TYPE_ID_SYSTEM, systemTypeItem, systemJobList);
        addJobListToJobItemList(TYPE_ID_MYSELF, myselfTypeItem, null);
        addJobListToJobItemList(TYPE_ID_PROCESSED, processedTypeItem, null);
        addJobListToJobItemList(TYPE_ID_COMPLETE, completeTypeItem, null);
        notifyDataSetChanged();
    }

    private void addJobListToJobItemList(int typeId, TypeItem typeItem, List<JobBean> jobBeanList) {
        jobItemList.add(new JobItem(typeId, typeItem));
        if (jobBeanList != null&& jobBeanList.size() > 0) {
            for (JobBean bean : jobBeanList) {
                jobItemList.add(new JobItem(typeId, bean));
            }
            typeItem.empty = false;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = viewType == TYPE_LABEL_ITEM? R.layout.layout_job_label_item : R.layout.layout_job_detail_item;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, null);
        return new ViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final View itemView = holder.itemView;
        final JobItem jobItem = jobItemList.get(position);
        int type = getItemViewType(position);
        if (type == TYPE_LABEL_ITEM) {
            TextView labelView = (TextView) itemView.findViewById(R.id.job_label);
            labelView.setText(jobItem.typeItem.type);
            if (itemClickListener != null) {
                View btnView = itemView.findViewById(R.id.more_job_btn);
                btnView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onMore(jobItem.typeId);
                    }
                });
            }
            View emptyView = itemView.findViewById(R.id.empty_container);
            emptyView.setVisibility(jobItem.typeItem.empty? View.VISIBLE : View.GONE);
        } else {
            switch (jobItem.typeId) {
                case TYPE_ID_SYSTEM:
                    setSystemItemView(holder, jobItem.jobBean);
                    break;
                case TYPE_ID_WAITING:
                    setWaitingItemView(holder, jobItem.jobBean);
                    break;
                case TYPE_ID_RECENT:
                    setRecentItemView(holder, jobItem.jobBean);
                    break;
                default:
                    return;
            }
            if (itemClickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onClick(jobItem.jobBean);
                    }
                });
            }
        }
    }

    private void setCommonView(ViewHolder holder, JobBean jobBean) {
        holder.timeView.setText(jobBean.mod_time);
        holder.titleView.setText(jobBean.title);
        holder.senderView.setText(jobBean.invoker_name);
        holder.lastOperatorView.setText(jobBean.last_operator_name);
    }

    private void setSystemItemView(ViewHolder holder, JobBean jobBean) {

    }

    private void setWaitingItemView(ViewHolder holder, JobBean jobBean) {
        setCommonView(holder, jobBean);
        holder.typeView.setText("[" + Constant.job_type_map[jobBean.type] + "]");
        setStatus(holder, jobBean);
    }

    private void setRecentItemView(ViewHolder holder, JobBean jobBean) {
        setCommonView(holder, jobBean);
        holder.typeView.setText("[" + Constant.job_type_map[jobBean.type] + "]");
        setStatus(holder, jobBean);
    }

    private void setStatus(ViewHolder holder, JobBean jobBean) {
        String status = null;
        int status_color = R.color.gray;
        int status_icon = R.drawable.icon_doubt;
        if (jobBean.sub_type == Constant.TYPE_JOB_SUB_TYPE_GROUP) {
            if (jobBean.mark_status == Constant.STATUS_JOB_MARK_WAITING) {
                status = "新消息";
                status_icon = Constant.mark_status_icon[Constant.STATUS_JOB_MARK_NEW_REPLY];
            } else if (jobBean.mark_status == Constant.STATUS_JOB_MARK_PROCESSED){
                status = "已读";
                status_icon = R.drawable.icon_read;
            }
        }
        try {
            if (status == null) {
                status = Constant.mark_status_map[jobBean.mark_status];
                status_icon = Constant.mark_status_icon[jobBean.mark_status];
            }
            status_color = Constant.mark_status_color[jobBean.mark_status];
        } catch (Exception e) {
            status_color = R.color.gray;
            status_icon = R.drawable.icon_doubt;
        }
        Resources res = holder.itemView.getResources();
        holder.statusView.setTextColor(res.getColor(status_color));
        holder.statusView.setText(status);
        holder.statusIconView.setBackgroundResource(status_icon);
    }

    @Override
    public int getItemCount() {
        return jobItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return jobItemList.get(position).isTypeItem()? TYPE_LABEL_ITEM : TYPE_JOB_ITEM;
    }

    @Override
    public long getItemId(int position) {
        JobItem item = jobItemList.get(position);
        return (item.typeId << 16) + (item.isTypeItem()? 0 : item.jobBean.job_id);
    }


}
