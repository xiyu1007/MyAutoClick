package com.example.yu;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

/**
 * @author XiYU
 */
public class MainActivity extends AppCompatActivity {

    private FloatingIconManager floatingIconManager;
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        floatingIconManager = new FloatingIconManager(this);

        // 处理按钮点击事件，用于添加悬浮图标
        Button addButton = findViewById(R.id.add_button);
        Button delButton = findViewById(R.id.delete_button);
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
    }

    public void onAddButtonClicked(View view) {
        // 检查是否拥有SYSTEM_ALERT_WINDOW权限
        if (!Settings.canDrawOverlays(this)) {
            // 若未授予权限，则向用户请求权限
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            requestOverlayPermissionLauncher.launch(intent);
        } else {
            // 已授予权限，可以继续添加悬浮图标
            floatingIconManager.addFloatingIcon();
        }
    }
    public void onDelButtonClicked(View view) {
        int id = floatingIconManager.getIdCounter();
        floatingIconManager.removeLastFloatingIcon(id);
    }
    public void onSetButtonClicked(View view) {
//        dataWatcher = createNewDataWatcherWithDefaultValues();
        // 启动 FeatureSetting 服务并绑定
//        floatingIconManager.showFeatureSetting(dataWatcher);
        // 获取 FeatureSetting 的 Binder 对象
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



