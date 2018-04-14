package com.example.luegg.oa.job.all_job;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.luegg.oa.R;
import com.example.luegg.oa.base.CommonUtil;
import com.example.luegg.oa.base.Constant;
import com.example.luegg.oa.base.Logger;
import com.example.luegg.oa.base.bean.AttachmentBean;
import com.example.luegg.oa.base.bean.JobBean;
import com.example.luegg.oa.base.bean.JobNodeBean;
import com.example.luegg.oa.base.bean.UserBean;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;
import java.util.List;

import ss.com.bannerslider.banners.Banner;
import ss.com.bannerslider.banners.RemoteBanner;
import ss.com.bannerslider.events.OnBannerClickListener;
import ss.com.bannerslider.views.BannerSlider;

/**
 * Created by luegg on 2017/12/13.
 */
public class JobDetailRcViewAdapter extends RecyclerView.Adapter<JobDetailRcViewAdapter.ViewHolder>{

    private static final String TAG = "JobDetailRcViewAdapter";
    private static final int TYPE_INVALID = 0;
    private static final int TYPE_HEADER_ITEM = 1;
    private static final int TYPE_NODE_ITEM = 2;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class NodeItem {
        public JobBean mainInfo;
        public JobNodeBean nodeBean;

        public NodeItem(JobBean bean) {
            this.mainInfo = bean;
        }

        public NodeItem(JobNodeBean bean) {
            nodeBean = bean;
        }

        public int getItemType() {
            if (mainInfo != null) {
                return TYPE_HEADER_ITEM;
            }
            if (nodeBean != null) {
                return TYPE_NODE_ITEM;
            }
            return TYPE_INVALID;
        }
    }

    private List<NodeItem> nodeItemList = new ArrayList<>();
    private String recSetString = null;


    public void setJobNodeData(JobBean mainInfo, List<JobNodeBean> nodeBeanList, List<UserBean> recSet) {
        if (recSet != null && recSet.size() > 0) {
            recSetString = "参与人员：" + mainInfo.invoker_name + "; ";
            for (UserBean bean : recSet) {
                recSetString += bean.name + "; ";
            }
        }
        nodeItemList.clear();
        nodeItemList.add(new NodeItem(mainInfo));
        for (JobNodeBean nodeBean : nodeBeanList) {
            nodeItemList.add(new NodeItem(nodeBean));
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout_id = viewType == TYPE_HEADER_ITEM
                ? R.layout.layout_job_detail_header
                : R.layout.layout_job_detail_node;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout_id, null);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final View itemView = holder.itemView;
        NodeItem nodeItem = nodeItemList.get(position);
        Resources res = itemView.getResources();
        if (isHeader(position)) {
            JobBean mainInfo = nodeItem.mainInfo;
            TextView textView = (TextView)itemView.findViewById(R.id.job_type);
            textView.setText(Constant.job_type_map[mainInfo.type]);
            textView = (TextView)itemView.findViewById(R.id.job_status);
            textView.setText(Constant.job_status_map[mainInfo.job_status]);
            textView.setTextColor(res.getColor(Constant.job_status_color[mainInfo.job_status]));
            View iconView = itemView.findViewById(R.id.job_status_icon);
            iconView.setBackgroundResource(Constant.job_status_icon[mainInfo.job_status]);
            textView = (TextView) itemView.findViewById(R.id.job_title);
            textView.setText(mainInfo.title);
            textView = (TextView) itemView.findViewById(R.id.total_members);
            if (recSetString != null) {
                textView.setText(recSetString);
                textView.setVisibility(View.VISIBLE);
            } else {
                textView.setVisibility(View.GONE);
            }
        } else {
            JobNodeBean nodeBean = nodeItem.nodeBean;
            TextView textView = (TextView) itemView.findViewById(R.id.node_time);
            textView.setText(nodeBean.time);
            textView = (TextView) itemView.findViewById(R.id.node_content);
            textView.setText(nodeBean.parseContent());
            textView = (TextView) itemView.findViewById(R.id.node_account);
            textView.setText(nodeBean.account);
            textView = (TextView) itemView.findViewById(R.id.node_sender);
            textView.setText(nodeBean.sender);
            textView = (TextView) itemView.findViewById(R.id.node_dept);
            textView.setText(nodeBean.dept);
            View memberContainer = itemView.findViewById(R.id.member_container);
            if (!TextUtils.isEmpty(nodeBean.extend)) {
                memberContainer.setVisibility(View.VISIBLE);
                textView = (TextView) memberContainer.findViewById(R.id.member_info);
                textView.setText(nodeBean.parseExtend());
            } else {
                memberContainer.setVisibility(View.GONE);
            }
            View attachmentContainer = itemView.findViewById(R.id.attachment_container);
            if (nodeBean.has_attachment) {
                attachmentContainer.setVisibility(View.VISIBLE);
                textView = (TextView) attachmentContainer.findViewById(R.id.attachment_info);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                textView.setText(nodeBean.parseAttachment());
            } else {
                attachmentContainer.setVisibility(View.GONE);
            }
            try {
                BannerSlider bannerSlider = (BannerSlider) itemView.findViewById(R.id.image_banner);
                if (nodeBean.has_img && nodeBean.img_attachment != null && nodeBean.img_attachment.size() > 0) {
                    List<Banner> banners = new ArrayList<>();
                    final List<String> urls = new ArrayList<>();
                    for (AttachmentBean bean : nodeBean.img_attachment) {
                        String path = CommonUtil.getWholeUrl(bean.path);
                        banners.add(new RemoteBanner(path));
                        urls.add(path);
                    }
                    bannerSlider.setBanners(banners);
                    bannerSlider.setVisibility(View.VISIBLE);
                    bannerSlider.setOnBannerClickListener(new OnBannerClickListener() {
                        @Override
                        public void onClick(int position) {
                            new ImageViewer.Builder<>(itemView.getContext(), urls).setStartPosition(position).show();
                        }
                    });
                } else {
                    bannerSlider.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Logger.e(TAG, e.toString());
            }
        }
    }

    @Override
    public int getItemCount() {
        return nodeItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return nodeItemList.get(position).getItemType();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private boolean isHeader(int position) {
        return getItemViewType(position) == TYPE_HEADER_ITEM;
    }

}
