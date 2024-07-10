package com.narc.arclient.camera.callback;

import static com.narc.arclient.enums.NetworkEnums.GRAY;

import android.media.Image;
import android.media.ImageReader;

import com.narc.arclient.R;
import com.narc.arclient.entity.RecognizeTask;
import com.narc.arclient.enums.UnloadEnd;
import com.narc.arclient.process.processor.BitmapProcessor;
import com.narc.arclient.process.processor.PreHandleProcessor;
import com.narc.arclient.process.ProcessorManager;
import com.narc.arclient.process.processor.RecognizeProcessor;
import com.narc.arclient.process.processor.RenderProcessor;
import com.narc.arclient.process.processor.SendRemoteProcessor;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class CameraImageAvailableListener implements ImageReader.OnImageAvailableListener {
    private Random random = new Random();

    @Override
    public void onImageAvailable(ImageReader reader) {
        Image image;
        try {
            image = reader.acquireLatestImage();
        } catch (IllegalStateException e) {
            return;
        }
        RecognizeTask initialRecognizeTask = new RecognizeTask(image);

        if (random.nextInt(100) < GRAY) {
            initialRecognizeTask.setUnloadEnd(UnloadEnd.REMOTE.name());
            CompletableFuture.supplyAsync(() -> BitmapProcessor.getInstance().process(initialRecognizeTask), ProcessorManager.imageCopyExecutor)
                    .thenApplyAsync(result -> SendRemoteProcessor.getInstance().process(result), ProcessorManager.normalExecutor);
        } else {
            initialRecognizeTask.setUnloadEnd(UnloadEnd.LOCAL.name());
            CompletableFuture.supplyAsync(() -> BitmapProcessor.getInstance().process(initialRecognizeTask), ProcessorManager.imageCopyExecutor)
                    .thenApplyAsync(result -> PreHandleProcessor.getInstance().process(result), ProcessorManager.normalExecutor)
                    .thenApplyAsync(result -> RecognizeProcessor.getInstance().process(result), ProcessorManager.normalExecutor)
                    .thenApplyAsync(result -> RenderProcessor.getInstance().process(result), ProcessorManager.normalExecutor);
        }
    }
}
