package com.narc.arclient.process;

import static android.content.ContentValues.TAG;

import org.pytorch.Tensor;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class TFLiteModel {
    private AssetManager assetManager;

    public static void Init(AssetManager assetManager) {
        try {
            TF_LITE_MODEL = new TFLiteModel(assetManager);
        } catch (IOException e) {
            Log.d(TAG, "TFLiteModel init error");
        }
    }

    private static TFLiteModel TF_LITE_MODEL;
    private Interpreter interpreter;

    public TFLiteModel(AssetManager assetManager) throws IOException {
        this.assetManager = assetManager;
        interpreter = new Interpreter(loadModelFile(assetManager, "simple_model.tflite"));
    }

    private MappedByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public float[][][] runInference(float[][] input) {
        float[][][] output = new float[1][25200][85]; // 根据YOLOv5的输出格式调整
        interpreter.run(input, output);
        return output;
    }

    public float[][][][] runInferencePart1(float[][] input) {
        float[][][][] output = new float[1][32][320][320];
        interpreter.run(input, output);
        return output; // 返回预测结果
    }

    public float runInference(float input) {
        float[][] output = new float[1][1];
        interpreter.run(new float[][]{ {input} }, output);
        return output[0][0]; // 返回预测结果
    }

    public float[][] preprocessImage(String assetFileName) throws IOException {
        InputStream inputStream = assetManager.open(assetFileName);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

        float[][] input = new float[1][640 * 640 * 3]; // 假设输入为640x640 RGB图像
        int[] intValues = new int[640 * 640];

        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int i = 0; i < intValues.length; ++i) {
            final int val = intValues[i];
            input[0][i * 3 + 0] = ((val >> 16) & 0xFF) / 255.0f; // R
            input[0][i * 3 + 1] = ((val >> 8) & 0xFF) / 255.0f;  // G
            input[0][i * 3 + 2] = (val & 0xFF) / 255.0f;         // B
        }
        System.out.println(input);
        return input;
    }

    private Tensor preprocessBitmap(Bitmap bitmap) {
        // 确保输入Bitmap为640x640，若需要则添加调整逻辑
        float[] inputArray = new float[640 * 640 * 3]; // RGB

        for (int y = 0; y < 640; y++) {
            for (int x = 0; x < 640; x++) {
                int pixel = bitmap.getPixel(x, y);
                inputArray[y * 640 * 3 + x * 3 + 0] = ((pixel >> 16) & 0xff) / 255.0f; // R
                inputArray[y * 640 * 3 + x * 3 + 1] = ((pixel >> 8) & 0xff) / 255.0f;  // G
                inputArray[y * 640 * 3 + x * 3 + 2] = (pixel & 0xff) / 255.0f;         // B
            }
        }

        return Tensor.fromBlob(inputArray, new long[]{1, 3, 640, 640}); // CHW格式
    }


    public void close() {
        interpreter.close();
    }

    public static TFLiteModel getInstance() {
        return TF_LITE_MODEL;
    }
}

