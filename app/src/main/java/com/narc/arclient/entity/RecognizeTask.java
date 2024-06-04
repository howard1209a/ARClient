package com.narc.arclient.entity;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;

import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult;
import com.narc.arclient.enums.TaskType;

public class RecognizeTask {
    private Image image;
    private Bitmap originBitmap;
    private MPImage mpImage;
    private GestureRecognizerResult gestureRecognizerResult;
    private Bitmap renderedBitmap;
    private TimeConsumer timeConsumer;

    public static class TimeConsumer {
        private long copyTime;
        private long preprocessTime;
        private long recognizeTime;
        private long renderTime;
    }


    public RecognizeTask(Image image) {
        this.image = image;
        this.timeConsumer = new TimeConsumer();
    }

    public void recordTimeConsumeStart(TaskType taskType) {
        if (taskType == TaskType.COPY) {
            timeConsumer.copyTime = System.currentTimeMillis();
        } else if (taskType == TaskType.PREPROCESS) {
            timeConsumer.preprocessTime = System.currentTimeMillis();
        } else if (taskType == TaskType.RECOGNIZE) {
            timeConsumer.recognizeTime = System.currentTimeMillis();
        } else if (taskType == TaskType.RENDER) {
            timeConsumer.renderTime = System.currentTimeMillis();
        }
    }

    public void recordTimeConsumeEnd(TaskType taskType) {
        if (taskType == TaskType.COPY) {
            timeConsumer.copyTime = System.currentTimeMillis() - timeConsumer.copyTime;
        } else if (taskType == TaskType.PREPROCESS) {
            timeConsumer.preprocessTime = System.currentTimeMillis() - timeConsumer.preprocessTime;
        } else if (taskType == TaskType.RECOGNIZE) {
            timeConsumer.recognizeTime = System.currentTimeMillis() - timeConsumer.recognizeTime;
        } else if (taskType == TaskType.RENDER) {
            timeConsumer.renderTime = System.currentTimeMillis() - timeConsumer.renderTime;
        }
    }

    public void timeConsumeLog() {
        Log.d(TAG, String.format("task end --- copy: %dms preprocess: %dms recognize: %dms render: %dms", timeConsumer.copyTime, timeConsumer.preprocessTime, timeConsumer.recognizeTime, timeConsumer.renderTime));
    }

    public Bitmap getOriginBitmap() {
        return originBitmap;
    }

    public void setOriginBitmap(Bitmap originBitmap) {
        this.originBitmap = originBitmap;
    }

    public MPImage getMpImage() {
        return mpImage;
    }

    public void setMpImage(MPImage mpImage) {
        this.mpImage = mpImage;
    }

    public GestureRecognizerResult getGestureRecognizerResult() {
        return gestureRecognizerResult;
    }

    public void setGestureRecognizerResult(GestureRecognizerResult gestureRecognizerResult) {
        this.gestureRecognizerResult = gestureRecognizerResult;
    }

    public Bitmap getRenderedBitmap() {
        return renderedBitmap;
    }

    public void setRenderedBitmap(Bitmap renderedBitmap) {
        this.renderedBitmap = renderedBitmap;
    }

    public Image getImage() {
        return image;
    }
}