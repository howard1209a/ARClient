package com.narc.arclient.processor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.TextureView;

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult;
import com.narc.arclient.R;
import com.narc.arclient.camera.ICameraManager;

import java.util.List;

public class RenderProcessor implements Processor<ProcessorManager.RecognizeTask, ProcessorManager.RecognizeTask> {
    private static final RenderProcessor RENDER_PROCESSOR = new RenderProcessor();

    private RenderProcessor() {
    }

    @Override
    public ProcessorManager.RecognizeTask process(ProcessorManager.RecognizeTask recognizeTask) {
        Bitmap originBitmap = recognizeTask.getOriginBitmap();
        GestureRecognizerResult gestureRecognizerResult = recognizeTask.getGestureRecognizerResult();
        List<List<NormalizedLandmark>> landmarks = gestureRecognizerResult.landmarks();
        if (!landmarks.isEmpty()) {
            recognizeTask.setRenderedBitmap(drawLandmarks(originBitmap, landmarks.get(0)));
        } else {
            recognizeTask.setRenderedBitmap(originBitmap);
        }

        TextureView imgTextureView = ICameraManager.getInstance().getMainActivity().findViewById(R.id.imgTextureView);
        Canvas canvas = imgTextureView.lockCanvas();
        if (canvas != null) {
            canvas.drawBitmap(recognizeTask.getRenderedBitmap(), 0, 0, null);
            imgTextureView.unlockCanvasAndPost(canvas);
        }

        // Image底层是ByteBuffer存的，ByteBuffer是元空间的直接内存，需要手动free
        recognizeTask.getImage().close();

        return recognizeTask;
    }

    private Bitmap drawLandmarks(Bitmap bitmap, List<NormalizedLandmark> normalizedLandmarks) {
        // 创建一个可变的Bitmap副本，原Bitmap不可变
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);

        // 设置画笔属性
        Paint paint = new Paint();
        paint.setColor(Color.RED); // 圆点颜色
        paint.setStyle(Paint.Style.FILL); // 填充样式
        paint.setAntiAlias(true); // 抗锯齿
        float radius = 10; // 圆点半径

        normalizedLandmarks.stream().forEach((NormalizedLandmark normalizedLandmark) -> {
            int pixelX = (int) (normalizedLandmark.x() * bitmap.getWidth());
            int pixelY = (int) (normalizedLandmark.y() * bitmap.getHeight());
            canvas.drawCircle(pixelX, pixelY, radius, paint);
        });

        return mutableBitmap;
    }

    public static Processor<ProcessorManager.RecognizeTask, ProcessorManager.RecognizeTask> getInstance() {
        return RENDER_PROCESSOR;
    }
}
