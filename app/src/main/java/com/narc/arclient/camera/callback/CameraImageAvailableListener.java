package com.narc.arclient.camera.callback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.Image;
import android.media.ImageReader;
import android.view.TextureView;
import android.view.View;

import com.narc.arclient.R;
import com.narc.arclient.camera.ICameraManager;
import com.narc.arclient.processor.BitmapProcessor;

import java.nio.ByteBuffer;

public class CameraImageAvailableListener implements ImageReader.OnImageAvailableListener {
    private ICameraManager iCameraManager;

    public CameraImageAvailableListener(ICameraManager iCameraManager) {
        this.iCameraManager = iCameraManager;
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        Image image = null;
        try {
            image = reader.acquireLatestImage();
            Bitmap bitmap = new BitmapProcessor().process(image);
            TextureView imgTextureView = iCameraManager.getMainActivity().findViewById(R.id.imgTextureView);

            Canvas canvas = imgTextureView.lockCanvas();
            if (canvas != null) {
                canvas.drawBitmap(bitmap, 0, 0, null);
                imgTextureView.unlockCanvasAndPost(canvas);
            }
        } finally {
            if (image != null) {
                image.close();
            }
        }
    }
}
