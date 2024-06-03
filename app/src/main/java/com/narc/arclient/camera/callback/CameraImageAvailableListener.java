package com.narc.arclient.camera.callback;

import android.media.Image;
import android.media.ImageReader;

import com.narc.arclient.entity.RecognizeTask;
import com.narc.arclient.processor.BitmapProcessor;
import com.narc.arclient.processor.PreHandleProcessor;
import com.narc.arclient.processor.ProcessorManager;
import com.narc.arclient.processor.RecognizeProcessor;
import com.narc.arclient.processor.RenderProcessor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class CameraImageAvailableListener implements ImageReader.OnImageAvailableListener {

    @Override
    public void onImageAvailable(ImageReader reader) {
        Image image;
        try {
            image = reader.acquireLatestImage();
        } catch (IllegalStateException e) {
            return;
        }
        RecognizeTask initialRecognizeTask = new RecognizeTask(image);
        CompletableFuture.supplyAsync(() -> BitmapProcessor.getInstance().process(initialRecognizeTask), ProcessorManager.imageCopyExecutor)
                .thenApplyAsync(result -> PreHandleProcessor.getInstance().process(result), ProcessorManager.normalExecutor)
                .thenApplyAsync(result -> RecognizeProcessor.getInstance().process(result), ProcessorManager.normalExecutor)
                .thenApplyAsync(result -> RenderProcessor.getInstance().process(result), ProcessorManager.normalExecutor);
    }
}
