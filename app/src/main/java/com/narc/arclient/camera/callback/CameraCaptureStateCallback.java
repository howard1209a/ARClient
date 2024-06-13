package com.narc.arclient.camera.callback;

import static android.content.ContentValues.TAG;
import static com.narc.arclient.enums.CameraEnums.FPS;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.util.Log;

import androidx.annotation.NonNull;

import com.narc.arclient.camera.ICameraManager;
import com.narc.arclient.process.ProcessorManager;

import java.util.concurrent.TimeUnit;

public class CameraCaptureStateCallback extends CameraCaptureSession.StateCallback {

    @Override
    public void onConfigured(@NonNull CameraCaptureSession session) {
        int capturePeriod = 1000 / FPS;
        ProcessorManager.scheduledExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    session.captureSingleRequest(ICameraManager.getInstance().getCaptureRequestBuilder().build(), ProcessorManager.normalExecutor, new CameraCaptureCallback());
                } catch (CameraAccessException e) {
                    Log.e(TAG, e.toString());
                }
                ProcessorManager.scheduledExecutor.schedule(this, capturePeriod, TimeUnit.MILLISECONDS);
            }
        }, capturePeriod, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
        Log.e(TAG, "camera capture configure fail");
    }
}
