package com.example.yu;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

// Custom ThreadFactory implementation
class MyThreadFactory implements ThreadFactory {
    private final AtomicInteger threadCount = new AtomicInteger(1);

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, "ClickThread-" + threadCount.getAndIncrement());
        thread.setPriority(Thread.NORM_PRIORITY); // Set thread priority if needed
        return thread;
    }
}