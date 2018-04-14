package com.example.luegg.oa.contact;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.luegg.oa.R;
import com.example.luegg.oa.base.bean.UserBean;
import com.example.luegg.oa.base.http.ApiHttpRequest;

import java.util.List;

/**
 * Created by luegg on 2017/12/26.
 */
public class MemberSelectorController {
    private static final String TAG = "MemberSelectorController";

    private View container;
    private RecyclerView memberRcView;
    private MemberSelectorAdapter adapter;
    private List<UserBean> userListCache;
    private List<UserBean> originalFilterList;

    public void init(View container, List<UserBean> filterList) {
        this.container = container;
        this.originalFilterList = filterList;
        commonInit();
    }

    private void commonInit() {
        memberRcView = (RecyclerView) container.findViewById(R.id.member_selector_rc_view);
        memberRcView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        adapter = new MemberSelectorAdapter();
        memberRcView.setAdapter(adapter);

        load();
    }

    public List<Integer> getSelectedMember() {
        return adapter.getSelectedList();
    }


    public void filter(List<UserBean> filterList) {
        for (UserBean filterBean : filterList) {
            for (UserBean userBean : userListCache) {
                if (userBean.getUid() == filterBean.getUid()) {
//                    userListCache.remove(userBean);
                    userBean.state = -1;
                    break;
                }
            }
        }
        updateView();
    }

    public void filterUid(List<Integer> filterList) {
        for (Integer uid : filterList) {
            for (UserBean userBean : userListCache) {
                if (userBean.getUid() == uid) {
//                    userListCache.remove(userBean);
                    userBean.state = -1;
                    break;
                }
            }
        }
        updateView();
    }

    private void load() {
        if (this.container == null) return;
        new ApiHttpRequest.PostBuilder().url(ApiHttpRequest.getApiUrl("query_account_list"))
                .add("type", 1)
                .executeForArray(UserBean.class, new ApiHttpRequest.ObjectCallbackAdapter() {
                    @Override
                    public <T> void onArray(List<T> objectList) {
                        userListCache = (List<UserBean>) objectList;
                        if (originalFilterList != null && originalFilterList.size() > 0) {
                            filter(originalFilterList);
                        } else {
                            updateView();
                        }
                    }
                });
    }

    private void updateView() {
        adapter.setUserListData(userListCache);
        adapter.notifyDataSetChanged();
    }

}
