package com.example.luegg.oa.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.luegg.oa.R;
import com.example.luegg.oa.base.OaApplication;

/**
 * Created by Administrator on 2016-07-12.
 */
public class NotifyCenter {
    private static final String STAG = "NotifyCenter";
    public static final int DEFAULT_NOTIFY_ID = 12345678;

    public static class NotifyParameter {
        public int id = DEFAULT_NOTIFY_ID;
        public Context context = OaApplication.instance;
        public String title = "title";
        public String message = "message";
        public int smallIconId = R.drawable.oa_logo_small;
        public long when = System.currentTimeMillis();
        public boolean autoCancel = true;
        public PendingIntent pendingIntent;
        public int defaults = Notification.DEFAULT_ALL;
        public int priority = NotificationCompat.PRIORITY_DEFAULT;
        public int extraFlags = 0;
        public NotificationCompat.Style style = null;
    }

    public static void notify(NotifyParameter parameter) {
        if (parameter.pendingIntent == null) {
            Log.i(STAG, "the intent is null, cannot notify!!!");
            return;
        }
        if (parameter.style instanceof NotificationCompat.BigTextStyle) {
            ((NotificationCompat.BigTextStyle) parameter.style).bigText(parameter.message);
        }
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(parameter.context)
                        .setAutoCancel(parameter.autoCancel)
                        .setContentIntent(parameter.pendingIntent)
                        .setContentText(parameter.message)
                        .setWhen(parameter.when)
                        .setSmallIcon(parameter.smallIconId)
                        .setContentTitle(parameter.title)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setDefaults(parameter.defaults)
                        .setStyle(parameter.style)
                        .setPriority(parameter.priority);
        Notification notification = builder.build();
        notification.flags |= parameter.extraFlags;
        NotificationManager notificationManager = (NotificationManager)parameter.context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(parameter.id, notification);
    }
}
