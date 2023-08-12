package com.example.yu;

import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public interface AccessibilityServiceInterface {

      default void sendAccessibilityEvent(AccessibilityEvent event) {
        // 获取事件类型
        int eventType = event.getEventType();

        // 获取事件关联的视图对象
        AccessibilityNodeInfo nodeInfo = event.getSource();

        // 根据事件类型执行相应的操作
        switch (eventType) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                // 处理视图点击事件
                if (nodeInfo != null) {
                    // 执行与点击事件相关的操作，例如触发特定响应机制或更新界面等
                    Log.e("AAAAA","gwssssssssss");
                    // ...
                }
                break;
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                // 处理长按视图事件
                if (nodeInfo != null) {
                    // 执行与长按事件相关的操作，例如触发特定响应机制或更新界面等
                    // ...
                }
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                // 处理文本变更事件
                if (nodeInfo != null) {

                }
                break;
            // 根据需求添加其他事件类型的处理逻辑
            // ...
            default:
                break;
        }
    }
}
