package com.narc.arclient.camera.callback;

import android.media.Image;
import android.media.ImageReader;

import com.narc.arclient.processor.BitmapProcessor;
import com.narc.arclient.processor.PreHandleProcessor;
import com.narc.arclient.processor.ProcessorManager;
import com.narc.arclient.processor.RecognizeProcessor;
import com.narc.arclient.processor.RenderProcessor;

import java.util.concurrent.CompletableFuture;

public class CameraImageAvailableListener implements ImageReader.OnImageAvailableListener {

    @Override
    public void onImageAvailable(ImageReader reader) {
        Image image = reader.acquireLatestImage();
        ProcessorManager.RecognizeTask initialRecognizeTask = new ProcessorManager.RecognizeTask(image);
        CompletableFuture.supplyAsync(() -> BitmapProcessor.getInstance().process(initialRecognizeTask), ProcessorManager.executor)
                .thenApplyAsync(result -> PreHandleProcessor.getInstance().process(result), ProcessorManager.executor)
                .thenApplyAsync(result -> RecognizeProcessor.getInstance().process(result), ProcessorManager.executor)
                .thenApplyAsync(result -> RenderProcessor.getInstance().process(result), ProcessorManager.executor);
    }
}
