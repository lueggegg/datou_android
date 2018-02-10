package com.example.luegg.oa.contact;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.luegg.oa.R;
import com.example.luegg.oa.base.CommonUtil;
import com.example.luegg.oa.base.bean.BaseBean;
import com.example.luegg.oa.base.bean.DeptBean;
import com.example.luegg.oa.base.bean.UserBean;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by luegg on 2017/12/26.
 */
public class MemberSelectorAdapter extends RecyclerView.Adapter<MemberSelectorAdapter.ViewHolder>{


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class MemberBean {
        public DeptBean dept;
        public List<UserBean> userList;
        public boolean fold = true;
    }

    private class MemberItem {
        public int type;
        public BaseBean bean;

        public MemberItem() {}

        public MemberItem(int type, BaseBean bean) {
            this.type = type;
            this.bean = bean;
        }
    }

    private static final int TYPE_DEPT_ITEM = 0;
    private static final int TYPE_USER_ITEM = 1;

    private List<MemberBean> memberData;
    private List<MemberItem> itemDataList = new ArrayList<>();
    private Map<Integer, MemberBean> memberBeanMap = new HashMap<>();
    private Set<Integer> selectedSet = new LinkedHashSet<>();

    public List<Integer> getSelectedList() {
        List<Integer> result = new LinkedList<>();
        for (Integer i : selectedSet) {
            result.add(i);
        }
        return result;
    }
    
    public void setMemberData(List<MemberBean> data) {
        memberData = data;
        parseMemberBeanList();
    }

    public void setUserListData(List<UserBean> data) {
        memberBeanMap.clear();
        memberData = new ArrayList<>();
        for (UserBean bean : data) {
            MemberBean memberBean = memberBeanMap.get(bean.getDeptId());
            if (memberBean == null) {
                memberBean = new MemberBean();
                memberBean.fold = false;
                memberBean.userList = new ArrayList<>();
                DeptBean deptBean = new DeptBean();
                deptBean.id = bean.getDeptId();
                deptBean.name = bean.dept;
                memberBean.dept = deptBean;
                memberBeanMap.put(bean.getDeptId(), memberBean);
                memberData.add(memberBean);
            }
            memberBean.userList.add(bean);
        }
        parseMemberBeanList();
    }

    private void parseMemberBeanList() {
        parseMemberBeanList(memberData);
    }

    private void parseMemberBeanList(List<MemberBean> data) {
        itemDataList.clear();
        if (data != null) {
            for (MemberBean bean : data) {
                itemDataList.add(new MemberItem(TYPE_DEPT_ITEM, bean.dept));
                if (!bean.fold) {
                    for (UserBean userBean : bean.userList) {
                        itemDataList.add(new MemberItem(TYPE_USER_ITEM, userBean));
                    }
                }
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = viewType == TYPE_DEPT_ITEM? R.layout.layout_contact_dept_item : R.layout.layout_member_selector_item;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, null);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final View itemView = holder.itemView;
        final MemberItem memberItem = itemDataList.get(position);
        int type = getItemViewType(position);
        if (type == TYPE_DEPT_ITEM) {
            TextView deptView = (TextView)itemView.findViewById(R.id.dept_name);
            deptView.setText(memberItem.bean.name);
            final View foldBtn = itemView.findViewById(R.id.fold_btn);
            final MemberBean memberBean = memberBeanMap.get(memberItem.bean.id);
            foldBtn.setActivated(!memberBean.fold);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    memberBean.fold = !memberBean.fold;
                    parseMemberBeanList();
                    notifyDataSetChanged();
                }
            });
        } else {
            final UserBean userBean = (UserBean)memberItem.bean;
            TextView accountView = (TextView)itemView.findViewById(R.id.user_account);
            TextView nameView = (TextView)itemView.findViewById(R.id.user_name);
            TextView positionView = (TextView)itemView.findViewById(R.id.user_position);
            accountView.setText(userBean.account);
            nameView.setText(userBean.name);
            positionView.setText(userBean.position);
            CheckBox checkBox = (CheckBox) itemView.findViewById(R.id.member_checkbox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        selectedSet.add(userBean.id);
                    } else {
                        selectedSet.remove(userBean.id);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    @Override
    public long getItemId(int position) {
        MemberItem item = itemDataList.get(position);
        return (item.type << 16) + item.bean.id;
    }

    @Override
    public int getItemViewType(int position) {
        return itemDataList.get(position).type;
    }
}
