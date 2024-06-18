package com.narc.arclient;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
//        TextureView imgTextureView = ICameraManager.getInstance().getMainActivity().findViewById(R.id.imgTextureView);



        mBindingPair.updateView(new Function1<ActivityMainBinding, Unit>() {
            @Override
            public Unit invoke(ActivityMainBinding activityMainBinding) {
                mBindingPair.updateView(new Function1<ActivityMainBinding, Unit>() {
                    @Override
                    public Unit invoke(ActivityMainBinding activityMainBinding) {
                        View myButton = activityMainBinding.myButton;
                        // 修改按钮大小
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.width = 300; // 设置宽度为300像素
                        params.height = 150; // 设置高度为150像素
                        myButton.setLayoutParams(params);

                        // 修改按钮位置
                        myButton.setX(50); // 设置按钮的X坐标位置
                        myButton.setY(100); // 设置按钮的Y坐标位置

                        return null;
                    }
                });
                return null;
            }
        });

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

