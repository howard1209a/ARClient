package com.narc.arclient;

import android.os.Bundle;
import android.view.TextureView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.narc.arclient.camera.ICameraManager;
import com.narc.arclient.process.processor.RecognizeProcessor;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecognizeProcessor.init(this);
        ICameraManager.init(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ICameraManager.getInstance().permissionResultCallback(requestCode, permissions, grantResults);
    }
}

