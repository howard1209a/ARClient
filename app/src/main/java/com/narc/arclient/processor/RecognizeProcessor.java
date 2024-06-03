package com.narc.arclient.processor;

import static androidx.core.content.ContextCompat.getSystemService;
import static com.narc.arclient.enums.ProcessorEnums.DEFAULT_HAND_DETECTION_CONFIDENCE;
import static com.narc.arclient.enums.ProcessorEnums.DEFAULT_HAND_PRESENCE_CONFIDENCE;
import static com.narc.arclient.enums.ProcessorEnums.DEFAULT_HAND_TRACKING_CONFIDENCE;
import static com.narc.arclient.enums.ProcessorEnums.MP_RECOGNIZER_TASK;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer;
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult;
import com.narc.arclient.MainActivity;
import com.narc.arclient.entity.RecognizeTask;
import com.narc.arclient.enums.TaskType;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RecognizeProcessor implements Processor<RecognizeTask, RecognizeTask> {
    private static volatile RecognizeProcessor recognizeProcessor;
    private final MainActivity mainActivity;
    private GestureRecognizer recognizer;
    private final ReentrantReadWriteLock recognizerLock;

    private RecognizeProcessor(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        setUpGestureRecognizer();
        this.recognizerLock = new ReentrantReadWriteLock();
        timelyUpdateRecognizer();
    }

    private void setUpGestureRecognizer() {
        BaseOptions.Builder baseOptionBuilder = BaseOptions.builder().setModelAssetPath(MP_RECOGNIZER_TASK);
        BaseOptions baseOptions = baseOptionBuilder.build();

        GestureRecognizer.GestureRecognizerOptions.Builder optionsBuilder = GestureRecognizer.GestureRecognizerOptions.builder()
                .setBaseOptions(baseOptions)
                .setMinHandDetectionConfidence(DEFAULT_HAND_DETECTION_CONFIDENCE)
                .setMinTrackingConfidence(DEFAULT_HAND_TRACKING_CONFIDENCE)
                .setMinHandPresenceConfidence(DEFAULT_HAND_PRESENCE_CONFIDENCE)
                .setRunningMode(RunningMode.IMAGE);

        GestureRecognizer.GestureRecognizerOptions options = optionsBuilder.build();

        this.recognizer = GestureRecognizer.createFromOptions(mainActivity, options);
    }

    private void timelyUpdateRecognizer() {
        ProcessorManager.scheduledExecutor.scheduleWithFixedDelay(() -> {
//            Intent intent = mainActivity.getBaseContext().getPackageManager()
//                    .getLaunchIntentForPackage(mainActivity.getBaseContext().getPackageName());
//            PendingIntent pendingIntent = PendingIntent.getActivity(mainActivity.getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//            AlarmManager alarmManager = (AlarmManager) mainActivity.getBaseContext().getSystemService(Context.ALARM_SERVICE);
//            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, pendingIntent);
//            System.exit(0);

            ReentrantReadWriteLock.WriteLock writeLock = recognizerLock.writeLock();
            writeLock.lock();
            recognizer.close();
            setUpGestureRecognizer();
            writeLock.unlock();
        }, 5, 5, TimeUnit.SECONDS);
    }

    @Override
    public RecognizeTask process(RecognizeTask recognizeTask) {
        recognizeTask.recordTimeConsumeStart(TaskType.RECOGNIZE);

        MPImage mpImage = recognizeTask.getMpImage();
        ReentrantReadWriteLock.ReadLock readLock = recognizerLock.readLock();
        // protect
        readLock.lock();
        GestureRecognizerResult gestureRecognizerResult = recognizer.recognize(mpImage);
        readLock.unlock();
        recognizeTask.setGestureRecognizerResult(gestureRecognizerResult);

        recognizeTask.recordTimeConsumeEnd(TaskType.RECOGNIZE);
        return recognizeTask;
    }

    public static void init(MainActivity mainActivity) {
        recognizeProcessor = new RecognizeProcessor(mainActivity);
    }

    public static Processor<RecognizeTask, RecognizeTask> getInstance() {
        return recognizeProcessor;
    }
}
