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
import com.narc.arclient.entity.Rectangle;
import com.narc.arclient.entity.RenderData;
import com.narc.arclient.enums.TaskType;
import com.narc.arclient.process.processor.RenderProcessor;

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
            public void onNext(RecognizeResponse recognizeResponse) {
                if (!recognizeResponse.getGesture().equals("")) {
                    float x1 = recognizeResponse.getX1();
                    float y1 = recognizeResponse.getY1();
                    float x2 = recognizeResponse.getX2();
                    float y2 = recognizeResponse.getY2();
                    RenderData renderData = new RenderData(new Rectangle(x1, y1, x2, y2), recognizeResponse.getGesture());
                    recognizeTask.setRenderData(renderData);
                }

                recognizeTask.recordTimeConsumeEnd(TaskType.REMOTE);
                RenderProcessor.getInstance().process(recognizeTask);
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
