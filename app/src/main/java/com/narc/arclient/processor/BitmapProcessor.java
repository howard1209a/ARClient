package com.narc.arclient.processor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;

import java.nio.ByteBuffer;

public class BitmapProcessor implements Processor<Image, Bitmap> {
    @Override
    public Bitmap process(Image image) {
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        int width = image.getWidth();
        int height = image.getHeight();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height);
    }
}
