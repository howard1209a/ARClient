package com.narc.arclient.processor;

import static com.narc.arclient.enums.ProcessorEnums.DEFAULT_HAND_DETECTION_CONFIDENCE;
import static com.narc.arclient.enums.ProcessorEnums.DEFAULT_HAND_PRESENCE_CONFIDENCE;
import static com.narc.arclient.enums.ProcessorEnums.DEFAULT_HAND_TRACKING_CONFIDENCE;
import static com.narc.arclient.enums.ProcessorEnums.MP_RECOGNIZER_TASK;

import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer;
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult;
import com.narc.arclient.MainActivity;

public class RecognizeProcessor implements Processor<ProcessorManager.RecognizeTask, ProcessorManager.RecognizeTask> {
    private static volatile RecognizeProcessor recognizeProcessor;
    private GestureRecognizer recognizer;
    private MainActivity mainActivity;

    private RecognizeProcessor(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        setUpGestureRecognizer();
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
        recognizer = GestureRecognizer.createFromOptions(mainActivity, options);
    }

    @Override
    public ProcessorManager.RecognizeTask process(ProcessorManager.RecognizeTask recognizeTask) {
        MPImage mpImage = recognizeTask.getMpImage();
        recognizeTask.setGestureRecognizerResult(recognizer.recognize(mpImage));
        return recognizeTask;
    }

    public static void init(MainActivity mainActivity) {
        recognizeProcessor = new RecognizeProcessor(mainActivity);
    }

    public static Processor<ProcessorManager.RecognizeTask, ProcessorManager.RecognizeTask> getInstance() {
        return recognizeProcessor;
    }
}
