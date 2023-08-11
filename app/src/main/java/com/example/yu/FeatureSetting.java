package com.example.yu;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * @author ASUS
 */
//public class FeatureSetting extends Service implements EditTextWatcher.TextChangeListener{
public class FeatureSetting extends Service {
    private WindowManager windowManager;
    private View floatingView;

    private DataWatcher periordataWatcher;
    private DataWatcher finalldataWatcher;

    private Context context = null;

    private EditText dataDelay;
    private EditText dataKeepTime;
    private EditText dataRepeatTimes;
    private EditText dataDiameter;
    private EditText dataRandomDelay;

    private TextView dataType;
    private TextView dataTimed;

    private TextView junior;
    private TextView senior;
    private Button deleteButton;
    private TextView cancel;
    private TextView save;

    private final String TAG = "FeatureSetting";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onFeatureSettingServiceStopped(int val) {
        // 发送广播通知回调
        Intent intent = new Intent(MyApplication.ACTION_FEATURE_SETTING_STOPPED);
        intent.putExtra(MyApplication.MESSAGE_HAD_SET_VIEW, val);
        context.sendBroadcast(intent);
    }

    @SuppressLint("InflateParams")
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();


        LayoutInflater inflater = LayoutInflater.from(this);
        floatingView = inflater.inflate(R.layout.feature_setting, null);
        setView();
        initializeViews(floatingView);
        periordataWatcher = MyApplication.getconveydatawatcher();
        setData(periordataWatcher);

        // 添加触摸监听器
        floatingView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 判断是否点击在悬浮窗外部区域
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    // 关闭悬浮窗
                    stopSelf();
                    return true; // 返回true表示消费了该事件，不再传递给下层的View
                }
                return false; // 返回false表示不消费该事件，继续传递给下层的View
            }
        });
        floatingView.setFocusableInTouchMode(true);
        floatingView.requestFocus();
        floatingView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // 用户按下系统返回按钮
                    // 在这里执行您的逻辑
                    stopSelf();
                    return true; // 返回true表示已处理按键事件
                }
                return false; // 返回false表示未处理按键事件
            }
        });

        senior.setOnClickListener(this::foldSetting);
        junior.setOnClickListener(this::foldSetting);
        cancel.setOnClickListener(customView -> returnData(MyApplication.StatusNO));
        save.setOnClickListener(this::saveCheck);

    }

    public boolean setData(DataWatcher newDataWatcher) {
//        newDataWatcher.printAttributes(TAG);
        dataDelay.setText(newDataWatcher.delay);
        dataKeepTime.setText(newDataWatcher.keepTime);
        dataRepeatTimes.setText(newDataWatcher.repeatTimes);
        dataDiameter.setText(newDataWatcher.diameter);
        dataRandomDelay.setText(newDataWatcher.randomDelay);
        dataType.setText(newDataWatcher.type);
        dataTimed.setText(newDataWatcher.timed);
        ShowToast.show(context,"设置显示成功");

        return MyApplication.STATUS_TRUE;
    }

    public DataWatcher getData() {
        DataWatcher dataWatcher = new DataWatcher();
        dataWatcher.delay = dataDelay.getText().toString();
        dataWatcher.keepTime = dataKeepTime.getText().toString();
        dataWatcher.repeatTimes = dataRepeatTimes.getText().toString();
        dataWatcher.diameter = dataDiameter.getText().toString();
        dataWatcher.randomDelay = dataRandomDelay.getText().toString();
        dataWatcher.type = dataType.getText().toString();
        dataWatcher.timed = dataTimed.getText().toString();

        return dataWatcher;
    }

    private void saveCheck(View view) {
        finalldataWatcher = getData();
        if (finalldataWatcher == null){
            stopSelf();
        }
        int status = finalldataWatcher.compareData(periordataWatcher);
        if (status == MyApplication.StatusNONE) {
            returnData(MyApplication.StatusNONE);
        } else {
            returnData(finalldataWatcher.checkData());
        }
    }

    public void returnData(int status) {
        if (status == MyApplication.StatusOK) {
            Log.i("FeatureSetting","检测完毕，已修改");
            MyApplication.setConveryDataWatcher(finalldataWatcher);
            onFeatureSettingServiceStopped(MyApplication.StatusOK);
        } else if (status == MyApplication.StatusNONE) {
            //ShowToast.show(context, "数据未改变");
            Log.i("FeatureSetting","数据未改变");
        } else if (status == MyApplication.StatusERR) {
            //ShowToast.show(context, "数据异常");
            Log.i("FeatureSetting","数据异常");
            WriteToLog.writeLogToFile(context,TAG+"用户输入属性异常",true);

        }
        stopSelf();
    }

    public void onDestroy() {
        super.onDestroy();
        if (floatingView != null && floatingView.getWindowToken() != null) {
            windowManager.removeView(floatingView);
        }
        //ShowToast.show(context,"Service结束");
        Log.i("FeatureSetting","Service结束");
    }

    private void initializeViews(View floatingView) {
        dataDelay = floatingView.findViewById(R.id.data_delay);
        dataKeepTime = floatingView.findViewById(R.id.data_keep_time);
        dataRepeatTimes = floatingView.findViewById(R.id.data_repeat_times);
        dataDiameter = floatingView.findViewById(R.id.data_diameter);
        dataRandomDelay = floatingView.findViewById(R.id.data_random_delay);

        dataType = floatingView.findViewById(R.id.data_type);
        dataTimed = floatingView.findViewById(R.id.data_timed);

        junior = floatingView.findViewById(R.id.junior_setting);
        senior = floatingView.findViewById(R.id.senior_setting);
        deleteButton = floatingView.findViewById(R.id.delete_button);
        cancel = floatingView.findViewById(R.id.cancel);
        save = floatingView.findViewById(R.id.save);
    }

    public void setView() {
        /*
        FLAG_NOT_FOCUSABLE: 这个标志指示窗口不接收焦点，不会响应用户的输入事件。
        设置此标志后，窗口上的控件（例如EditText）将无法获取焦点并接收用户输入。
        FLAG_NOT_TOUCH_MODAL: 这个标志指示窗口在接收触摸事件时，不会将触摸事件传递给后面的窗口。
        如果未设置此标志，当用户点击窗口之外的区域时，触摸事件会被传递给后面的窗口。
        FLAG_SPLIT_TOUCH: 这个标志指示窗口支持分割触摸事件，允许多个窗口同时处理触摸事件。
        FLAG_WATCH_OUTSIDE_TOUCH: 这个标志指示窗口监视在窗口之外的触摸事件
        */
        int layoutFlag;

        // 将320dp转换为像素
        int widthInDp = 320;
        int widthInPixels = getPixels(widthInDp);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                widthInPixels,
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

        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(floatingView, params);
    }

    public int getPixels(int widthInDp) {
        // 获取屏幕的密度
        float density = getResources().getDisplayMetrics().density;
        return (int) (widthInDp * density);

    }

    public void foldSetting(View view) {
        LinearLayout juniorSetting = floatingView.findViewById(R.id.junior_setting_item);
        LinearLayout seniorSetting = floatingView.findViewById(R.id.senior_setting_item);
        int juniorVisibility = juniorSetting.getVisibility();
        int seniorVisibility = seniorSetting.getVisibility();
        if (view.getId() == R.id.junior_setting) {
            juniorSetting.setVisibility(juniorVisibility == View.VISIBLE ? View.GONE : View.VISIBLE);
            seniorSetting.setVisibility(View.GONE);
        }
        if (view.getId() == R.id.senior_setting) {
            seniorSetting.setVisibility(seniorVisibility == View.VISIBLE ? View.GONE : View.VISIBLE);
            juniorSetting.setVisibility(View.GONE);
        }
    }
}
