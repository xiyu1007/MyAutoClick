package com.example.yu;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.List;
import java.util.Objects;

/**
 * @author XiYU
 */
public class MainActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static FloatingIconManager floatingIconManager;
    private final String TAG = "MainActivity";
    private Context context;
    private final String PACKAGE_NAME = "com.example.yu";



    @SuppressLint({"ResourceType", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        context = this;

        MyApplication.context = this;

//        floatingIconManager = new FloatingIconManager(this);

        // 处理按钮点击事件，用于添加悬浮图标
        Button addButton = findViewById(R.id.add_button);
        Button delButton = findViewById(R.id.delete_button);
        Button setButton = findViewById(R.id.setting_button);

        Button beginButton = findViewById(R.id.begin_button);


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在这里添加代码来添加悬浮图标
                // 确保在添加悬浮图标之前已经检查了权限
                onAddButtonClicked(v);
            }
        });
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在这里添加代码来添加悬浮图标
                // 确保在添加悬浮图标之前已经检查了权限
                onDelButtonClicked(v);
            }
        });
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在这里添加代码来添加悬浮图标
                // 确保在添加悬浮图标之前已经检查了权限
                onSetButtonClicked(v);
            }
        });

        beginButton.setOnClickListener(this::beginClick);

    }

    private void beginClick(View view) {


    }

    /**
     * 开启无障碍服务
     */
    public void onSetButtonClicked(View view) {
        if (!hasOverlayPermission()){
            openOverlayPermission();
        }
        if (!hasAccessibilityServiceEnabled()){
            openAccessibilitySettings();
        }
    }

    private boolean hasOverlayPermission() {
        return Settings.canDrawOverlays(this);
    }
    private boolean hasAccessibilityServiceEnabled() {
        //检测当前无障碍服务已开启的应用列表信息
        AccessibilityManager am = (AccessibilityManager) getSystemService
                (Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> accessibilityServiceList =
                am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServiceList) {
            String packageName = info.getResolveInfo().serviceInfo.packageName;
//            MyApplication.pritfLine();
//            Log.e(TAG, "当前已开启的无障碍服务的信息: " + info.getResolveInfo().toString());
            if(Objects.equals(packageName, PACKAGE_NAME)){
                return MyApplication.STATUS_TRUE;
            }
        }
        return MyApplication.STATUS_FALSE;
    }


    public void onAddButtonClicked(View view) {
//        floatingIconManager.addFloatingIcon();
        Intent startService = new Intent(context, FloatingIconManager.class);
        context.startService(startService);

    }
    public void onDelButtonClicked(View view) {
        int id = floatingIconManager.getIdCounter();
        floatingIconManager.removeLastFloatingIcon(id);
    }


    public void openOverlayPermission(){
        // 若未授予权限，则向用户请求权限
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        requestOverlayPermissionLauncher.launch(intent);

    }
    private void openAccessibilitySettings() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        requestAccessibilityPermissionLauncher.launch(intent);

        //跳转到系统设置页面，由用户手动点击确认是否开启对应的无障碍服务
        //Intent intent = new Intent();
        //intent.setAction(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        ////intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //startActivity(intent);
    }

    ActivityResultLauncher<Intent> requestAccessibilityPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // 用户处理权限请求后，再次检查是否已授予权限
                if (!hasAccessibilityServiceEnabled()) {
                    // 用户拒绝了权限请求，显示提示信息
                    ShowToast.show(MainActivity.this, "授予无障碍权限失败");
                    MyApplication.pritfLine();
                    Log.e(TAG, PACKAGE_NAME+"未开启无障碍服务");
                    WriteToLog.writeLogToFile(context,"授予无障碍权限失败",false);

                }else {
                    ShowToast.show(MainActivity.this, "成功获取权限");
                    MyApplication.pritfLine();
                    Log.e(TAG, TAG +PACKAGE_NAME+ "开启无障碍服务");
                    WriteToLog.writeLogToFile(context,"成功获取权限",false);
                }
            }
    );

    ActivityResultLauncher<Intent> requestOverlayPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // 用户处理权限请求后，再次检查是否已授予权限
                if (!Settings.canDrawOverlays(this)) {
                    // 用户拒绝了权限请求，显示提示信息
                    ShowToast.show(MainActivity.this, "授予悬浮权限失败");
                }
            }
    );




    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}






