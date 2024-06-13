package com.narc.arclient.network;

import static android.content.ContentValues.TAG;

import static com.narc.arclient.enums.NetworkEnums.HOST;
import static com.narc.arclient.enums.NetworkEnums.PORT;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.TextureView;

import com.google.protobuf.ByteString;
import com.narc.arclient.R;
import com.narc.arclient.camera.ICameraManager;
import com.narc.arclient.entity.RecognizeTask;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class RemoteRecognizeServiceStub {
    private static final RemoteRecognizeServiceStub REMOTE_RECOGNIZE_SERVICE_STUB = new RemoteRecognizeServiceStub();
    private RemoteRecognizeServiceGrpc.RemoteRecognizeServiceStub stub;

    private RemoteRecognizeServiceStub() {
        initStub();
    }

    private void initStub() {
        ManagedChannel mChannel = ManagedChannelBuilder.forAddress(HOST, PORT).usePlaintext().build();
        stub = RemoteRecognizeServiceGrpc.newStub(mChannel);
    }

    public void recognize(RecognizeTask recognizeTask) {
        ByteString byteString = ByteString.copyFrom(recognizeTask.getOriginBytes());
        RecognizeRequest recognizeRequest = RecognizeRequest.newBuilder().setBitmapData(byteString).build();

        stub.recognize(recognizeRequest, new StreamObserver<RecognizeResponse>() {
            // 所有回调在 gRPC 库管理的线程池中执行
            @Override
            public void onNext(RecognizeResponse value) {
                TextureView imgTextureView = ICameraManager.getInstance().getMainActivity().findViewById(R.id.imgTextureView);
                Canvas canvas = imgTextureView.lockCanvas();
                if (canvas != null) {
                    Bitmap bitmap = recognizeTask.getOriginBitmap();
                    // 获取Bitmap的宽高
                    int bitmapWidth = bitmap.getWidth();
                    int bitmapHeight = bitmap.getHeight();

                    // 获取TextureView的宽高
                    int viewWidth = imgTextureView.getWidth();
                    int viewHeight = imgTextureView.getHeight();

                    // 计算缩放比例
                    float scale = Math.min((float) viewWidth / bitmapWidth, (float) viewHeight / bitmapHeight);

                    // 计算Bitmap绘制的起始位置，使其居中显示
                    float dx = (viewWidth - bitmapWidth * scale) / 2;
                    float dy = (viewHeight - bitmapHeight * scale) / 2;

                    // 设置缩放和位移
                    canvas.save();
                    canvas.translate(dx, dy);
                    canvas.scale(scale, scale);

                    // 绘制Bitmap
                    canvas.drawBitmap(bitmap, 0, 0, null);

                    // 恢复Canvas状态
                    canvas.restore();

                    imgTextureView.unlockCanvasAndPost(canvas);
                }

            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, t.toString());
            }

            @Override
            public void onCompleted() {

            }
        });
    }

    public static RemoteRecognizeServiceStub getInstance() {
        return REMOTE_RECOGNIZE_SERVICE_STUB;
    }
}
