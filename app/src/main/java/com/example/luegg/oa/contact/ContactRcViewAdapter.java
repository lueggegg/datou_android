package com.example.luegg.oa.contact;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.luegg.oa.R;
import com.example.luegg.oa.base.CommonUtil;
import com.example.luegg.oa.base.bean.BaseBean;
import com.example.luegg.oa.base.bean.DeptBean;
import com.example.luegg.oa.base.bean.UserBean;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by luegg on 2017/12/1.
 */
public class ContactRcViewAdapter extends RecyclerView.Adapter<ContactRcViewAdapter.ViewHolder> {

    public interface OnUserItemClickListener {
        void onClick(UserBean bean);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class ContactBean {
        public DeptBean dept;
        public List<UserBean> userList;
        public boolean fold = true;
    }

    private class ContactItem {
        public int type;
        public BaseBean bean;

        public ContactItem() {}

        public ContactItem(int type, BaseBean bean) {
            this.type = type;
            this.bean = bean;
        }
    }

    private static final int TYPE_DEPT_ITEM = 0;
    private static final int TYPE_USER_ITEM = 1;

    private List<ContactBean> contactData;
    private boolean foldDefault = true;
    private List<ContactItem> itemDataList = new ArrayList<>();
    private Map<Integer, ContactBean> contactBeanMap = new HashMap<>();
    private OnUserItemClickListener userItemClickListener;

    public void setFoldDefault(boolean foldDefault) {
        this.foldDefault = foldDefault;
    }

    public void setContactData(List<ContactBean> data) {
        contactData = data;
        parseContactBeanList();
    }

    public void setUserListData(List<UserBean> data) {
        contactBeanMap.clear();
        contactData = new ArrayList<>();
        for (UserBean bean : data) {
            ContactBean contactBean = contactBeanMap.get(bean.getDeptId());
            if (contactBean == null) {
                contactBean = new ContactBean();
                contactBean.fold = foldDefault;
                contactBean.userList = new ArrayList<>();
                DeptBean deptBean = new DeptBean();
                deptBean.id = bean.getDeptId();
                deptBean.name = bean.dept;
                contactBean.dept = deptBean;
                contactBeanMap.put(bean.getDeptId(), contactBean);
                contactData.add(contactBean);
            }
            contactBean.userList.add(bean);
        }
        parseContactBeanList();
    }

    private void parseContactBeanList() {
        parseContactBeanList(contactData);
    }

    private void parseContactBeanList(List<ContactBean> data) {
        itemDataList.clear();
        if (data != null) {
            for (ContactBean bean : data) {
                itemDataList.add(new ContactItem(TYPE_DEPT_ITEM, bean.dept));
                if (!bean.fold) {
                    for (UserBean userBean : bean.userList) {
                        itemDataList.add(new ContactItem(TYPE_USER_ITEM, userBean));
                    }
                }
            }
        }
    }

    public void setUserItemClickListener(OnUserItemClickListener listener) {
        userItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = viewType == TYPE_DEPT_ITEM? R.layout.layout_contact_dept_item : R.layout.layout_contact_user_item;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, null);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final View itemView = holder.itemView;
        final ContactItem contactItem = itemDataList.get(position);
        int type = getItemViewType(position);
        if (type == TYPE_DEPT_ITEM) {
            TextView deptView = (TextView)itemView.findViewById(R.id.dept_name);
            deptView.setText(contactItem.bean.name);
            final View foldBtn = itemView.findViewById(R.id.fold_btn);
            final ContactBean contactBean = contactBeanMap.get(contactItem.bean.id);
            foldBtn.setActivated(!contactBean.fold);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contactBean.fold = !contactBean.fold;
                    parseContactBeanList();
                    notifyDataSetChanged();
                }
            });
        } else {
            final UserBean userBean = (UserBean)contactItem.bean;
            TextView accountView = (TextView)itemView.findViewById(R.id.user_account);
            TextView nameView = (TextView)itemView.findViewById(R.id.user_name);
            TextView positionView = (TextView)itemView.findViewById(R.id.user_position);
            accountView.setText(userBean.account);
            nameView.setText(userBean.name);
            positionView.setText(userBean.position);
            if (userItemClickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userItemClickListener.onClick(userBean);
                    }
                });
            }
            SimpleDraweeView draweeView = (SimpleDraweeView) itemView.findViewById(R.id.user_portrait);
            Uri uri = userBean.portrait != null? Uri.parse(CommonUtil.getWholeUrl(userBean.portrait)) : null;
            draweeView.setImageURI(uri);
        }
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    @Override
    public long getItemId(int position) {
        ContactItem item = itemDataList.get(position);
        return (item.type << 16) + item.bean.id;
    }

    @Override
    public int getItemViewType(int position) {
        return itemDataList.get(position).type;
    }
}
