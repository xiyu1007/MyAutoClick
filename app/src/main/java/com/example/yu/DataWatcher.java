package com.example.yu;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.*;
import java.lang.reflect.Field;

public class DataWatcher implements Serializable{
    private final String TAG = "DataWatcher";
    // int
    public String delay = "20";
    public String keepTime = "10";
    public String repeatTimes = "1";
    public String diameter = "0";
    public String randomDelay = "0";
    // String

    public String type = "点击";
    public String timed = "00:00:00";

    // 设置范围限制的变量



    public void setAllAttributes(String[] attributes) {
        if (attributes.length != 7) {
            throw new IllegalArgumentException("属性数组长度必须为7。");
        }

        this.delay = attributes[0];
        this.keepTime = attributes[1];
        this.repeatTimes = attributes[2];
        this.type = attributes[3];
        this.diameter = attributes[4];
        this.randomDelay = attributes[5];
        this.timed = attributes[6];
    }
    public int compareData(DataWatcher dataWatcher) {
        if (this.delay.equals(dataWatcher.delay) &&
                this.keepTime.equals(dataWatcher.keepTime) &&
                this.repeatTimes.equals(dataWatcher.repeatTimes) &&
                this.diameter.equals(dataWatcher.diameter) &&
                this.randomDelay.equals(dataWatcher.randomDelay) &&
                this.type.equals(dataWatcher.type) &&
                this.timed.equals(dataWatcher.timed)) {
            return MyApplication.StatusNONE;
        } else {
            return MyApplication.StatusNO;
        }
    }
    public int checkData() {
        if (isValidInteger(delay) &&
                isValidInteger(keepTime) &&
                isValidInteger(repeatTimes) &&
                isValidInteger(diameter) &&
                isValidInteger(randomDelay)) {
            return MyApplication.StatusOK;
        } else {
            return MyApplication.StatusERR;
        }
    }

    private boolean isValidInteger(String value) {
        if (!value.isEmpty()) {
            int intValue = Integer.parseInt(value);
            return intValue >= MyApplication.MIN_VALUE && intValue <= MyApplication.MAX_VALUE;
        }else {
            return true;
        }
    }

    // 打印对象属性

    public String printAttributes(String TAG) {
        String logMessage = "===========================================================\n" +
                "delay: " + delay + "\n" +
                "keepTime: " + keepTime + "\n" +
                "repeatTimes: " + repeatTimes + "\n" +
                "diameter: " + diameter + "\n" +
                "randomDelay: " + randomDelay + "\n" +
                "type: " + type + "\n" +
                "timed: " + timed + "\n" +
                "===========================================================\n";
        Log.d(TAG,logMessage);
        return logMessage;
    }

    public String dataToString() {
        return "DataWatcher{" + "delay='" + delay + '\'' +
                ", keepTime='" + keepTime + '\'' +
                ", repeatTimes='" + repeatTimes + '\'' +
                ", diameter='" + diameter + '\'' +
                ", randomDelay='" + randomDelay + '\'' +
                ", type='" + type + '\'' +
                ", timed='" + timed + '\'' + '}';
    }


}
