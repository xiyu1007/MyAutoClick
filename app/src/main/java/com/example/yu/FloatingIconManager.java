package com.example.yu;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ASUS
 */
public class FloatingIconManager extends Service{

    private static final int MAX_FLOATING_ICONS = 5;
    private Context context;
    private Handler handler;
    private static int idCounter = 0;
    private final int absMoveLength = 10;
    public  WindowManager windowManager;
    public final List<View> floatingViews = new ArrayList<>();
    private final List<FloatingIcon> floatingIcons = new ArrayList<>();
    private final FeatureSetting featureSetting = new FeatureSetting();
    private final String TAG =  "FloatingIconManager";

    private int initialX = 0;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;

//    public  FloatingIconManager() {
//    }
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    public  FloatingIconManager(Context context) {
        this.context = context;

        //TODO
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        MyApplication.windowManager = windowManager;

        //延时
        handler = new Handler(Looper.getMainLooper());

        //TODO 广播接收Feature Setting
        //广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyApplication.ACTION_ICON_SETTING_START);
        filter.addAction(MyApplication.ACTION_FEATURE_SETTING_STOPPED);
        // 可以继续添加其他动作
        context.registerReceiver(featureSettingReceiver, filter);

    }
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public void onCreate() {
        super.onCreate();

        //TODO
//        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//        MyApplication.windowManager = windowManager;

        //延时
        handler = new Handler(Looper.getMainLooper());

        //TODO 广播接收Feature Setting
        //广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyApplication.ACTION_ICON_SETTING_START);
        filter.addAction(MyApplication.ACTION_FEATURE_SETTING_STOPPED);
        // 可以继续添加其他动作
        this.registerReceiver(featureSettingReceiver, filter);
    }

    public void addFloatingIcon() {
        if (floatingViews.size() >= MAX_FLOATING_ICONS) {
            // 达到最大悬浮图标数量，不再添加新的图标
            ShowToast.show(this, "达到最大悬浮图标数量");
            return;
        }
        idCounter += 1;

        //TODO
        FloatingIcon floatingIcon = FloatingIconFactory.createFloatingIcon(context);
//        FloatingIcon floatingIcon = FloatingIconFactory.createFloatingIcon(this);
        floatingIcon.setId(idCounter);
        floatingIcon.setNumber();

        if(floatingIcon.dataWatcher == null){
            ShowToast.show(this,"空");
            Log.d(TAG,"获取到空属性图标");
            WriteToLog.writeLogToFile(this,"获取到空属性图标",true);
            return;
        }

        // 创建新的悬浮图标视图
        View floatingView =floatingIcon.getView();
//
        WindowManager.LayoutParams params = floatingIcon.returnParams();

//        LayoutInflater inflater = LayoutInflater.from(this);
//        View floatingView = inflater.inflate(R.layout.floating_icon_layout, null);


        windowManager.addView(floatingView, params);
        // 将悬浮图标添加到主 WindowManager
        floatingViews.add(floatingView);
        floatingIcons.add(floatingIcon);


        //TODO 需要修改的部分
        // 设置悬浮图标的拖动功能,如果超过500ms,则触发showFeatureSetting

        floatingView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        handler.postDelayed(longPressRunnable, 500);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int deltaX = (int) (event.getRawX() - initialTouchX);
                        int deltaY = (int) (event.getRawY() - initialTouchY);

                        params.x = initialX + deltaX;
                        params.y = initialY + deltaY;

                        windowManager.updateViewLayout(floatingView, params);
                        floatingIcon.setParams(params);

                        if (Math.abs(deltaX) > absMoveLength || Math.abs(deltaY) > absMoveLength) {
                            handler.removeCallbacks(longPressRunnable);
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        handler.removeCallbacks(longPressRunnable);
                        floatingView.performClick(); // 触发点击操作
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

    public void updateWindowManagerParams(WindowManager windowManager,List<View> floatingViews,boolean IS_TOUCHABLE){
        for (View floatingView : floatingViews) {
            // 获取当前视图的 WindowManager.LayoutParams 对象
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) floatingView.getLayoutParams();

            // 修改需要更新的属性
            if(IS_TOUCHABLE){
                params.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

            }else {
                params.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            }
            // 更新视图的属性
            windowManager.updateViewLayout(floatingView, params);
        }

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
            ShowToast.show(this,"空");
            return;
        }
        floatingIcon.dataWatcher.printAttributes(TAG);
        MyApplication.WHICH_ICON = floatingIcon.getId();
//        Intent startService = new Intent(this, FeatureSetting.class);
//        this.startService(startService);

        //TODO
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
                        boolean status = floatingIcons.get(MyApplication.WHICH_ICON -1).updateAttributes(MyApplication.getconveydatawatcher());
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
    public void onDestroy() {
        super.onDestroy();
        // 注销广播接收器
        unregisterReceiver(featureSettingReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }


    public void stopFeatureSettingService() {
        // 停止服务
        Intent stopIntent = new Intent(this, featureSetting.getClass());
        this.stopService(stopIntent);
    }

    public int getIdCounter() {
        return idCounter;
    }


}
