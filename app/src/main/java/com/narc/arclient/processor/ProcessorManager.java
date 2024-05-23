package com.narc.arclient.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ProcessorManager {
    public static final ExecutorService executor = new ThreadPoolExecutor(2, 3, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    public static final ScheduledExecutorService scheduledExecutor = new ScheduledThreadPoolExecutor(1);

    public static class TaskLifeCycle {

    }
}
