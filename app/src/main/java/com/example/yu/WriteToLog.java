package com.example.yu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author ASUS
 */
public class WriteToLog {
    private static final String TAG = "LogFileWriter";

    private static final String LOG_FILE_NAME = "log/log.txt";
    private static final String ERROR_LOG_FILE_NAME = "log/error_log.txt";

    @SuppressLint("StaticFieldLeak")
    private static FileHelper fileHelper;


    public static boolean writeLogToFile(Context context, String logMessage, Boolean isErrLog) {
        fileHelper = new FileHelper(context);
        try {
            File file;
            if (isErrLog) {
                file = fileHelper.getInternalFile(ERROR_LOG_FILE_NAME);
            } else {
                file = fileHelper.getInternalFile(LOG_FILE_NAME);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String formattedDate = sdf.format(new Date());
            String logWithTimestamp = "[" + formattedDate + "] " + logMessage;
            // 写入日志到文件
            // 使用 true 参数追加写入
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(logWithTimestamp);
            bufferedWriter.newLine();  // 写入换行符
            bufferedWriter.close();    // 关闭 BufferedWriter

            return MyApplication.STATUS_TRUE;

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error writing error to log file: " + e.getMessage());
        }
        return MyApplication.STATUS_FALSE;
    }
}
