package com.example.yu;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author ASUS
 */
public class FloatButton extends Service{
    private WindowManager windowManager;
    private View floatingButtonsView;
    private WindowManager.LayoutParams params;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();

        // 检查悬浮窗口权限
        if (!Settings.canDrawOverlays(this)) {
            //若无权限则请求权限
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            stopSelf(); // 停止服务，因为悬浮窗口权限未授予
            return;
        }

        // 如果有权限或在Android版本低于Marshmallow时，显示悬浮按钮
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        LayoutInflater inflater = LayoutInflater.from(this);
        floatingButtonsView = inflater.inflate(R.layout.float_button, null);

        // 悬浮窗口的参数设置
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.START | Gravity.TOP;
        params.x = 0;
        params.y = 0;

        windowManager.addView(floatingButtonsView, params);

// 浮窗移动
        Button move = floatingButtonsView.findViewById(R.id.move);
        move.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            private boolean isDragging = false;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        isDragging = false;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        if (!isDragging) {
                            // Check if the touch moved enough to consider it a drag
                            if (Math.abs(event.getRawX() - initialTouchX) > 10 || Math.abs(event.getRawY() - initialTouchY) > 10) {
                                isDragging = true;
                            }
                        }
                        if (isDragging) {
                            int deltaX = (int) (event.getRawX() - initialTouchX);
                            int deltaY = (int) (event.getRawY() - initialTouchY);
                            params.x = initialX + deltaX;
                            params.y = initialY + deltaY;
                            windowManager.updateViewLayout(floatingButtonsView, params);
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        isDragging = false;
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });


        // 设置按钮点击事件
        Button btn1 = floatingButtonsView.findViewById(R.id.add);
        Button btn2 = floatingButtonsView.findViewById(R.id.del);
        Button btn3 = floatingButtonsView.findViewById(R.id.begin);
        Button btn4 = floatingButtonsView.findViewById(R.id.time);
        Button btn5 = floatingButtonsView.findViewById(R.id.close);
        Button btn6 = floatingButtonsView.findViewById(R.id.set);


        // 在这里添加每个按钮的点击事件逻辑

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在这里添加按钮1的点击事件逻辑
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在这里添加按钮1的点击事件逻辑
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在这里添加按钮1的点击事件逻辑
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在这里添加按钮1的点击事件逻辑
            }
        });
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在这里添加按钮1的点击事件逻辑
            }
        });

        // 添加其他按钮的点击事件...

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingButtonsView != null && windowManager != null) {
            windowManager.removeView(floatingButtonsView);
        }
    }
}
