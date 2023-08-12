package com.example.yu;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ASUS
 */
public class FloatingIconManager extends Service {

    private static final int MAX_FLOATING_ICONS = 5;
    private Context context;
    private static int idCounter = 0;
    private final int lofi_data = 10;
    public  WindowManager windowManager;
    private final List<View> floatingViews = new ArrayList<>();
    private final List<FloatingIcon> floatingIcons = new ArrayList<>();
    private final FeatureSetting featureSetting = new FeatureSetting();

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final String TAG =  "FloatingIconManager";

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    public  FloatingIconManager() {

    }
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        //TODO 广播接收Feature Setting
        //广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyApplication.ACTION_ICON_SETTING_START);
        filter.addAction(MyApplication.ACTION_FEATURE_SETTING_STOPPED);
        // 可以继续添加其他动作
        this.registerReceiver(featureSettingReceiver, filter);

        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//        View floatingView = new View(this);

        addFloatingIcon();
    }

    public void addFloatingIcon() {
        if (floatingViews.size() >= MAX_FLOATING_ICONS) {
            // 达到最大悬浮图标数量，不再添加新的图标
            ShowToast.show(this, "达到最大悬浮图标数量");
            return;
        }
        idCounter += 1;
        FloatingIcon floatingIcon = FloatingIconFactory.createFloatingIcon(this);
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

        // 设置悬浮图标的拖动功能
//        floatingView.setOnTouchListener(new View.OnTouchListener() {
//            private int initialX;
//            private int initialY;
//            private float initialTouchX;
//            private float initialTouchY;
//            private boolean isMoving = false;
//
//            @SuppressLint("ClickableViewAccessibility")
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        // 获取初始位置
//                        initialX = params.x;
//                        initialY = params.y;
//                        initialTouchX = event.getRawX();
//                        initialTouchY = event.getRawY();
//                        isMoving = false;
//                        // 2秒后执行长按操作
//                        handler.postDelayed(longPressRunnable, 500);
//                        return true;
//                    case MotionEvent.ACTION_MOVE:
//                        // 计算移动距离
//                        int deltaX = (int) (event.getRawX() - initialTouchX);
//                        int deltaY = (int) (event.getRawY() - initialTouchY);
//
//                        // 更新悬浮图标的位置
//                        params.x = initialX + deltaX;
//                        params.y = initialY + deltaY;
//
//                        MyApplication.pritfLine();
//
//                        // 更新悬浮图标的显示位置
//
//
//                        windowManager.updateViewLayout(floatingView, params);
//                        floatingIcon.setParams(params);
//
//                        Rect rect = new Rect();
//                        floatingIcon.getView().getGlobalVisibleRect(rect);
//
//                        Log.d(TAG,"X:"+floatingIcon.getParams().x+"  Y:"+floatingIcon.getView().getWidth());
//
//
//                        int left = rect.left;
//                        int top = rect.top;
//                        int right = rect.right;
//                        int bottom = rect.bottom;
//
//                        Log.d(TAG,"left:"+left+"  right:"+right);
//                        Log.d(TAG,"top:"+top+"  bottom:"+bottom);
//
//                        // 判断是否移动过
//                        if (Math.abs(deltaX) > lofi_data || Math.abs(deltaY) > lofi_data) {
//                            isMoving = true;
//                            handler.removeCallbacks(longPressRunnable); // 取消长按操作
//                        }
//                        return true;
//                    case MotionEvent.ACTION_UP:
//                        handler.removeCallbacks(longPressRunnable); // 取消长按操作
////                        if (!isMoving) {
////                            // 执行点击操作
////                            showFeatureSetting(floatingIcon);
////                        }
//                        return true;
//                    default:
//                        return false;
//                }
//
//            }
//
//            // 长按操作的Runnable
//            private final Runnable longPressRunnable = new Runnable() {
//                @Override
//                public void run() {
//                    // 长按操作
//                    showFeatureSetting(floatingIcon);
//                }
//            };
//        });
    }


    public WindowManager.LayoutParams setView() {
        /*
        FLAG_NOT_FOCUSABLE: 这个标志指示窗口不接收焦点，不会响应用户的输入事件。
        设置此标志后，窗口上的控件（例如EditText）将无法获取焦点并接收用户输入。
        FLAG_NOT_TOUCH_MODAL: 这个标志指示窗口在接收触摸事件时，不会将触摸事件传递给后面的窗口。
        如果未设置此标志，当用户点击窗口之外的区域时，触摸事件会被传递给后面的窗口。
        FLAG_SPLIT_TOUCH: 这个标志指示窗口支持分割触摸事件，允许多个窗口同时处理触摸事件。
        FLAG_WATCH_OUTSIDE_TOUCH: 这个标志指示窗口监视在窗口之外的触摸事件
        */
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH,
                PixelFormat.TRANSLUCENT
        );
//         |WindowManager.LayoutParams.FLAG_DIM_BEHIND,


        params.x = 0;
        params.y = 0;
        params.gravity = android.view.Gravity.CENTER;
        return params;

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
        Intent startService = new Intent(this, FeatureSetting.class);
        this.startService(startService);
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

    @Nullable
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
