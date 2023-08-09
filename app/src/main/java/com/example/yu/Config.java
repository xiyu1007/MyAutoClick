package com.example.yu;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ASUS
 */
public class Config {

    private final Context context;

    private final Resources resources;

    private final FileHelper fileHelper;
    private final String FILE_NAME = "data/data.ser";
    private final String TEMP_DATA = "data/temp.txt";
    private final String SHARED_PREFERENCES_NAME = "config";

    private final String LOG_FILE_NAME = "log/log.txt";
    private final String ERROR_LOG_FILE_NAME = "log/error_log.txt";
    private final String TAG = "Config";
    private final String Init_Status = "Init_Status";
    private final int Max_Error_Number = 0;
    private int Start_Error_Number_Value = 1;
    private final String Start_Error_Number_Key = "Start_Error_Number_Key";

    private final SharedPreferencesHelper sharedPreferencesHelper;

    private MyApplication myApplication;

    public Config(Context context) {
        this.context = context;
        resources = this.context.getResources();
        fileHelper = new FileHelper(context);
        sharedPreferencesHelper = new SharedPreferencesHelper(context, SHARED_PREFERENCES_NAME);
        initConfig();
    }

    private void initConfig(){
        //<boolean name="isFirstLaunch" value="false" />
        if(!sharedPreferencesHelper.getBoolean(Init_Status)){
            //清空目录
            boolean isDelete = fileHelper.deleteInternalDirs();

            fileHelper.getInternalFile(ERROR_LOG_FILE_NAME);
            fileHelper.getInternalFile(LOG_FILE_NAME);
            fileHelper.getInternalFile(TEMP_DATA);

            if (isDelete){
                WriteToLog.writeLogToFile(context,TAG+"目录初始化成功",false);
            }else {
                WriteToLog.writeLogToFile(context,TAG+"目录初始化失败",true);
            }
            //TODO 未判断
            WriteToLog.writeLogToFile(context,TAG+"日志文件创建成功",false);

            configWrite();

            sharedPreferencesHelper.saveBoolean(Init_Status,true);
            WriteToLog.writeLogToFile(context,TAG+"应用初始化成功",false);
            ShowToast.show(context,"初始化成功");
        }else {
//            configWrite();
        }
    }

    private void configWrite(){
        List<DataWatcher> dataWatcherList =  setData();
        boolean status = fileHelper.writeCategoriesToFile(FILE_NAME,null,false,dataWatcherList);
        if (!status){
            ShowToast.show(context,"初始化失败，请重启应用");
            if(sharedPreferencesHelper.getInt(Start_Error_Number_Key) > Max_Error_Number){
                fileHelper.deleteInternalDirs();
                ShowToast.show(context,"应用重启中");
                myApplication = new MyApplication();
                myApplication.restartApp();
                Start_Error_Number_Value = 0;
            }
            Start_Error_Number_Value += 1;
            sharedPreferencesHelper.saveInt(Start_Error_Number_Key, Start_Error_Number_Value);
        }
    }


    private List<DataWatcher> setData(){
        List<DataWatcher> dataWatchers = new ArrayList<>();
        try (XmlResourceParser parser = resources.getXml(R.xml.default_data)) {
            String[] currentData = new String[7];
            int eventType = parser.getEventType();
            int itemCounter = 0;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && "item".equals(parser.getName())) {
                    String value = parser.getAttributeValue(null, "value");
                    currentData[itemCounter++] = value;
                    if (itemCounter == 7) {
                        DataWatcher dataWatcher = new DataWatcher();
                        // Create a new instance here
                        dataWatcher.setAllAttributes(currentData);
                        dataWatchers.add(dataWatcher);
                        currentData = new String[7];
                        itemCounter = 0;
                    }
                }
                eventType = parser.next();
            }
            printfData(dataWatchers);
            WriteToLog.writeLogToFile(context,TAG+"获取config.xml成功，读取到"+dataWatchers.size()+"个数据对象",false);


        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            WriteToLog.writeLogToFile(context,TAG+"获取配置文件失败"+e.getMessage(),true);
        }
        return dataWatchers;
    }

    private void printfData(List<DataWatcher> dataWatchers){
//        ShowToast.show(context, String.valueOf(dataWatchers.size()));
        for (DataWatcher watcher : dataWatchers) {
            WriteToLog.writeLogToFile(context,"写入临时数据:"+watcher.dataToString(),false);
        }
    }

}


//    String[] initialDataArray = resources.getStringArray(R.array.Default_Data);
//        for (String item : initialDataArray) {
//                System.out.println(item);
//                }

//    // 获取 API 配置
//    String apiUrl = resources.getString(R.string.api_base_url);
//    String apiKey = resources.getString(R.string.api_key);
//
//    // 获取其他配置
//    boolean isDebugMode = resources.getBoolean(R.bool.debug_mode);
//    int defaultTimeout = resources.getInteger(R.integer.default_timeout);
//
//    // 获取用户首选项
//    String userName = resources.getString(R.string.user_name);
//    String userEmail = context.getString(R.string.user_email);
