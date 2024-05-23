package com.narc.arclient.camera.callback;

import static android.hardware.camera2.params.SessionConfiguration.SESSION_REGULAR;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.SessionConfiguration;

import androidx.annotation.NonNull;

import com.narc.arclient.R;
import com.narc.arclient.camera.ICameraManager;
import com.narc.arclient.processor.ProcessorManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class CameraStateCallback extends CameraDevice.StateCallback {

    private ICameraManager iCameraManager;

    public CameraStateCallback(ICameraManager iCameraManage) {
        this.iCameraManager = iCameraManage;
    }

    @Override
    public void onOpened(@NonNull CameraDevice camera) {
        iCameraManager.setCameraDevice(camera);

        try {
            CaptureRequest.Builder captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//            captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
            captureRequestBuilder.addTarget(iCameraManager.getImageReader().getSurface());
            iCameraManager.setCaptureRequestBuilder(captureRequestBuilder);

            SessionConfiguration sessionConfiguration = new SessionConfiguration(SESSION_REGULAR, Arrays.asList(new OutputConfiguration(iCameraManager.getImageReader().getSurface())), ProcessorManager.executor, new CameraCaptureStateCallback(iCameraManager));
            camera.createCaptureSession(sessionConfiguration);
        } catch (CameraAccessException e) {
            throw new RuntimeException("camera access exception");
        }
    }


    @Override
    public void onDisconnected(@NonNull CameraDevice camera) {

    }

    @Override
    public void onError(@NonNull CameraDevice camera, int error) {

    }
}
