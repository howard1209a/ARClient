package com.narc.arclient.camera.callback;

import android.app.assist.AssistStructure;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.view.TextureView;

import androidx.annotation.NonNull;

import com.narc.arclient.R;
import com.narc.arclient.camera.ICameraManager;

public class CameraCaptureCallback extends CameraCaptureSession.CaptureCallback {

    @Override
    public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {

    }
}
