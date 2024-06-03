package com.narc.arclient.processor;

import static android.content.ContentValues.TAG;

import static com.narc.arclient.enums.ProcessorEnums.MONITOR_FREQUENCY;

import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;

import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ProcessorManager {
    public static final ThreadPoolExecutor normalExecutor = new ThreadPoolExecutor(3, 10, 20, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    public static final ThreadPoolExecutor imageCopyExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    public static final ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(1);

    private static final ProcessorManager PROCESSOR_MANAGER = new ProcessorManager();

    private Monitor monitor;

    private class Monitor implements Runnable {
        private Monitor() {
            startMonitor();
        }

        private void startMonitor() {
            int monitorPeriod = 1000 / MONITOR_FREQUENCY;
            scheduledExecutor.scheduleWithFixedDelay(this, monitorPeriod, monitorPeriod, TimeUnit.MILLISECONDS);
        }

        @Override
        public void run() {
            monitorSystemResources();
            monitorThreadPool();
        }

        private void monitorThreadPool() {
            int normalExecutorQueueSize = normalExecutor.getQueue().size();
            int imageCopyExecutorQueueSize = imageCopyExecutor.getQueue().size();
            Log.d(TAG, String.format("normalExecutor queue size: %d imageCopyExecutor queue size: %d", normalExecutorQueueSize, imageCopyExecutorQueueSize));
//            Log.d(TAG, "Pool Size: " + scheduledExecutor.getPoolSize());
//            Log.d(TAG, "Active Threads: " + scheduledExecutor.getActiveCount());
//            Log.d(TAG, "Completed Tasks: " + scheduledExecutor.getCompletedTaskCount());
//            Log.d(TAG, "Task Count: " + scheduledExecutor.getTaskCount());
//            Log.d(TAG, "Queue Size: " + scheduledExecutor.getQueue().size());
//            Log.d(TAG, "isTerminating: " + scheduledExecutor.isTerminating() );
//            Log.d(TAG, "isTerminated: " + scheduledExecutor.isTerminated());
//            Log.d(TAG, "isShutdown: " + scheduledExecutor.isShutdown());
        }

        private void monitorSystemResources() {
            try {
                String[] cmd = {
                        "/system/bin/top", "-n", "1"
                };
                Process process = Runtime.getRuntime().exec(cmd);
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("com.narc")) {
                        String[] parts = line.split("\\s+");
                        Log.d(TAG, String.format("CPU usage: %.2f MEM usage: %.2f", Double.parseDouble(parts[9]), Double.parseDouble(parts[10])));
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting CPU usage and MEM usage", e);
            }
        }
    }

    private ProcessorManager() {
        this.monitor = new Monitor();
    }

    public static ProcessorManager getInstance() {
        return PROCESSOR_MANAGER;
    }
}
