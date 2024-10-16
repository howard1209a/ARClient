package com.narc.arclient.yolo.biz;

import static com.narc.arclient.yolo.constant.constant.YOLO_LAYER_COUNT;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;

import com.narc.arclient.yolo.model.Context;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class YoloModelHandler implements Handler {
    private final AssetManager assetManager;
    private final File cacheDir;
    private static YoloModelHandler yoloModelHandler;
    private final List<Module> moduleList = new ArrayList<>();

    public static void init(AssetManager assetManager, File cacheDir) {
        yoloModelHandler = new YoloModelHandler(assetManager, cacheDir);
    }

    private YoloModelHandler(AssetManager assetManager, File cacheDir) {
        this.assetManager = assetManager;
        this.cacheDir = cacheDir;

        for (int i = 0; i < YOLO_LAYER_COUNT; i++) {
            @SuppressLint("DefaultLocale") String name = String.format("sub_model%d.ptl", i);
            String path = copyAssetToFile(name);
            loadModule(path);
        }
    }

    private String copyAssetToFile(String assetFileName) {
        File outFile = new File(cacheDir, assetFileName);

        try (InputStream in = assetManager.open(assetFileName);
             OutputStream out = Files.newOutputStream(outFile.toPath())) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return outFile.getAbsolutePath();
    }

    private void loadModule(String path) {
        Module module = Module.load(path);
        moduleList.add(module);
    }

    @Override
    public Context handle(Context ctx) {
        long startTime = System.currentTimeMillis();

        Tensor inputTensor = ctx.inputTensor;
        IValue inputIValue = IValue.from(inputTensor);

        for (int i = 0; i < ctx.localComputeCount; i++) {
            Module module = moduleList.get(i);
            inputIValue = module.forward(inputIValue);
            ctx.middleTensorList.add(inputIValue);
        }

        IValue outputIValue = ctx.middleTensorList.get(ctx.middleTensorList.size() - 1);
        ctx.outputTensor = outputIValue.toTensor();

        long endTime = System.currentTimeMillis();
        ctx.outcomeAssessment.localComputeTime = endTime - startTime;

        return ctx;
    }

    public static YoloModelHandler getInstance() {
        return yoloModelHandler;
    }
}
