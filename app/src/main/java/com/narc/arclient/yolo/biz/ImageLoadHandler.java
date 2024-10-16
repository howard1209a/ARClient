package com.narc.arclient.yolo.biz;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.narc.arclient.yolo.model.Context;

import org.pytorch.Tensor;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

public class ImageLoadHandler implements Handler {
    private final AssetManager assetManager;

    private static ImageLoadHandler imageLoadHandler;

    private ImageLoadHandler(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public static void init(AssetManager assetManager) {
        imageLoadHandler = new ImageLoadHandler(assetManager);
    }


    @Override
    public Context handle(Context ctx) {
        long startTime = System.currentTimeMillis();

        InputStream imgStream;
        try {
            imgStream = assetManager.open(ctx.imgName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Bitmap bitmap = BitmapFactory.decodeStream(imgStream);
        ctx.originBitmap = bitmap;

        float[] inputArray = new float[640 * 640 * 3]; // RGB

        for (int y = 0; y < 640; y++) {
            for (int x = 0; x < 640; x++) {
                int pixel = bitmap.getPixel(x, y);
                inputArray[0 * 640 * 640 + y * 640 + x] = ((pixel >> 16) & 0xff) / 255.0f; // R
                inputArray[1 * 640 * 640 + y * 640 + x] = ((pixel >> 8) & 0xff) / 255.0f;  // G
                inputArray[2 * 640 * 640 + y * 640 + x] = (pixel & 0xff) / 255.0f;         // B
            }
        }

        ctx.inputTensor = Tensor.fromBlob(inputArray, new long[]{1, 3, 640, 640});// CHW格式

        long endTime = System.currentTimeMillis();
        ctx.outcomeAssessment.imageLoadTime = endTime - startTime;

        return ctx;
    }

    public void test() {
        System.out.println("in");
    }

    public static ImageLoadHandler getInstance() {
        return imageLoadHandler;
    }
}
