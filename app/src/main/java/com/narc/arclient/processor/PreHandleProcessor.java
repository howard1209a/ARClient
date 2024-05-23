package com.narc.arclient.processor;

import android.graphics.Bitmap;

import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.MPImage;

public class PreHandleProcessor implements Processor<ProcessorManager.RecognizeTask, ProcessorManager.RecognizeTask> {
    private static final PreHandleProcessor PRE_HANDLE_PROCESSOR = new PreHandleProcessor();

    private PreHandleProcessor() {
    }

    @Override
    public ProcessorManager.RecognizeTask process(ProcessorManager.RecognizeTask recognizeTask) {
        Bitmap originBitmap = recognizeTask.getOriginBitmap();
        recognizeTask.setMpImage(new BitmapImageBuilder(originBitmap).build());
        return recognizeTask;
    }

    public static Processor<ProcessorManager.RecognizeTask, ProcessorManager.RecognizeTask> getInstance() {
        return PRE_HANDLE_PROCESSOR;
    }
}
