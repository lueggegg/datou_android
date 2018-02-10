package com.example.luegg.oa.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.example.luegg.oa.base.bean.UserBean;
import com.example.luegg.oa.login.LoginUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by luegg on 2017/12/1.
 */
public class CommonUtil {
    public static final String TAG = "CommonUtil";

    public static void showToast(final String msg) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    showToast(msg);
                }
            });
        } else {
            Toast.makeText(OaApplication.instance, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static void call(String phone) {
        Intent intent=new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            OaApplication.instance.startActivity(intent);
        } catch (SecurityException e) {
            showToast("拔号权限被关闭，请手动打开");
        }
    }

    public static String getHash(String data) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(data.getBytes());
            MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            sha1.update(toHexString(md5.digest()).getBytes());
            return toHexString(sha1.digest());
        } catch (NoSuchAlgorithmException e) {
            Logger.e(TAG, e.toString());
            return null;
        }
    }

    public static String toHexString(byte[] data) {
        StringBuffer sb = new StringBuffer();
        for (byte item : data) {
            String temp = Integer.toHexString(item & 0xff);
            if (temp.length() == 1) {
                sb.append("0");
            }
            sb.append(temp);
        }
        return sb.toString();
    }

    public static String getWholeUrl(String path) {
        return Constant.HOST + "/" + path;
    }

    public static boolean isAuthorized(int operationMask) {
        UserBean myself = LoginUtil.getMyself();
        return myself != null && (myself.authority <= 8 || (myself.operation_mark & operationMask) != 0);
    }

    public static String wrapJobContent(String content) {
        return "{" + content + "}";
    }

    public static void showCommonDialog(Context context, String title, String msg,
                                        DialogInterface.OnClickListener okListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", okListener);
        AlertDialog dialog = builder.create();
        dialog.show();}
}
