package com.example.yu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * @author ASUS
 */
public class FloatingIcon implements View.OnTouchListener {
    public DataWatcher dataWatcher;

    public SharedPreferencesHelper sharedPreferencesHelper;
    public FileHelper fileHelper;
    private final View floatingView;
    private int id = 1;
    @SuppressLint("StaticFieldLeak")
    private static FloatingIcon instance;
    private  WindowManager.LayoutParams params;

    private final Context context;

    public FloatingIcon(Context context) {
        this.context = context;
        this.floatingView = LayoutInflater.from(context).inflate(R.layout.floating_icon_layout, null);
        // 查找TextView并设置其文本
        setNumber();

        this.params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                // 宽度设置为自适应内容
                WindowManager.LayoutParams.WRAP_CONTENT,
                // 高度设置为自适应内容
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                // 设置类型为应用程序悬浮窗口
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                // 不获取焦点，避免影响其他操作
                PixelFormat.TRANSLUCENT
                // 设置窗口背景为透明
        );
        params.x = 0;
        params.y = 0;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // Touch event handling logic
        return true;
    }

    public void setNumber(){
        TextView numberText = floatingView.findViewById(R.id.number_text);
        numberText.setText(String.valueOf(this.id));
    }
    public int getId(){
        return this.id;
    }
    public void setId(int id){
        this.id = id;
    }
    public View getView() {
        return this.floatingView;
    }
    public WindowManager.LayoutParams getParams() {
        return this.params;
    }

    public void setParams(WindowManager.LayoutParams params){
        this.params = params;
    }

    public void onIconSettingStart(int val) {
        // 发送广播通知回调
        Intent intent = new Intent(MyApplication.ACTION_ICON_SETTING_START);
        intent.putExtra(MyApplication.MESSAGE_ICON_SETTING_START, val);
        context.sendBroadcast(intent);
    }

    public boolean updateAttributes(DataWatcher dataWatcher) {
        if (dataWatcher != null) {
            this.dataWatcher.delay = !dataWatcher.delay.isEmpty()  ? dataWatcher.delay : this.dataWatcher.delay;
            this.dataWatcher.keepTime = !dataWatcher.keepTime.isEmpty()  ? dataWatcher.keepTime : this.dataWatcher.keepTime;
            this.dataWatcher.repeatTimes = !dataWatcher.repeatTimes.isEmpty()? dataWatcher.repeatTimes : this.dataWatcher.repeatTimes;
            this.dataWatcher.diameter = !dataWatcher.diameter.isEmpty() ? dataWatcher.diameter : this.dataWatcher.diameter;
            this.dataWatcher.randomDelay = !dataWatcher.randomDelay.isEmpty()? dataWatcher.randomDelay : this.dataWatcher.randomDelay;
            this.dataWatcher.type = !dataWatcher.type.isEmpty() ? dataWatcher.type : this.dataWatcher.type;
            this.dataWatcher.timed = !dataWatcher.timed.isEmpty() ? dataWatcher.timed : this.dataWatcher.timed;
            return MyApplication.STATUS_TRUE;
        }
        return MyApplication.STATUS_FALSE;
    }

    public static FloatingIcon getInstance(Context context) {
        if (instance == null) {
            instance = new FloatingIcon(context);
        }
        return instance;
    }

}
