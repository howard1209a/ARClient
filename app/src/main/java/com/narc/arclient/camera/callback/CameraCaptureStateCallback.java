package com.narc.arclient.camera.callback;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;

import androidx.annotation.NonNull;

import com.narc.arclient.camera.ICameraManager;
import com.narc.arclient.processor.ProcessorManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class CameraCaptureStateCallback extends CameraCaptureSession.StateCallback {

    @Override
    public void onConfigured(@NonNull CameraCaptureSession session) {
        ProcessorManager.scheduledExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    session.captureSingleRequest(ICameraManager.getInstance().getCaptureRequestBuilder().build(), ProcessorManager.executor, new CameraCaptureCallback());
                } catch (CameraAccessException e) {
                    throw new RuntimeException(e);
                }
                ProcessorManager.scheduledExecutor.schedule(this, 200, TimeUnit.MILLISECONDS);
            }
        }, 200, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
        throw new RuntimeException("camera capture configure fail");
    }
}
