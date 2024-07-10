package com.narc.arclient.entity;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;

import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult;
import com.google.protobuf.ByteString;
import com.narc.arclient.enums.TaskType;
import com.narc.arclient.process.ProcessorManager;

public class RecognizeTask {
    private String deviceSerialNumber = ProcessorManager.deviceSerialNumber;
    private String taskId;
    private String unloadEnd;
    private String startTime;
    private String endTime;
    private String posExist; // 0=not exist 1=exist

    private Image image;
    private byte[] originBytes;
    private Bitmap originBitmap;
    private MPImage mpImage;
    private GestureRecognizerResult gestureRecognizerResult;

    private RenderData renderData;
    private TimeConsumer timeConsumer;

    public static class TimeConsumer {
        private long copyTime;
        private long preprocessTime;
        private long recognizeTime;
        private long renderTime;
        private long transfer2RemoteTime;
        private long computeRemoteTime;
        private long transfer2LocalTime;
    }


    public RecognizeTask(Image image) {
        this.image = image;
        this.timeConsumer = new TimeConsumer();
        this.taskId = ProcessorManager.TASK_ID.getAndIncrement() + "";
        this.startTime = System.currentTimeMillis() + "";
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

    public void setTimeConsume(TaskType taskType, long time) {
        if (taskType == TaskType.COPY) {
            timeConsumer.copyTime = time;
        } else if (taskType == TaskType.PREPROCESS) {
            timeConsumer.preprocessTime = time;
        } else if (taskType == TaskType.RECOGNIZE) {
            timeConsumer.recognizeTime = time;
        } else if (taskType == TaskType.RENDER) {
            timeConsumer.renderTime = time;
        } else if (taskType == TaskType.TRANSFER_2_REMOTE) {
            timeConsumer.transfer2RemoteTime = time;
        } else if (taskType == TaskType.COMPUTE_REMOTE) {
            timeConsumer.computeRemoteTime = time;
        } else {
            timeConsumer.transfer2LocalTime = time;
        }
    }

    public long getTimeConsume(TaskType taskType) {
        if (taskType == TaskType.COPY) {
            return timeConsumer.copyTime;
        } else if (taskType == TaskType.PREPROCESS) {
            return timeConsumer.preprocessTime;
        } else if (taskType == TaskType.RECOGNIZE) {
            return timeConsumer.recognizeTime;
        } else if (taskType == TaskType.RENDER) {
            return timeConsumer.renderTime;
        } else if (taskType == TaskType.TRANSFER_2_REMOTE) {
            return timeConsumer.transfer2RemoteTime;
        } else if (taskType == TaskType.COMPUTE_REMOTE) {
            return timeConsumer.computeRemoteTime;
        } else {
            return timeConsumer.transfer2LocalTime;
        }
    }

    public void timeConsumeLog() {
        Log.d(TAG, String.format("deviceSerialNumber %s | taskId %s | unloadEnd %s | startTime %s | endTime %s | copyTime %dms | preprocessTime %dms | recognizeTime %dms | renderTime %dms | transfer2RemoteTime %dms | computeRemoteTime %dms | transfer2LocalTime %dms | posExist %s", deviceSerialNumber, taskId, unloadEnd, startTime, endTime, timeConsumer.copyTime, timeConsumer.preprocessTime, timeConsumer.recognizeTime, timeConsumer.renderTime, timeConsumer.transfer2RemoteTime, timeConsumer.computeRemoteTime, timeConsumer.transfer2LocalTime, posExist));
    }

    public byte[] getOriginBytes() {
        return originBytes;
    }

    public void setOriginBytes(byte[] originBytes) {
        this.originBytes = originBytes;
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

    public Image getImage() {
        return image;
    }

    public RenderData getRenderData() {
        return renderData;
    }

    public void setRenderData(RenderData renderData) {
        this.renderData = renderData;
    }

    public String getUnloadEnd() {
        return unloadEnd;
    }

    public void setUnloadEnd(String unloadEnd) {
        this.unloadEnd = unloadEnd;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getPosExist() {
        return posExist;
    }

    public void setPosExist(String posExist) {
        this.posExist = posExist;
    }

    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getStartTime() {
        return startTime;
    }
}