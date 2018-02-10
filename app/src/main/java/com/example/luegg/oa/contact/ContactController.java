package com.example.luegg.oa.contact;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.example.luegg.oa.R;
import com.example.luegg.oa.base.CommonUtil;
import com.example.luegg.oa.base.bean.UserBean;
import com.example.luegg.oa.base.http.ApiHttpRequest;

import java.util.List;

/**
 * Created by luegg on 2017/12/2.
 */
public class ContactController {
    private View container;
    private RecyclerView contactRcView;
    private ContactRcViewAdapter contactAdapter;
    private View detailContainer;
    private ApiHttpRequest.ObjectCallback queryCallback;
    private ApiHttpRequest.ObjectCallback searchCallback;
    private EditText nameEdit;
    private List<UserBean> userListCache;
    private View clearBtn;
    private PtrClassicFrameLayout ptrClassicFrameLayout;
    private boolean refreshing = false;

    public void init(View container) {
        if (this.container != null) return;

        this.container = container;
        contactRcView = (RecyclerView) container.findViewById(R.id.contact_rc_view);
        contactRcView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        contactAdapter = new ContactRcViewAdapter();
        contactRcView.setAdapter(contactAdapter);
        detailContainer = container.findViewById(R.id.contact_detail);
        contactAdapter.setUserItemClickListener(new ContactRcViewAdapter.OnUserItemClickListener() {
            @Override
            public void onClick(final UserBean bean) {
                detailContainer.setVisibility(View.VISIBLE);
                TextView view;
                view = (TextView)detailContainer.findViewById(R.id.detail_name);
                view.setText(bean.name != null? bean.name : "");
                view = (TextView)detailContainer.findViewById(R.id.detail_cellphone);
                view.setText(bean.cellphone != null? bean.cellphone : "");
                view = (TextView)detailContainer.findViewById(R.id.detail_email);
                view.setText(bean.email != null? bean.email : "");
                view = (TextView)detailContainer.findViewById(R.id.detail_wx);
                view.setText(bean.wehcat != null? bean.wehcat : "");
                view = (TextView)detailContainer.findViewById(R.id.detail_qq);
                view.setText(bean.qq != null? bean.qq : "");
                View callBtn = detailContainer.findViewById(R.id.call_btn);
                callBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtil.call(bean.cellphone);
                    }
                });
            }
        });
        View closeDetailBtn = detailContainer.findViewById(R.id.close_contact_detail);
        closeDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailContainer.setVisibility(View.GONE);
            }
        });

        queryCallback = new ApiHttpRequest.ObjectCallbackAdapter() {
            @Override
            public <T> void onArray(List<T> objectList) {
                if (refreshing) {
                    ptrClassicFrameLayout.refreshComplete();
                }
                refreshing = false;
                userListCache = (List<UserBean>) objectList;
                contactAdapter.setFoldDefault(true);
                contactAdapter.setUserListData(userListCache);
                contactAdapter.notifyDataSetChanged();
            }
        };

        searchCallback = new ApiHttpRequest.ObjectCallbackAdapter() {
            @Override
            public <T> void onArray(List<T> objectList) {
                clearBtn.setVisibility(View.VISIBLE);
                List<UserBean> searchResult = (List<UserBean>) objectList;
                contactAdapter.setFoldDefault(false);
                contactAdapter.setUserListData(searchResult);
                contactAdapter.notifyDataSetChanged();
            }
        };

        nameEdit = (EditText) container.findViewById(R.id.contact_search_edit);
        View searchBtn = container.findViewById(R.id.contact_search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
        clearBtn = container.findViewById(R.id.contact_clear_search_btn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSearch();
            }
        });

        initPullToRefresh();
    }

    private void initPullToRefresh() {
        ptrClassicFrameLayout = (PtrClassicFrameLayout)container.findViewById(R.id.contact_list_container);
        ptrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (refreshing) return;
                detailContainer.setVisibility(View.GONE);
                refreshing = true;
                reload();
            }
        });
    }

    public void reload() {
        if (this.container == null) return;
        new ApiHttpRequest.PostBuilder().url(ApiHttpRequest.getApiUrl("query_account_list"))
                .add("type", 1)
                .executeForArray(UserBean.class, queryCallback);
    }

    private void search() {
        String name = nameEdit.getText().toString();
        if (name.equals("")) {
            CommonUtil.showToast("请输入搜索内容");
            return;
        }
        new ApiHttpRequest.PostBuilder().url(ApiHttpRequest.getApiUrl("query_account_list"))
                .add("type", 1)
                .add("name", name)
                .executeForArray(UserBean.class, searchCallback);
    }

    private void clearSearch() {
        clearBtn.setVisibility(View.GONE);
        nameEdit.setText("");
        contactAdapter.setFoldDefault(true);
        contactAdapter.setUserListData(userListCache);
        contactAdapter.notifyDataSetChanged();
    }
}
