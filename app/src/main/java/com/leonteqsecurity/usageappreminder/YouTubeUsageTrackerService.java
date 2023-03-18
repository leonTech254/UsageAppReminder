package com.leonteqsecurity.usageappreminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class YouTubeUsageTrackerService extends Service {
    private SharedPreferences sharedPreferences;


    private static final long INTERVAL = 20000; // Check usage every 60 seconds
    private static final String YOUTUBE_PACKAGE_NAME = "com.google.android.youtube";
    private static  int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "UsageAppReminder";

    private UsageStatsManager mUsageStatsManager;
    private  String duration;
    private  String durationType;
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            long usageTime = getUsageDuration(YOUTUBE_PACKAGE_NAME, currentTime - INTERVAL, currentTime);
            System.out.println("check youtuber");
            Log.d("TAG", "message");
            sharedPreferences =getSharedPreferences("time", Context.MODE_PRIVATE);
             duration = sharedPreferences.getString("duration", "");
             durationType = sharedPreferences.getString("durationType", "");
            System.out.println(durationType.equals("Minutes"));

            if (durationType.equals("minutes"))
            {
                if (usageTime >= Integer.parseInt(duration) * 60 * 1000) {
                    showNotification();
                    NOTIFICATION_ID = YOUTUBE_PACKAGE_NAME.hashCode();


                }

            }else
            {
                if (usageTime >= Integer.parseInt(duration)*60 * 60 * 1000) {
                    showNotification();
                    NOTIFICATION_ID = YOUTUBE_PACKAGE_NAME.hashCode();
                }
            }


            mHandler.postDelayed(mRunnable, INTERVAL);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("UsageTrackerService", "Service started");
        mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);

        // Create notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Usage App Reminder", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle("Usage App Reminder")
                    .setContentText("Service is running in the background.")
                    .setSmallIcon(R.drawable.baseline_data_usage_24)
                    .build();

            startForeground(NOTIFICATION_ID, notification);
        }

        mHandler.post(mRunnable);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private long getUsageDuration(String packageName, long startTime, long endTime) {
        UsageEvents.Event event = new UsageEvents.Event();
        UsageEvents usageEvents = mUsageStatsManager.queryEvents(startTime, endTime);
        long usageDuration = 0;
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event);
            if (event.getPackageName().equals(packageName)) {
                usageDuration += event.getTimeStamp() - usageDuration;
            }
        }
        return usageDuration;
    }

    private void showNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Usage App Reminder", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Usage App Reminder")
                .setContentText("You have been using YouTube for more than "+duration +" "+ durationType+ ". Consider taking a break and using other apps.")
                .setSmallIcon(R.drawable.baseline_data_usage_24);

        Notification notification = builder.build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
