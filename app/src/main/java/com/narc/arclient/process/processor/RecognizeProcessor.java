package com.narc.arclient.process.processor;

import static com.narc.arclient.enums.ProcessorEnums.DEFAULT_HAND_DETECTION_CONFIDENCE;
import static com.narc.arclient.enums.ProcessorEnums.DEFAULT_HAND_PRESENCE_CONFIDENCE;
import static com.narc.arclient.enums.ProcessorEnums.DEFAULT_HAND_TRACKING_CONFIDENCE;
import static com.narc.arclient.enums.ProcessorEnums.MP_RECOGNIZER_TASK;

import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.components.containers.Category;
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;
import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer;
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult;
import com.narc.arclient.MainActivity;
import com.narc.arclient.entity.RecognizeTask;
import com.narc.arclient.entity.Rectangle;
import com.narc.arclient.entity.RenderData;
import com.narc.arclient.enums.TaskType;
import com.narc.arclient.process.Processor;

import java.util.List;

public class RecognizeProcessor implements Processor<RecognizeTask, RecognizeTask> {
    private static volatile RecognizeProcessor recognizeProcessor;
    private final MainActivity mainActivity;
    private GestureRecognizer recognizer;

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

        this.recognizer = GestureRecognizer.createFromOptions(mainActivity, options);
    }

    @Override
    public RecognizeTask process(RecognizeTask recognizeTask) {
        recognizeTask.recordTimeConsumeStart(TaskType.RECOGNIZE);

        MPImage mpImage = recognizeTask.getMpImage();
        GestureRecognizerResult gestureRecognizerResult = recognizer.recognize(mpImage);
        recognizeTask.setGestureRecognizerResult(gestureRecognizerResult);

        List<List<Category>> gestures = gestureRecognizerResult.gestures();
        if (!gestures.isEmpty()) {
            List<NormalizedLandmark> normalizedLandmarks = gestureRecognizerResult.landmarks().get(0);
            Rectangle rectangle = detectRectangle(normalizedLandmarks);
            Category category = gestures.get(0).get(0);
            recognizeTask.setRenderData(new RenderData(rectangle, category.categoryName()));
        }

        recognizeTask.recordTimeConsumeEnd(TaskType.RECOGNIZE);
        return recognizeTask;
    }

    /**
     * 找到包含所有点的最小矩形，原点在左上角
     */
    private Rectangle detectRectangle(List<NormalizedLandmark> normalizedLandmarks) {
        float x1 = normalizedLandmarks.get(0).x();
        float y1 = normalizedLandmarks.get(0).y();
        float x2 = normalizedLandmarks.get(0).x();
        float y2 = normalizedLandmarks.get(0).y();

        for (NormalizedLandmark normalizedLandmark : normalizedLandmarks) {
            x1 = Math.min(x1, normalizedLandmark.x());
            y1 = Math.min(y1, normalizedLandmark.y());
            x2 = Math.max(x2, normalizedLandmark.x());
            y2 = Math.max(y2, normalizedLandmark.y());
        }

        return new Rectangle(x1, y1, x2, y2);
    }

    public static void init(MainActivity mainActivity) {
        recognizeProcessor = new RecognizeProcessor(mainActivity);
    }

    public static Processor<RecognizeTask, RecognizeTask> getInstance() {
        return recognizeProcessor;
    }
}
