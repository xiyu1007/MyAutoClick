package com.example.yu;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ASUS
 */
public class FloatingIconManager extends AppCompatActivity {

    private static final int MAX_FLOATING_ICONS = 5;
    private final Context context;
    private static int idCounter = 0;
    private final int lofi_data = 10;
    private final WindowManager windowManager;
    private final List<View> floatingViews = new ArrayList<>();
    private final List<FloatingIcon> floatingIcons = new ArrayList<>();
    private final FeatureSetting featureSetting = new FeatureSetting();

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final String TAG =  "FloatingIconManager";

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    public FloatingIconManager(Context context) {
        this.context = context;
        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyApplication.ACTION_ICON_SETTING_START);
        filter.addAction(MyApplication.ACTION_FEATURE_SETTING_STOPPED);
        // 可以继续添加其他动作
        context.registerReceiver(featureSettingReceiver, filter);

    }

    public void addFloatingIcon() {
        if (floatingViews.size() >= MAX_FLOATING_ICONS) {
            // 达到最大悬浮图标数量，不再添加新的图标
            ShowToast.show(context, "达到最大悬浮图标数量");
            return;
        }
        idCounter += 1;
        FloatingIcon floatingIcon = FloatingIconFactory.createFloatingIcon(context);
        floatingIcon.setId(idCounter);
        floatingIcon.setNumber();

        if(floatingIcon.dataWatcher == null){
            ShowToast.show(context,"空");
            return;
        }



        // 创建新的悬浮图标视图
        View floatingView =floatingIcon.getView();
        WindowManager.LayoutParams params = floatingIcon.getParams();

        // 将悬浮图标添加到主 WindowManager
        windowManager.addView(floatingView, params);
        floatingViews.add(floatingView);
        floatingIcons.add(floatingIcon);

        // 设置悬浮图标的拖动功能
        floatingView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            private boolean isMoving = false;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 获取初始位置
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        isMoving = false;
                        // 2秒后执行长按操作
                        handler.postDelayed(longPressRunnable, 500);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // 计算移动距离
                        int deltaX = (int) (event.getRawX() - initialTouchX);
                        int deltaY = (int) (event.getRawY() - initialTouchY);

                        // 更新悬浮图标的位置
                        params.x = initialX + deltaX;
                        params.y = initialY + deltaY;

                        // 更新悬浮图标的显示位置
                        windowManager.updateViewLayout(floatingView, params);
                        floatingIcon.setParams(params);

                        // 判断是否移动过
                        if (Math.abs(deltaX) > lofi_data || Math.abs(deltaY) > lofi_data) {
                            isMoving = true;
                            handler.removeCallbacks(longPressRunnable); // 取消长按操作
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        handler.removeCallbacks(longPressRunnable); // 取消长按操作
//                        if (!isMoving) {
//                            // 执行点击操作
//                            showFeatureSetting(floatingIcon);
//                        }
                        return true;
                    default:
                        return false;
                }

            }

            // 长按操作的Runnable
            private final Runnable longPressRunnable = new Runnable() {
                @Override
                public void run() {
                    // 长按操作
                    showFeatureSetting(floatingIcon);
                }
            };
        });
    }

    public void removeLastFloatingIcon(int id) {
        if (!floatingViews.isEmpty()) {
            // 移除悬浮图标视图
            View floatingView = floatingViews.remove(id - 1);
            windowManager.removeView(floatingView);

            floatingIcons.remove(id - 1);
            if (floatingIcons.size() + 1 > id) {
                for (int i = id; i < floatingIcons.size() - 1; i++) {
                    FloatingIcon floatingIcon = floatingIcons.get(i);
                    floatingIcon.setId(i);
                }
            }
            idCounter--;
        }
    }

    public void showFeatureSetting(FloatingIcon floatingIcon) {
        MyApplication.setConveryDataWatcher(floatingIcon.dataWatcher);
        if(floatingIcon.dataWatcher == null){
            ShowToast.show(context,"空");
            return;
        }
        floatingIcon.dataWatcher.printAttributes(TAG);
        MyApplication.WHICH_ICON = floatingIcon.getId();
        Intent startService = new Intent(context, FeatureSetting.class);
        context.startService(startService);
    }
    private final BroadcastReceiver featureSettingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MyApplication.ACTION_FEATURE_SETTING_STOPPED.equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    int intValue = extras.getInt(MyApplication.MESSAGE_HAD_SET_VIEW);
                    if (intValue == MyApplication.StatusOK) {
                        boolean status = floatingIcons.get(MyApplication.WHICH_ICON -1).updateAttributes(MyApplication.getConveryDataWatcher());
                        floatingIcons.get(MyApplication.WHICH_ICON -1).dataWatcher.printAttributes(TAG);
                        if (status){
                            WriteToLog.writeLogToFile(context,TAG+"修改浮窗属性成功",false);
                        }
                        else {
                            WriteToLog.writeLogToFile(context,TAG+"修改浮窗属性失败",true);
                        }
                    }
                }
            } else if (MyApplication.ACTION_ICON_SETTING_START.equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    int intValue = extras.getInt(MyApplication.MESSAGE_ICON_SETTING_START);
                }
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销广播接收器
        unregisterReceiver(featureSettingReceiver);
    }

    public void stopFeatureSettingService() {
        // 停止服务
        Intent stopIntent = new Intent(context, featureSetting.getClass());
        context.stopService(stopIntent);
    }

    public int getIdCounter() {
        return idCounter;
    }

}
