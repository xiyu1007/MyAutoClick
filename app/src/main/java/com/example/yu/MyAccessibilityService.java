package com.example.yu;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.content.pm.ApplicationInfo;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * @author ASUS
 */
public class MyAccessibilityService extends AccessibilityService implements AccessibilityServiceInterface {
    private static final String TAG = "AccessibilityService";
    private final String PACKAGENAME = "com.example.yu";

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        AccessibilityServiceInfo serviceInfo = new AccessibilityServiceInfo();
//        serviceInfo.eventTypes = AccessibilityEvent.TYPE_WINDOWS_CHANGED;
        serviceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        serviceInfo.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;

        setServiceInfo(serviceInfo);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String s = event.toString();
        //Log.e(TAG,s);

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            if (event.getPackageName() != null && event.getPackageName().equals(PACKAGENAME)) {
                Log.i(TAG,"TYPE_WINDOW_CONTENT_CHANGED");
                WriteToLog.writeLogToFile(this,"TYPE_WINDOW_CONTENT_CHANGED",false);
            }
        } else if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            if (event.getPackageName() != null && event.getPackageName().equals(PACKAGENAME)) {
                MyApplication.pritfLine();
                Log.i(TAG,"TYPE_VIEW_CLICKED");
//                simulateClick(400,100);
                AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                printRootNodeInfo(rootNode);
                printChildNodeInfo(rootNode);
            }
        }
    }
    private void simulateClick(int x, int y) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) {
            return;
        }
        List<AccessibilityNodeInfo> id =  rootNode.findAccessibilityNodeInfosByViewId("aim_icon");
        List<AccessibilityNodeInfo> text = rootNode.findAccessibilityNodeInfosByText("Add_button");

        if (!text.isEmpty()) {
            AccessibilityNodeInfo textNode = text.get(0);
            MyApplication.pritfLine();
            getPosition(textNode);
//            printNodeInfo(textNode);
        } else {
            Log.d("Node Info", "No node found by text");
        }


//        printChildNodeInfo(rootNode)

        Path clickPath = new Path();
        clickPath.moveTo(x, y);

        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(clickPath, 0, 100));

        GestureDescription gestureDescription = gestureBuilder.build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dispatchGesture(gestureDescription, null, null);
        }
    }

    private void printNodeInfo(AccessibilityNodeInfo nodeInfo) {
            if (nodeInfo == null) {
                return;
            }

            Log.d("Node Info", "Package Name: " + nodeInfo.getPackageName());
            Log.d("Node Info", "Class Name: " + nodeInfo.getClassName());
            Log.d("Node Info", "Content Description: " + nodeInfo.getContentDescription());
            Log.d("Node Info", "Text: " + nodeInfo.getText());
            Log.d("Node Info", "Clickable: " + nodeInfo.isClickable());
            Log.d("Node Info", "id: " + nodeInfo.getUniqueId());
            // Add more properties as needed

            // Recycle the nodeInfo object
            nodeInfo.recycle();
    }

    public  Rect getPosition(AccessibilityNodeInfo rootNode) {
        Rect boundsInScreen = null;
        if (rootNode != null) {
            // 获取节点在屏幕中的位置
            boundsInScreen = new Rect();
            rootNode.getBoundsInScreen(boundsInScreen);

            int left = boundsInScreen.left;
            int top = boundsInScreen.top;
            int right = boundsInScreen.right;
            int bottom = boundsInScreen.bottom;
            Log.d(TAG,"Left"+left+"Top"+top+"Right"+right+"Bottom"+bottom);
        }
        return boundsInScreen;
    }
    private void printChildNodeInfo(AccessibilityNodeInfo nodeInfo) {
            if (nodeInfo == null) {
                return;
            }

            for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                AccessibilityNodeInfo childNode = nodeInfo.getChild(i);

                if (childNode != null) {
                    MyApplication.pritfLine();
                    MyApplication.pritfLine();
                    Log.d("Child Node Info", "Package Name: " + childNode.getPackageName());
                    Log.d("Child Node Info", "Class Name: " + childNode.getClassName());
                    Log.d("Child Node Info", "Text: " + childNode.getText());

                    // 递归打印子节点的子节点
                    printChildNodeInfo(childNode);

                    // 释放资源
                    childNode.recycle();
                }
            }
        }

    private void printRootNodeInfo(AccessibilityNodeInfo rootNode){
        // 打印根节点的一些基本信息
        Log.d("Root Node Info", "Package Name: " + rootNode.getPackageName());
        Log.d("Root Node Info", "Class Name: " + rootNode.getClassName());
        Log.d("Root Node Info", "Content Description: " + rootNode.getContentDescription());

        // 打印根节点的子节点数量
        Log.d("Root Node Info", "Child Count: " + rootNode.getChildCount());

    }



    @Override
    public void onInterrupt() {
        Log.e(TAG, "Something went wrong");
    }


    //目前不知道怎么用

    @Override
    public void sendAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityServiceInterface.super.sendAccessibilityEvent(event);
    }


}
