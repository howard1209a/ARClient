package com.narc.arclient.processor;

import android.graphics.Bitmap;

import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.MPImage;
import com.narc.arclient.entity.RecognizeTask;
import com.narc.arclient.enums.TaskType;

public class PreHandleProcessor implements Processor<RecognizeTask, RecognizeTask> {
    private static final PreHandleProcessor PRE_HANDLE_PROCESSOR = new PreHandleProcessor();

    private PreHandleProcessor() {
    }

    @Override
    public RecognizeTask process(RecognizeTask recognizeTask) {
        recognizeTask.recordTimeConsumeStart(TaskType.PREPROCESS);

        Bitmap originBitmap = recognizeTask.getOriginBitmap();
        originBitmap = scaleBitmap(originBitmap, 0.4f);
        recognizeTask.setMpImage(new BitmapImageBuilder(originBitmap).build());

        recognizeTask.recordTimeConsumeEnd(TaskType.PREPROCESS);
        return recognizeTask;
    }

    public static Bitmap scaleBitmap(Bitmap originalBitmap, float scale) {
        if (originalBitmap == null) {
            throw new IllegalArgumentException("Original bitmap cannot be null");
        }

        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();

        int newWidth = Math.round(width * scale);
        int newHeight = Math.round(height * scale);

        return Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
    }

    public static Processor<RecognizeTask, RecognizeTask> getInstance() {
        return PRE_HANDLE_PROCESSOR;
    }
}
