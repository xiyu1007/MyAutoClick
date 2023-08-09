package com.example.yu;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;

/**
 * @author XiYU
 */
public class MainActivity extends AppCompatActivity {

    private FloatingIconManager floatingIconManager;
    private final String TAG = "MainActivity";

    private final String ACCESSIBILITY_SERVICE = "accessibility_service";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        floatingIconManager = new FloatingIconManager(this);

        // 处理按钮点击事件，用于添加悬浮图标
        Button addButton = findViewById(R.id.add_button);
        Button delButton = findViewById(R.id.delete_button);
        Button beginButton = findViewById(R.id.begin_button);
        Button setButton = findViewById(R.id.setting_button);

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
        Intent intent = new Intent(this, MyAccessibilityService.class);
        startService(intent);
        // Start your MyAccessibilityService here
        MyAccessibilityService myAccessibilityService = (MyAccessibilityService)
                getSystemService(MyAccessibilityService.class);
        if (myAccessibilityService != null) {
            ShowToast.show(MainActivity.this, "mmmyyy");

            myAccessibilityService.performClick();
        } else {
            ShowToast.show(MainActivity.this, "affafaf");

        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }



    public void onAddButtonClicked(View view) {
        floatingIconManager.addFloatingIcon();

    }
    public void onDelButtonClicked(View view) {
        int id = floatingIconManager.getIdCounter();
        floatingIconManager.removeLastFloatingIcon(id);
    }


    public void onSetButtonClicked(View view) {
        if (hasAccessibilityServiceEnabled()&&hasOverlayPermission()){
            ShowToast.show(MainActivity.this, "已有悬浮权限");
        }else {
             if(openAccessibilitySettings()){
                 ShowToast.show(MainActivity.this, "授予无障碍权限成功");
             }else {
                 ShowToast.show(MainActivity.this, "授予无障碍权限失败");
             }
        }
    }

    public boolean openOverlayPermission(){
        // 若未授予权限，则向用户请求权限
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        requestOverlayPermissionLauncher.launch(intent);
        return MyApplication.STATUS_FALSE;
    }
    private boolean openAccessibilitySettings() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
//        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//        requestAccessibilityPermissionLauncher.launch(intent);
        return MyApplication.STATUS_FALSE;
    }
    private boolean hasOverlayPermission() {
        return Settings.canDrawOverlays(this);
    }
    private boolean hasAccessibilityServiceEnabled() {
        int accessibilityEnabled = Settings.Secure.getInt(
                getContentResolver(),
                android.provider.Settings.Secure.ACCESSIBILITY_ENABLED,
                0
        );

        return accessibilityEnabled == 1;
    }



    ActivityResultLauncher<Intent> requestOverlayPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // 用户处理权限请求后，再次检查是否已授予权限
                if (Settings.canDrawOverlays(this)) {
                    // 已授予权限，可以继续添加悬浮图标
                    floatingIconManager.addFloatingIcon();
                }else {
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






