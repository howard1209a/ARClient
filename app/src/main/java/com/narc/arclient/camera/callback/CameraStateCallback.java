package com.narc.arclient.camera.callback;

import static android.content.ContentValues.TAG;
import static android.hardware.camera2.params.SessionConfiguration.SESSION_REGULAR;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.SessionConfiguration;
import android.util.Log;

import androidx.annotation.NonNull;

import com.narc.arclient.camera.ICameraManager;
import com.narc.arclient.process.ProcessorManager;

import java.util.Arrays;

public class CameraStateCallback extends CameraDevice.StateCallback {

    @Override
    public void onOpened(@NonNull CameraDevice camera) {
        ICameraManager iCameraManager = ICameraManager.getInstance();
        iCameraManager.setCameraDevice(camera);

        try {
            CaptureRequest.Builder captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//            captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
            captureRequestBuilder.addTarget(iCameraManager.getImageReader().getSurface());
            iCameraManager.setCaptureRequestBuilder(captureRequestBuilder);

            SessionConfiguration sessionConfiguration = new SessionConfiguration(SESSION_REGULAR, Arrays.asList(new OutputConfiguration(iCameraManager.getImageReader().getSurface())), ProcessorManager.normalExecutor, new CameraCaptureStateCallback());
            camera.createCaptureSession(sessionConfiguration);
        } catch (CameraAccessException e) {
            Log.e(TAG, e.toString());
        }
    }


    @Override
    public void onDisconnected(@NonNull CameraDevice camera) {
        Log.e(TAG, "camera disconnected");
    }

    @Override
    public void onError(@NonNull CameraDevice camera, int error) {
        Log.e(TAG, "camera error");
    }
}
