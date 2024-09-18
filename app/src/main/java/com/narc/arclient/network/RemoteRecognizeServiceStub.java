package com.narc.arclient.network;

import static android.content.ContentValues.TAG;

import static com.narc.arclient.enums.NetworkEnums.HOST;
import static com.narc.arclient.enums.NetworkEnums.PORT;
import static com.narc.arclient.enums.TaskType.COMPUTE_REMOTE;
import static com.narc.arclient.enums.TaskType.TRANSFER_2_LOCAL;
import static com.narc.arclient.enums.TaskType.TRANSFER_2_REMOTE;

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
        recognizeTask.setTimeConsume(TRANSFER_2_REMOTE, System.currentTimeMillis());

        // copyFrom不是实际内存拷贝，不耗时
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

                recognizeTask.setTimeConsume(TRANSFER_2_REMOTE, recognizeResponse.getRecieveTime() - recognizeTask.getTimeConsume(TRANSFER_2_REMOTE));
                recognizeTask.setTimeConsume(COMPUTE_REMOTE, recognizeResponse.getSendbackTime() - recognizeResponse.getRecieveTime());
                recognizeTask.setTimeConsume(TRANSFER_2_LOCAL, System.currentTimeMillis() - recognizeResponse.getSendbackTime());

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

    public void logReport(RecognizeTask recognizeTask) {
        LogReportRequest logReportRequest = LogReportRequest.newBuilder()
                .setDeviceSerialNumber(recognizeTask.getDeviceSerialNumber())
                .setTaskId(recognizeTask.getTaskId())
                .setUnloadEnd(recognizeTask.getUnloadEnd())
                .setStartTime(recognizeTask.getStartTime())
                .setEndTime(recognizeTask.getEndTime())
                .setPosExist(recognizeTask.getPosExist())
                .setCopyTime(recognizeTask.getTimeConsume(TaskType.COPY))
                .setPreprocessTime(recognizeTask.getTimeConsume(TaskType.PREPROCESS))
                .setRecognizeTime(recognizeTask.getTimeConsume(TaskType.RECOGNIZE))
                .setRenderTime(recognizeTask.getTimeConsume(TaskType.RENDER))
                .setTransfer2RemoteTime(recognizeTask.getTimeConsume(TRANSFER_2_REMOTE))
                .setComputeRemoteTime(recognizeTask.getTimeConsume(COMPUTE_REMOTE))
                .setTransfer2LocalTime(recognizeTask.getTimeConsume(TRANSFER_2_LOCAL))
                .build();

        stub.logReport(logReportRequest, new StreamObserver<EmptyResponse>() {
            @Override
            public void onNext(EmptyResponse value) {

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

    public void systemStateReport(String deviceSerialNumber, long timestamp, String cpuUsage, String memUsage, String batteryLevel) {
        SystemStateRequest systemStateRequest = SystemStateRequest.newBuilder()
                .setDeviceSerialNumber(deviceSerialNumber)
                .setTimestamp(timestamp)
                .setCpuUsage(cpuUsage)
                .setMemUsage(memUsage)
                .setBatteryLevel(batteryLevel)
                .build();

        stub.systemState(systemStateRequest, new StreamObserver<EmptyResponse>() {
            @Override
            public void onNext(EmptyResponse value) {

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
