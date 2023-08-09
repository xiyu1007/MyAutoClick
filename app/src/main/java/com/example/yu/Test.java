package com.example.yu;

import android.accessibilityservice.AccessibilityService;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * @author ASUS
 */
public class Test extends AccessibilityService {
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            AccessibilityNodeInfo sourceNode = event.getSource();
            if (sourceNode != null) {
                int clickCount = 5;
                int clickInterval = 1000;
                repeatClickAtLocation(sourceNode, 50, 50, clickCount, clickInterval);
                sourceNode.recycle();
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

    // 实现 clickAtLocation 方法

    private void clickAtLocation(AccessibilityNodeInfo nodeInfo, int x, int y) {
        // 查找位于屏幕上给定坐标 (x, y) 的视图元素
        AccessibilityNodeInfo targetNode = findViewAtLocation(nodeInfo, x, y);

        if (targetNode != null) {
            // 执行点击操作
            targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            targetNode.recycle();
        }
    }

    // 在给定坐标处查找视图元素
    private AccessibilityNodeInfo findViewAtLocation(AccessibilityNodeInfo nodeInfo, int x, int y) {
        if (nodeInfo == null) {
            return null;
        }

//        if (nodeInfo.getBoundsInScreen().contains(x, y)) {
//            return nodeInfo;
//        }

        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo childNode = nodeInfo.getChild(i);
            AccessibilityNodeInfo foundNode = findViewAtLocation(childNode, x, y);
            if (foundNode != null) {
                childNode.recycle();
                return foundNode;
            }
            childNode.recycle();
        }

        return null;
    }

    @Override
    public void onInterrupt() {
        // 当无障碍服务中断时的处理
    }

}
