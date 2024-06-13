package com.narc.arclient.process.processor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.TextureView;

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult;
import com.narc.arclient.R;
import com.narc.arclient.camera.ICameraManager;
import com.narc.arclient.entity.RecognizeTask;
import com.narc.arclient.enums.TaskType;
import com.narc.arclient.process.Processor;

import java.util.List;

public class RenderProcessor implements Processor<RecognizeTask, RecognizeTask> {
    private static final RenderProcessor RENDER_PROCESSOR = new RenderProcessor();

    private RenderProcessor() {
    }

    @Override
    public RecognizeTask process(RecognizeTask recognizeTask) {
        recognizeTask.recordTimeConsumeStart(TaskType.RENDER);






        recognizeTask.recordTimeConsumeEnd(TaskType.RENDER);

        // 打印本次任务各部分耗时
        recognizeTask.timeConsumeLog();

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

    public static Processor<RecognizeTask, RecognizeTask> getInstance() {
        return RENDER_PROCESSOR;
    }
}
