package com.narc.arclient.processor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;

import java.nio.ByteBuffer;

public class BitmapProcessor implements Processor<ProcessorManager.RecognizeTask, ProcessorManager.RecognizeTask> {
    private static final BitmapProcessor BITMAP_PROCESSOR = new BitmapProcessor();

    private BitmapProcessor() {
    }

    @Override
    public ProcessorManager.RecognizeTask process(ProcessorManager.RecognizeTask recognizeTask) {
        Image image = recognizeTask.getImage();
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        int width = image.getWidth();
        int height = image.getHeight();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        recognizeTask.setOriginBitmap(Bitmap.createBitmap(bitmap, 0, 0, width, height));

        return recognizeTask;
    }

    public static Processor<ProcessorManager.RecognizeTask, ProcessorManager.RecognizeTask> getInstance() {
        return BITMAP_PROCESSOR;
    }
}
