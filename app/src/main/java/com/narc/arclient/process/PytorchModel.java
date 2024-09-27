package com.narc.arclient.process;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class PytorchModel {
    private static PytorchModel pytorchModel;
    private AssetManager assetManager;
    private File cacheDir;
    private File fileDir;

    public static void Init(AssetManager assetManager, File cacheDir, File fileDir) {
        pytorchModel = new PytorchModel(assetManager, cacheDir, fileDir);
    }

    private PytorchModel(AssetManager assetManager, File cacheDir, File fileDir) {
        this.assetManager = assetManager;
        this.cacheDir = cacheDir;
        this.fileDir = fileDir;
    }

    public void recognize() {
        String path1 = copyAssetToFile("backbone_model_1.ptl");
        String path2 = copyAssetToFile("backbone_model_2.ptl");

        // 加载模型
        Module model1 = Module.load(path1);
        Module model2 = Module.load(path2);

        // 加载图像
        Bitmap bitmap = loadImageFromAssets("bus_preprocessed.jpg");

        // 预处理图像并转换为Tensor
        Tensor inputTensor = preprocessBitmap(bitmap);

        IValue iValue = IValue.from(inputTensor);

        IValue forwarded1 = model1.forward(iValue);
        IValue forwarded2 = model2.forward(forwarded1);

        // 执行推理
        Tensor outputTensor = forwarded2.toTensor();

        // 使用示例
        saveTensorToNpy(outputTensor, "output_tensor.npy");

        System.out.println("2");
    }

    private void saveTensorToNpy(Tensor tensor, String filename) {
        try {
            // 获取应用的文件目录
            File file = new File("/data/local/tmp", filename);

            // 准备 NumPy 的 .npy 头部信息
            ByteBuffer header = ByteBuffer.allocate(128);
            header.put("x".getBytes()); // Magic string
            header.put("npy".getBytes()); // Version
            header.put(new byte[]{0x01, 0x00}); // Version number
            header.put((byte) 0x00); // Data type (float32)
            header.put((byte) 0x20); // Endianness
            header.putInt(1); // Number of dimensions
            header.putInt(4); // Data type (float32)
            header.putInt(320); // Shape dimensions
            header.putInt(320); // Shape dimensions
            header.putInt(32); // Shape dimensions
            header.putInt(1); // Shape dimensions
            header.putInt(0); // Padding

            // 将 header 写入文件
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(header.array());

            // 将 Tensor 数据写入文件
            float[] tensorData = tensor.getDataAsFloatArray(); // 假设 Tensor 支持这种方法
            ByteBuffer buffer = ByteBuffer.allocate(tensorData.length * 4); // 每个 float 占 4 字节

            for (int i = 0; i < 10; i++) {
                buffer.putFloat(tensorData[i]);
            }

            fos.write(buffer.array());
            fos.close();

            System.out.println("Tensor saved to " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap loadImageFromAssets(String fileName) {
        try {
            InputStream is = assetManager.open(fileName);
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Tensor preprocessBitmap(Bitmap bitmap) {
        // 确保输入Bitmap为640x640，若需要则添加调整逻辑
        float[] inputArray = new float[640 * 640 * 3]; // RGB

        for (int y = 0; y < 640; y++) {
            for (int x = 0; x < 640; x++) {
                int pixel = bitmap.getPixel(x, y);
//                inputArray[y * 640 * 3 + x * 3 + 0] = ((pixel >> 16) & 0xff) / 255.0f; // R
//                inputArray[y * 640 * 3 + x * 3 + 1] = ((pixel >> 8) & 0xff) / 255.0f;  // G
//                inputArray[y * 640 * 3 + x * 3 + 2] = (pixel & 0xff) / 255.0f;         // B

                inputArray[0 * 640 * 640 + y * 640 + x] = ((pixel >> 16) & 0xff) / 255.0f; // R
                inputArray[1 * 640 * 640 + y * 640 + x] = ((pixel >> 8) & 0xff) / 255.0f;  // G
                inputArray[2 * 640 * 640 + y * 640 + x] = (pixel & 0xff) / 255.0f;         // B
            }
        }

        return Tensor.fromBlob(inputArray, new long[]{1, 3, 640, 640}); // CHW格式
    }


    private String assetFilePath(String assetName) {
        AssetFileDescriptor fileDescriptor;
        try {
            fileDescriptor = assetManager.openFd(assetName);
            return fileDescriptor.getFileDescriptor().toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String copyAssetToFile(String assetFileName) {
        File outFile = new File(cacheDir, assetFileName);

        try (InputStream in = assetManager.open(assetFileName);
             OutputStream out = new FileOutputStream(outFile)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return outFile.getAbsolutePath();  // 返回文件的绝对路径
    }


    public static PytorchModel getInstance() {
        return pytorchModel;
    }
}
