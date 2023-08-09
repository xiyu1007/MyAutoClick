package com.example.yu;

import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class ClickAtLocation {

    // 在你的无障碍服务类中
    private Handler handler = new Handler(Looper.getMainLooper());

    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            AccessibilityNodeInfo sourceNode = event.getSource();
            if (sourceNode != null) {
                int clickCount = 5;
                int clickInterval = 1000;
                repeatClickAtLocation(sourceNode, 50, 50, clickCount, clickInterval);
                sourceNode.recycle(); // 释 使用 close() 方法释放 AccessibilityNodeInfo 对象
            }
        }
    }

    private void repeatClickAtLocation(AccessibilityNodeInfo nodeInfo, int x, int y, int clickCount, int clickInterval) {
        int i = 0;
        while (i < clickCount) {
            final int delay = i * clickInterval;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    clickAtLocation(nodeInfo, x, y);
                }
            }, delay);
            i++;
        }
    }

    private void clickAtLocation(AccessibilityNodeInfo nodeInfo, int x, int y) {
        // 在这里实现点击操作的代码
//        performAction(int action)：执行指定的辅助功能操作，如点击、焦点等。
    }
}
