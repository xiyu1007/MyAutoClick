package com.example.yu;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * @author ASUS
 */
public class MyApplication extends Application {

    public static  int MIN_VALUE = 0;
    public static final int MAX_VALUE = 10000;

    public static final int StatusOK = 1;
    public static final int StatusNONE = 0;
    public static final int StatusNO = -1;

    public static final int StatusERR = -2;
    public static final boolean STATUS_TRUE = true;
    public static final boolean STATUS_FALSE = false;

    private static MyApplication myApplication;

    public static Context context;
    private static DataWatcher converyDataWatcher;

    //Action

    public static final String ACTION_FEATURE_SETTING_STOPPED = "com.example.yu.ACTION_FEATURE_SETTING_STOPPED";
    public static final String ACTION_ICON_SETTING_START = "com.example.yu.ICON__SETTING_START";

    //广播传递标识符

    public static final String MESSAGE_HAD_SET_VIEW = "HAD_SETTING";
    public static final String MESSAGE_ICON_SETTING_START = "ICON_SETTING_START";

    @SuppressLint("StaticFieldLeak")
    public static FileHelper fileHelper;
    public SharedPreferencesHelper sharedPreferencesHelper;
    private static final String SHARED_PREFERENCES_NAME = "config";

    public static int SELECT_DATA = 1;

    public static int WHICH_ICON = 0;

    private static final String TAG = "MyApplication";

    public static  WindowManager windowManager;


    private Config config;

    protected void restartApp() {
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;


        // 设置全局异常处理器
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
                // 将异常信息写入日志文件

                WriteToLog.writeLogToFile(myApplication ,throwable.getMessage(),true);
                // 在这里您可以添加其他处理逻辑，例如重启应用或显示错误界面
                // 在这里执行其他处理逻辑，例如发送崩溃报告

                // 延迟一段时间后退出应用程序
//                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        System.exit(2); // 退出应用程序
//                    }
//                }, 2000); // 延迟 2 秒钟

                // 最后，让系统默认的异常处理器处理异常
                //Objects.requireNonNull(Thread.getDefaultUncaughtExceptionHandler()).uncaughtException(thread, throwable);
            }
        });

        config = new Config(this);

        fileHelper = new FileHelper(this);
        sharedPreferencesHelper = new SharedPreferencesHelper(this,SHARED_PREFERENCES_NAME);
        sharedPreferencesHelper.saveInt("SELECT_DATA",SELECT_DATA);
        // Initialize the UserData instance here
    }

    public static DataWatcher getconveydatawatcher() {
        return converyDataWatcher;
    }

    public static void setConveryDataWatcher(DataWatcher dataWatcher) {
        MyApplication.converyDataWatcher = dataWatcher;
    }
    public static MyApplication getMyApplication(){
        return myApplication;
    }


    public static void pritfLine(){
        Log.d(TAG,"===========================================================\n");
    }



}
