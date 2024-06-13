package com.narc.arclient.process.processor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;

import com.narc.arclient.entity.RecognizeTask;
import com.narc.arclient.enums.TaskType;
import com.narc.arclient.process.Processor;

import java.nio.ByteBuffer;

public class BitmapProcessor implements Processor<RecognizeTask, RecognizeTask> {
    private static final BitmapProcessor BITMAP_PROCESSOR = new BitmapProcessor();

    private BitmapProcessor() {
    }

    @Override
    public RecognizeTask process(RecognizeTask recognizeTask) {
        recognizeTask.recordTimeConsumeStart(TaskType.COPY);

        Image image = recognizeTask.getImage();
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        int width = image.getWidth();
        int height = image.getHeight();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        // Image底层是ByteBuffer存的，ByteBuffer是元空间的直接内存，需要手动free。这里image信息已经写入堆区byte[]，可以及时free
        image.close();

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        recognizeTask.setOriginBytes(bytes);
        recognizeTask.setOriginBitmap(Bitmap.createBitmap(bitmap, 0, 0, width, height));

        recognizeTask.recordTimeConsumeEnd(TaskType.COPY);
        return recognizeTask;
    }

    public static Processor<RecognizeTask, RecognizeTask> getInstance() {
        return BITMAP_PROCESSOR;
    }
}
