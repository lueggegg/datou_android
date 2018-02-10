package com.example.luegg.oa.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.luegg.oa.R;

/**
 * Created by Administrator on 2016-06-22.
 */
abstract public class BaseActivity extends AppCompatActivity {
    protected String TAG;

    private TextView titleView;
    private View backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
    }

    public void setTitle(CharSequence title) {
        titleView.setText(title);
    }

    protected void hideBackButton() {
        backButton.setVisibility(View.GONE);
    }

    @Override
    public void setContentView(int resId) {
        super.setContentView(resId);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //Enable自定义的View
            actionBar.setCustomView(R.layout.layout_custom_title_bar);//设置自定义的布局：actionbar_custom
        }
        titleView = (TextView) findViewById(R.id.custom_bar_title_label);
        backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setTitle(TAG);

        initView();
        initData();
    }

    public <T extends Activity> void start( Class<T> clazz) {
        Context context = this;
        Intent intent = new Intent(context, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static <T extends Activity> void start(Context context, Class<T> clazz) {
        Intent intent = new Intent(context, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public void unInitialize() {

    }

    public void finish() {
        unInitialize();
        super.finish();
    }

    public static void home(Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);// "android.intent.action.MAIN"
        intent.addCategory(Intent.CATEGORY_HOME); //"android.intent.category.HOME"
        context.startActivity(intent);
    }

    abstract protected void initView();

    abstract protected void initData();

    protected Activity getActivity() {
        return this;
    }

    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
