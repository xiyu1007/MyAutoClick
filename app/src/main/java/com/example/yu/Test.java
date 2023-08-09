package com.example.yu;

import android.accessibilityservice.AccessibilityService;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
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



//    private boolean isAccessibilityServiceEnabled() {
//        // 定义您的辅助功能服务的完整类名，形如：包名/服务类名
//        String serviceName = "com.example.yourapp/com.example.yourapp.ClickAtLocationService";
//
//        // 通过系统设置获取辅助功能设置的状态，默认为 0
//        int accessibilityEnabled = Settings.Secure.getInt(
//                getContentResolver(),
//                android.provider.Settings.Secure.ACCESSIBILITY_ENABLED,
//                0
//        );
//
//        // 初始化一个简单的字符串分割器，用于解析已启用的辅助功能服务列表
//        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
//
//        // 如果辅助功能已启用
//        if (accessibilityEnabled == 1) {
//            // 获取已启用的辅助功能服务列表
//            String settingValue = Settings.Secure.getString(
//                    getContentResolver(),
//                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
//            );
//
//            // 如果服务列表不为空
//            if (settingValue != null) {
//                colonSplitter.setString(settingValue);
//
//                // 遍历服务列表
//                while (colonSplitter.hasNext()) {
//                    String accessibilityService = colonSplitter.next();
//
//                    // 如果找到了匹配的服务名，说明辅助功能已启用
//                    if (accessibilityService.equalsIgnoreCase(serviceName)) {
//                        return true;
//                    }
//                }
//            }
//        }
//
//        // 如果没有找到匹配的服务名，说明辅助功能未启用
//        return false;
//    }

