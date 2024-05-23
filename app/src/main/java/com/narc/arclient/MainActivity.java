package com.narc.arclient;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static com.narc.arclient.enums.CameraEnums.CAMERA_PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.narc.arclient.R;
import com.narc.arclient.camera.ICameraManager;
import com.narc.arclient.processor.RecognizeProcessor;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private TextureView imgTextureView;
    private ICameraManager iCameraManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgTextureView = findViewById(R.id.imgTextureView);

        RecognizeProcessor.init(this);
        ICameraManager.init(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        iCameraManager.permissionResultCallback(requestCode, permissions, grantResults);
    }
}
