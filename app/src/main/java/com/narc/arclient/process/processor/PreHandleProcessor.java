package com.narc.arclient.process.processor;

import static com.narc.arclient.enums.ProcessorEnums.SCALE_FACTOR;

import android.graphics.Bitmap;

import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.narc.arclient.entity.RecognizeTask;
import com.narc.arclient.enums.TaskType;
import com.narc.arclient.process.Processor;

public class PreHandleProcessor implements Processor<RecognizeTask, RecognizeTask> {
    private static final PreHandleProcessor PRE_HANDLE_PROCESSOR = new PreHandleProcessor();

    private PreHandleProcessor() {
    }

    @Override
    public RecognizeTask process(RecognizeTask recognizeTask) {
        recognizeTask.recordTimeConsumeStart(TaskType.PREPROCESS);

        Bitmap originBitmap = recognizeTask.getOriginBitmap();
        originBitmap = scaleBitmap(originBitmap, SCALE_FACTOR);
        recognizeTask.setMpImage(new BitmapImageBuilder(originBitmap).build());

        recognizeTask.recordTimeConsumeEnd(TaskType.PREPROCESS);
        return recognizeTask;
    }

    public static Bitmap scaleBitmap(Bitmap originalBitmap, float scale) {

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
