package com.leonteqsecurity.usageappreminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.Manifest;
import android.Manifest.permission;
import android.widget.Toast;


public class ManageTimer extends AppCompatActivity {
    private static final String USAGE_STATS = "android.permission.USAGE_STATS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_timer);

        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().hide();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "Default Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }




    }


    public  void SetTimer(View view)
    {
//        TextView fruits=(TextView) findViewById(R.id.textView);
//        Spinner fruit=(Spinner) findViewById(R.id.spinner);
//        String selected=String.valueOf(fruit.getSelectedItem());
//        fruits.setText(selected);


        // Get a reference to the NotificationManager
        NotificationManager manager = getSystemService(NotificationManager.class);

// Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.baseline_data_usage_24)
                .setContentTitle("My Notification")
                .setContentText("This is my notification")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

// Display the notification
        manager.notify(1, builder.build());
        Intent intent = new Intent(this, YouTubeUsageTrackerService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getPackageName());
            if (mode == AppOpsManager.MODE_ALLOWED) {
                // Permission granted
                // Start the service here
                Log.d("ManageTimer", "Starting YouTubeUsageTrackerService");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                    startService(intent);

                } else {
                    startService(intent);

                }
            } else {
                // Permission not granted, show permission request dialog
                Toast.makeText(this, "Grant permission", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            }
        } else {
            // Usage Access permission not required below Lollipop
            // Start the service here
            startService(intent);
        }












    }



}