package com.example.yu;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.os.Handler;

public class MyAccessibilityService extends AccessibilityService {

    private Handler handler = new Handler();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Handle accessibility events here
    }

    @Override
    public void onInterrupt() {
        // Service interrupted
    }

    // Function to simulate user clicks
    public void simulateClick(AccessibilityNodeInfo node, int times, long delay) {
        if (node == null) {
            return;
        }

        for (int i = 0; i < times; i++) {
            handler.postDelayed(() -> {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }, i * delay);
        }
    }

    // Perform a simulated click externally
    public void performClick() {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        simulateClick(rootNode, 1, 1000); // Simulate a single click with a delay of 1 second
    }
}
