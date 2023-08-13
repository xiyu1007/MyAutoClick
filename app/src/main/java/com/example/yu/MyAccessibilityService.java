package com.example.yu;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.annotation.SuppressLint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ASUS
 */
public class MyAccessibilityService extends AccessibilityService{
    private static final String TAG = "AccessibilityService";
    private final String PACKAGENAME = "com.example.yu";

    private volatile boolean isStopClick = false;
    private ThreadPoolExecutor clickThreadPool;

    public static List<FloatingIcon> floatingIcons = new ArrayList<>();
    List<List<String>> clickPointMessages = new ArrayList<>();

    // 初始化 clickPointMessages 列表
    @SuppressLint("InflateParams")
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        setAccessibilityNodeInfos();

        for (int i = 0; i < MyApplication.MAX_FLOATING_ICONS; i++) {
            clickPointMessages.add(new ArrayList<>());
        }

        ThreadFactory threadFactory = new MyThreadFactory();
        // Replace with your custom ThreadFactory implementation
        int corePoolSize = 1;
        // 初始核心线程数
        int maximumPoolSize = 5;
        // 最大线程数
        long keepAliveTime = 0;
        // 线程空闲时间
        TimeUnit unit = TimeUnit.MILLISECONDS;
        // 时间单位
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
        // 任务队列
        clickThreadPool = new ThreadPoolExecutor(
                corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory
        );

    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String s = event.toString();
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        Log.e(TAG,s);
        if (rootNode !=null) {
            if (event.getPackageName() != null && event.getPackageName().equals(PACKAGENAME)) {
                List<AccessibilityNodeInfo> nodeInfoList = rootNode.findAccessibilityNodeInfosByViewId("com.example.yu:id/aim_text");
                updateClickNodeMessage(nodeInfoList);
            }

        }
         else if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            if (event.getPackageName() != null && event.getPackageName().equals(PACKAGENAME)) {
                Log.i(TAG,"TYPE_VIEW_CLICKED");
//                simulateClick(400,100);
//                AccessibilityNodeInfo rootNode = getRootInActiveWindow();
            }
        }
    }

    private void updateClickNodeMessage(List<AccessibilityNodeInfo> nodeInfoList){
        for (AccessibilityNodeInfo node : nodeInfoList) {
            try {
                int clickSequence = Integer.parseInt(node.getText().toString());
                Rect nodeBoundsInScreen = myGetBoundsInScreen(node);
                List<String> dataToStringArray = new ArrayList<>();

                for (FloatingIcon floatingIcon : MyApplication.floatingIcons) {
                    if (floatingIcon.getId() == clickSequence) {
                        dataToStringArray = floatingIcon.dataWatcher.dataToArrayList();
                        if (dataToStringArray.size() >= 7) {
                            dataToStringArray.add(String.valueOf(nodeBoundsInScreen.centerX()));
                            dataToStringArray.add(String.valueOf(nodeBoundsInScreen.centerY()));
                        } else {
                            dataToStringArray.set(5, String.valueOf(nodeBoundsInScreen.centerX()));
                            // Replace index 5
                            dataToStringArray.set(6, String.valueOf(nodeBoundsInScreen.centerY()));
                            // Replace index 6
                        }
                    }
                }
                clickPointMessages.set(clickSequence, dataToStringArray);

                node.recycle(); // 记得回收节点资源

            } catch (NumberFormatException e) {
                // 处理转换异常，例如打印错误日志或者提醒用户输入无效
                e.printStackTrace();
                ShowToast.show(this,"出错了，请重启");
                // 或者显示一个提示给用户
            }
        }
        for (int i = 0; i < clickPointMessages.size(); i++) {

            List<String> clickEvent = clickPointMessages.get(i);
            if (!clickEvent.isEmpty()) {
                String logMessage = "Click Sequence " + i + ": " + clickEvent.toString();
                Log.d(TAG, logMessage);
            }
        }
    }


    // 修改 stopClickSimulation 方法，停止点击线程池：

    private void stopClickSimulation() {
        isStopClick = true;
        try {
            clickThreadPool.shutdown();
            if (!clickThreadPool.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                //500ms强行关闭
                clickThreadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            clickThreadPool.shutdownNow();
        }
    }

    private void startSimulateClick(int x, int y) {
        Path clickPath = new Path();
        clickPath.moveTo(x, y);
        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(clickPath, 0, 100));
        GestureDescription gestureDescription = gestureBuilder.build();

        while (!isStopClick) {
            try {
                clickThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dispatchGesture(gestureDescription, null, null);
                        } catch (Exception e) {
                            WriteToLog.writeLogToFile(MyAccessibilityService.this, "点击出错：" + e.getMessage(), true);
                            e.printStackTrace();
                        }
                    }
                });

                // 控制点击频率和休眠时间
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    WriteToLog.writeLogToFile(MyAccessibilityService.this, "点击结束：" + e.getMessage(), true);
                    e.printStackTrace();
                }
            } catch (RejectedExecutionException e) {
                WriteToLog.writeLogToFile(MyAccessibilityService.this, "任务被拒绝：" + e.getMessage(), true);
                e.printStackTrace();
            }
        }
    }

    public  Rect myGetBoundsInScreen(AccessibilityNodeInfo node) {
        Rect boundsInScreen = null;
        if (node != null) {
            // 获取节点在屏幕中的位置
            boundsInScreen = new Rect();
            node.getBoundsInScreen(boundsInScreen);

            int left = boundsInScreen.left;
            int top = boundsInScreen.top;
            int right = boundsInScreen.right;
            int bottom = boundsInScreen.bottom;
            Log.d(TAG,"Left:"+left+" Top:"+top+" Right:"+right+" Bottom:"+bottom);
            Log.d(TAG,node.toString());
        }
        return boundsInScreen;
    }

    private void printNodeMessage(AccessibilityNodeInfo node){
        String nodeId = node.getViewIdResourceName() != null ? node.getViewIdResourceName() : "No ID";
        String nodeText = node.getText() != null ? node.getText().toString() : "No text";
        String nodeDescription = node.getContentDescription() != null ? node.getContentDescription().toString() : "No description";
        Rect boundsInScreen = new Rect();
        node.getBoundsInScreen(boundsInScreen);
        MyApplication.pritfLine();
        Log.d(TAG, "Node ID: " + nodeId);
        Log.d(TAG, "Node Text: " + nodeText);
        Log.d(TAG, "Node Description: " + nodeDescription);
        Log.d(TAG, "Node Bounds in Screen: left=" + boundsInScreen.left + ", top=" + boundsInScreen.top +
                ", right=" + boundsInScreen.right + ", bottom=" + boundsInScreen.bottom);
    }


    @Override
    public void onInterrupt() {
        Log.e(TAG, "Something went wrong");
    }


    //TODO 动态配置，有时候onAccessibilityEvent无反应则需要配置以下
    //TODO 动态配置，有时候onAccessibilityEvent无反应则需要配置以下
    //TODO 动态配置，有时候onAccessibilityEvent无反应则需要配置以下

    private void setAccessibilityNodeInfos(){
        AccessibilityServiceInfo serviceInfo = new AccessibilityServiceInfo();
//        serviceInfo.eventTypes = AccessibilityEvent.TYPE_WINDOWS_CHANGED;
        serviceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        serviceInfo.flags |= AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;

        serviceInfo.flags |= AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE;

        serviceInfo.flags |= AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;

        setServiceInfo(serviceInfo);
    }

}
