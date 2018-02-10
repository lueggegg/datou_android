package com.example.luegg.oa.job.all_job;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.luegg.oa.R;
import com.example.luegg.oa.base.Constant;
import com.example.luegg.oa.base.bean.JobBean;
import com.example.luegg.oa.job.OnJobListItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luegg on 2017/12/25.
 */
public class JobListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView typeView;
        public TextView timeView;
        public TextView statusView;
        public View statusIconView;
        public TextView titleView;
        public TextView senderView;
        public TextView lastOperatorView;

        public ViewHolder(View itemView) {
            super(itemView);
            typeView = (TextView) itemView.findViewById(R.id.job_type);
            timeView = (TextView) itemView.findViewById(R.id.job_mod_time);
            statusView = (TextView) itemView.findViewById(R.id.job_status);
            statusIconView = itemView.findViewById(R.id.job_status_icon);
            titleView = (TextView) itemView.findViewById(R.id.job_title);
            senderView = (TextView) itemView.findViewById(R.id.sender);
            lastOperatorView = (TextView) itemView.findViewById(R.id.last_reply_user);
        }
    }

    private List<JobBean> jobItemList = new ArrayList<>();
    private JobBean finalBean;
    private OnJobListItemClickListener itemClickListener;

    public void setItemClickListener(OnJobListItemClickListener listener) {
        itemClickListener = listener;
    }

    public void clear() {
        jobItemList.clear();
        finalBean = null;
    }

    public void addJobList(List<JobBean> jobList) {
        if (finalBean != null) {
            JobBean removeUntilItem = null;
            for (JobBean bean : jobList) {
                if (bean.job_id == finalBean.job_id) {
                    removeUntilItem = bean;
                    break;
                }
            }
            if (removeUntilItem != null) {
                while (true) {
                    JobBean bean = jobList.remove(0);
                    if (bean == removeUntilItem) {
                        break;
                    }
                }
            }
        }
        jobItemList.addAll(jobList);
        finalBean = jobItemList.get(jobItemList.size() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_job_detail_item, null);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder realHolder = (ViewHolder) holder;
        final View itemView = realHolder.itemView;
        final JobBean jobBean = jobItemList.get(position);
        setCommonView(realHolder, jobBean);
        realHolder.typeView.setText("[" + Constant.job_type_map[jobBean.type] + "]");
        setStatus(realHolder, jobBean);
        if (itemClickListener != null) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onClick(jobBean);
                }
            });
        }
    }

    private void setCommonView(ViewHolder holder, JobBean jobBean) {
        holder.timeView.setText(jobBean.mod_time);
        holder.titleView.setText(jobBean.title);
        holder.senderView.setText(jobBean.invoker_name);
        holder.lastOperatorView.setText(jobBean.last_operator_name);
    }

    private void setStatus(ViewHolder holder, JobBean jobBean) {
        String status = null;
        int status_color = R.color.gray;
        int status_icon = R.drawable.icon_doubt;
        if (jobBean.mark_status == Constant.STATUS_JOB_MARK_SYS_MSG) {
            switch (jobBean.sub_type) {
                case Constant.TYPE_JOB_SYSTEM_MSG_SUB_TYPE_BIRTHDAY:
                    status = "生日快乐";
                    status_color = R.color.orange;
                    status_icon = R.drawable.icon_birthday;
                    break;
                case Constant.TYPE_JOB_SYSTEM_MSG_SUB_TYPE_CANCEL_JOB:
                    status = "系统撤回";
                    status_color = R.color.job_waiting;
                    status_icon = R.drawable.icon_warning;
                    break;
            }
        }
        else if (jobBean.sub_type == Constant.TYPE_JOB_SUB_TYPE_GROUP) {
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
                status_color = Constant.mark_status_color[jobBean.mark_status];
            }
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
    public long getItemId(int position) {
        JobBean item = jobItemList.get(position);
        return item.id;
    }
}
