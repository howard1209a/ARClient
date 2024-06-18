package com.narc.arclient;

import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.narc.arclient.camera.ICameraManager;
import com.narc.arclient.databinding.ActivityMainBinding;
import com.narc.arclient.front.ViewHandler;
import com.narc.arclient.process.processor.RecognizeProcessor;
import com.rayneo.arsdk.android.MercurySDK;
import com.rayneo.arsdk.android.ui.activity.BaseMirrorActivity;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class MainActivity extends BaseMirrorActivity<ActivityMainBinding> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        mBindingPair.updateView(new Function1<ActivityMainBinding, Unit>() {
//            @Override
//            public Unit invoke(ActivityMainBinding activityMainBinding) {
//                activityMainBinding.btn1.setText("hh");
//                return null;
//            }
//        });

//        setContentView(R.layout.activity_main);
//        setContentView(R.layout.handler);
//        setContentView(R.layout.activity_main);
//        TextView textView = findViewById(R.id.tv_title1);
//        textView.setText("howard");
//        ViewHandler viewHandler = new ViewHandler(this);
//        viewHandler.onInit();

        RecognizeProcessor.init(this);
        ICameraManager.init(this);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ICameraManager.getInstance().permissionResultCallback(requestCode, permissions, grantResults);
    }
}

