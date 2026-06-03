package com.homeautomation;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "HomeAutomationChannel";
    private EditText editSSID;
    private EditText editLat;
    private EditText editLng;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        createNotificationChannel();
        checkPermissions();
    }

    private void initViews() {
        editSSID = findViewById(R.id.editSSID);
        editLat = findViewById(R.id.editLat);
        editLng = findViewById(R.id.editLng);
        statusText = findViewById(R.id.statusText);

        Button btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(v -> startAutomation());

        Button btnTest = findViewById(R.id.btnTest);
        btnTest.setOnClickListener(v -> testAutomation());

        Button btnUU = findViewById(R.id.btnUU);
        btnUU.setOnClickListener(v -> openUURemote());

        Button btnMijia = findViewById(R.id.btnMijia);
        btnMijia.setOnClickListener(v -> openMijia());

        // Load saved settings
        loadSettings();
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "回家自动化",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("回家自动化通知");
            channel.enableVibration(true);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void checkPermissions() {
        String[] permissions = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_WIFI_STATE,
                "android.permission.POST_NOTIFICATIONS"
        };

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{permission}, 1);
                }
            }
        }
    }

    private void startAutomation() {
        String ssid = editSSID.getText().toString();
        String latStr = editLat.getText().toString();
        String lngStr = editLng.getText().toString();

        if (ssid.isEmpty()) {
            Toast.makeText(this, "请输入WiFi名称", Toast.LENGTH_SHORT).show();
            return;
        }

        saveSettings(ssid, latStr, lngStr);
        statusText.setText("自动化已启动，等待连接WiFi: " + ssid);
        Toast.makeText(this, "自动化已启动", Toast.LENGTH_SHORT).show();
    }

    private void testAutomation() {
        statusText.setText("正在测试自动化流程...");
        new Thread(() -> {
            try {
                // 等待10秒
                Thread.sleep(10000);

                // 打开UU远程
                openUURemote();
                Thread.sleep(2000);

                // 打开米家
                openMijia();
                Thread.sleep(2000);

                // 发送通知
                runOnUiThread(() -> {
                    sendNotification("测试完成", "自动化流程已执行");
                    statusText.setText("测试完成");
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void openUURemote() {
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage("com.bbk.uu");
            if (intent != null) {
                startActivity(intent);
                statusText.setText("已打开UU远程");
            } else {
                statusText.setText("未找到UU远程应用");
            }
        } catch (Exception e) {
            statusText.setText("打开UU远程失败: " + e.getMessage());
        }
    }

    private void openMijia() {
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage("com.xiaomi.smarthome");
            if (intent != null) {
                startActivity(intent);
                statusText.setText("已打开米家");
            } else {
                statusText.setText("未找到米家应用");
            }
        } catch (Exception e) {
            statusText.setText("打开米家失败: " + e.getMessage());
        }
    }

    private void sendNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(1, builder.build());
        }
    }

    private void saveSettings(String ssid, String lat, String lng) {
        getSharedPreferences("HomeAutomation", MODE_PRIVATE)
                .edit()
                .putString("ssid", ssid)
                .putString("lat", lat)
                .putString("lng", lng)
                .apply();
    }

    private void loadSettings() {
        var prefs = getSharedPreferences("HomeAutomation", MODE_PRIVATE);
        editSSID.setText(prefs.getString("ssid", ""));
        editLat.setText(prefs.getString("lat", ""));
        editLng.setText(prefs.getString("lng", ""));
    }
}