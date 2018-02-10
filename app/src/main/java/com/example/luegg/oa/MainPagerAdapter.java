package com.example.luegg.oa;

import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.luegg.oa.base.bean.DeptBean;
import com.example.luegg.oa.base.bean.UserBean;
import com.example.luegg.oa.contact.ContactController;
import com.example.luegg.oa.contact.ContactRcViewAdapter;
import com.example.luegg.oa.job.main_job.MainJobController;
import com.example.luegg.oa.my.MyController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by luegg on 2017/11/29.
 */
public class MainPagerAdapter extends PagerAdapter {

    private int[] pageViewId = {
            R.layout.layout_main_job,
            R.layout.layout_contact,
            R.layout.layout_my
    };
    private MainJobController mainJobController;
    private ContactController contactController;
    private MyController myController;

    private List<View> containerList = new ArrayList<>();

    @Override
    public int getCount() {
        return pageViewId.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View cache;
        try {
            cache = containerList.get(position);
        } catch (Exception e) {
            cache = null;
        }
        if (cache != null) {
            container.addView(cache);
            return cache;
        }
        int layoutId = pageViewId[position];
        View layout =  LayoutInflater.from(container.getContext()).inflate(pageViewId[position], null);

        switch (layoutId) {
            case R.layout.layout_main_job:
                mainJobController = new MainJobController();
                mainJobController.init(layout);
                mainJobController.reload();
                break;
            case R.layout.layout_contact:
                contactController = new ContactController();
                contactController.init(layout);
                contactController.reload();
                break;
            case R.layout.layout_my:
                myController = new MyController();
                myController.init(layout);
                break;
        }
        container.addView(layout);
        containerList.add(position, layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    public void onResume() {
        if (mainJobController != null) {
            mainJobController.reload();
        }
    }
}
