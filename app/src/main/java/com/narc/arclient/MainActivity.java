package com.narc.arclient;

import static com.rayneo.arsdk.android.core.BindingPairKt.make3DEffectForSide;

import android.os.Bundle;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.narc.arclient.camera.ICameraManager;
import com.narc.arclient.processor.RecognizeProcessor;
public class MainActivity extends AppCompatActivity {
    private TextureView imgTextureView;
    private ICameraManager iCameraManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgTextureView = findViewById(R.id.imgTextureView);
        imgTextureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apply3DEffect(true);
            }
        });
        RecognizeProcessor.init(this);
        ICameraManager.init(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        iCameraManager.permissionResultCallback(requestCode, permissions, grantResults);
    }
    private void apply3DEffect(boolean isLeft) {
        // 假设3D效果函数接受一个视图和两个布尔值：
        // 一个用于边（左或右）和一个用于焦点状态

        make3DEffectForSide(imgTextureView, isLeft, true);
    }
}
