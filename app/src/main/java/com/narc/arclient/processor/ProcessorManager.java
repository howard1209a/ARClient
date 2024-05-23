package com.narc.arclient.processor;

import android.graphics.Bitmap;
import android.media.Image;

import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ProcessorManager {
    public static final ExecutorService executor = new ThreadPoolExecutor(2, 3, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    public static final ScheduledExecutorService scheduledExecutor = new ScheduledThreadPoolExecutor(1);

    public static class RecognizeTask {
        private Image image;
        private Bitmap originBitmap;
        private MPImage mpImage;
        private GestureRecognizerResult gestureRecognizerResult;
        private Bitmap renderedBitmap;

        public RecognizeTask(Image image) {
            this.image = image;
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

        public void setImage(Image image) {
            this.image = image;
        }
    }
}
