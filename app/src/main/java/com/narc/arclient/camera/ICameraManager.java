package com.narc.arclient.camera;

import static android.content.Context.CAMERA_SERVICE;
import static android.hardware.camera2.params.SessionConfiguration.SESSION_REGULAR;

import static com.narc.arclient.enums.CameraEnums.CAMERA_PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.SessionConfiguration;
import android.media.ImageReader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.narc.arclient.MainActivity;
import com.narc.arclient.R;
import com.narc.arclient.camera.callback.CameraImageAvailableListener;
import com.narc.arclient.camera.callback.CameraStateCallback;
import com.narc.arclient.enums.CameraEnums;
import com.narc.arclient.processor.ProcessorManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ICameraManager {
    private static volatile ICameraManager iCameraManager;

    private MainActivity mainActivity;

    private CameraDevice cameraDevice;
    private CaptureRequest.Builder captureRequestBuilder;
    private ImageReader imageReader;

    private ICameraManager(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public static void init(MainActivity mainActivity) {
        iCameraManager = new ICameraManager(mainActivity);

        iCameraManager.checkCameraPermission();
    }

    public void checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.CAMERA}, CameraEnums.CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    private void openCamera() {
        CameraManager cameraManager = (CameraManager) mainActivity.getSystemService(CAMERA_SERVICE);
        String cameraId;

        try {
            cameraId = cameraManager.getCameraIdList()[0];
            if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
            Size[] jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            int width = 640;
            int height = 480;
            if (jpegSizes != null && jpegSizes.length > 0) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            // todo 这里maxImages设置还需要再考虑一下
            imageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 5);

            // Handler设置为null意味着CameraImageAvailableListener的回调在UI线程执行，需要异步
            imageReader.setOnImageAvailableListener(new CameraImageAvailableListener(), new Handler(Looper.getMainLooper()));

            cameraManager.openCamera(cameraId, ProcessorManager.normalExecutor, new CameraStateCallback());
        } catch (CameraAccessException e) {
            throw new RuntimeException("can not access camera");
        }
    }

    public void permissionResultCallback(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                throw new RuntimeException("camera permission denied");
            }
        }
    }

    public CameraDevice getCameraDevice() {
        return cameraDevice;
    }

    public void setCameraDevice(CameraDevice cameraDevice) {
        this.cameraDevice = cameraDevice;
    }

    public CaptureRequest.Builder getCaptureRequestBuilder() {
        return captureRequestBuilder;
    }

    public void setCaptureRequestBuilder(CaptureRequest.Builder captureRequestBuilder) {
        this.captureRequestBuilder = captureRequestBuilder;
    }

    public AppCompatActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public ImageReader getImageReader() {
        return imageReader;
    }

    public void setImageReader(ImageReader imageReader) {
        this.imageReader = imageReader;
    }

    public static ICameraManager getInstance() {
        return iCameraManager;
    }
}
