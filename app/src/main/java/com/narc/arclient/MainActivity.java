package com.narc.arclient;//package com.narc.arclient;
//
//import static androidx.core.content.ContentProviderCompat.requireContext;
//import static com.narc.arclient.enums.CameraEnums.CAMERA_PERMISSION_REQUEST_CODE;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.graphics.SurfaceTexture;
//import android.hardware.camera2.CameraAccessException;
//import android.hardware.camera2.CameraCaptureSession;
//import android.hardware.camera2.CameraDevice;
//import android.hardware.camera2.CameraManager;
//import android.hardware.camera2.CaptureRequest;
//import android.hardware.camera2.CaptureResult;
//import android.hardware.camera2.TotalCaptureResult;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.util.Size;
//import android.view.Surface;
//import android.view.TextureView;
//import android.view.TextureView.SurfaceTextureListener;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import com.narc.arclient.R;
//import com.narc.arclient.camera.ICameraManager;
//import com.narc.arclient.processor.RecognizeProcessor;
//
//import java.util.Arrays;
//
//public class MainActivity extends AppCompatActivity {
//    private TextureView imgTextureView;
//    private ICameraManager iCameraManager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        imgTextureView = findViewById(R.id.imgTextureView);
//
//        RecognizeProcessor.init(this);
//        ICameraManager.init(this);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        iCameraManager.permissionResultCallback(requestCode, permissions, grantResults);
//    }
//}

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.stub.StreamObserver;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "GrpcDemo";

    private static final int PROT = 56322;
    private static final String NAME = "hello world";
    private static final String HOST = "localhost";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "start");
        startServer(PROT);
        Log.d(TAG, "start server.");
        startClient(HOST, PROT, NAME);
        Log.d(TAG, "start client.");
    }

    private void startServer(int port){
        try {
            NettyServerBuilder.forPort(port)
                    .addService(new GreeterImpl())
                    .build()
                    .start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
        }
    }

    private void startClient(String host, int port, String name){
        ManagedChannel mChannel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        GreeterGrpc.GreeterStub stub = GreeterGrpc.newStub(mChannel);
        HelloRequest message = HelloRequest.newBuilder().setName(name).build();
        stub.sayHello(message, new StreamObserver<HelloReply>() {
            @Override
            public void onNext(HelloReply value) {
                //Log.d(TAG, "sayHello onNext.");
                Log.d(TAG, value.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                Log.d(TAG, "sayHello onError.");
            }

            @Override
            public void onCompleted() {
                Log.d(TAG, "sayHello onCompleted.");
            }
        });
    }

    private class GreeterImpl extends GreeterGrpc.GreeterImplBase {
        public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            responseObserver.onNext(sayHello(request));
            responseObserver.onCompleted();
        }

        private HelloReply sayHello(HelloRequest request) {
            return HelloReply.newBuilder()
                    .setMessage(request.getName())
                    .build();
        }
    }

}

