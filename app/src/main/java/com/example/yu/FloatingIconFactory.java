package com.example.yu;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import java.util.List;

public class FloatingIconFactory  extends Service {

    public static List<DataWatcher> dataWatcherList = null;
    private static DataWatcher defaultDataWatcher;
    private static final String FILE_NAME = "data/data.ser";

    private static final String CACHE_FILE_NAME = "data_watcher_cache.ser";

    private static final String TAG = "FloatingIconFactory";

    public static FloatingIcon createFloatingIcon(Context context) {
        FloatingIcon floatingIcon = new FloatingIcon(context);
        floatingIcon.dataWatcher = setDefaultDataWatcher(context,MyApplication.SELECT_DATA);
        WriteToLog.writeLogToFile(context,floatingIcon.dataWatcher.printAttributes(TAG),false);
        return floatingIcon;
    }

    public static DataWatcher setDefaultDataWatcher(Context context, int selectData){
        dataWatcherList = MyApplication.fileHelper.readCategoriesToFile(FILE_NAME);
        if (dataWatcherList != null && selectData >= 0 && selectData < dataWatcherList.size()) {
            defaultDataWatcher = dataWatcherList.get(selectData);
            if (defaultDataWatcher !=null){
                WriteToLog.writeLogToFile(context,TAG+"成功获取本地的数据",false);
            }else {
                defaultDataWatcher = createNewDataWatcherWithDefaultValues(context);
            }
        }
        else {
            defaultDataWatcher = createNewDataWatcherWithDefaultValues(context);
        }
        return defaultDataWatcher;
    }
    private static DataWatcher createNewDataWatcherWithDefaultValues(Context context) {
        DataWatcher newDataWatcher = new DataWatcher();
        String[] stringArray = {"20", "20", "1", "点击", "0", "0", "00:00:00"};
        newDataWatcher.setAllAttributes(stringArray);
        WriteToLog.writeLogToFile(context,TAG+"dataWatcherList读取失败或者越界",true);
        Log.i("MyApplication","dataWatcherList读取失败或者越界");
        ShowToast.show(context,"dataWatcherList读取失败或者越界");
        return newDataWatcher;
    }

    public static DataWatcher getDefaultDataWatcher(){
        return defaultDataWatcher;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
