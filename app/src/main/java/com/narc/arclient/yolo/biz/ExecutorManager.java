package com.narc.arclient.yolo.biz;

import com.narc.arclient.yolo.model.Context;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorManager {
    private int taskIDIndex;
    private static final ExecutorManager EXECUTOR_MANAGER = new ExecutorManager();
    private final ThreadPoolExecutor serialExecutor = new ThreadPoolExecutor(1, 1, 20, TimeUnit.MINUTES, new LinkedBlockingQueue<>());

    private ExecutorManager() {
    }

    public static ExecutorManager getInstance() {
        return EXECUTOR_MANAGER;
    }

    public void submitImgDetectTask(int localComputeCount,String imgName) {
        Context ctx = new Context();
        ctx.taskID = ++taskIDIndex;
        ctx.imgName = imgName;
        ctx.localComputeCount = localComputeCount;

        CompletableFuture.supplyAsync(() -> ImageLoadHandler.getInstance().handle(ctx), serialExecutor)
                .thenApplyAsync(result -> YoloModelHandler.getInstance().handle(result), serialExecutor)
                .thenApplyAsync(result -> OffloadHandler.getInstance().handle(result), serialExecutor);
    }
}
