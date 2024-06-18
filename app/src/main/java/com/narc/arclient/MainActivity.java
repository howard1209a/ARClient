package com.narc.arclient;

import static com.narc.arclient.enums.ProcessorEnums.DETECT_BOX_SIZE_SCALE;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;


import com.narc.arclient.camera.ICameraManager;
import com.narc.arclient.databinding.ActivityMainBinding;
import com.narc.arclient.entity.Rectangle;
import com.narc.arclient.entity.RenderData;
import com.narc.arclient.process.processor.RecognizeProcessor;
import com.narc.arclient.process.processor.RenderProcessor;
import com.rayneo.arsdk.android.ui.activity.BaseMirrorActivity;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class MainActivity extends BaseMirrorActivity<ActivityMainBinding> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RenderProcessor.init(this);
        RecognizeProcessor.init(this);

        ICameraManager.init(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ICameraManager.getInstance().permissionResultCallback(requestCode, permissions, grantResults);
    }

    public void updateView(RenderData renderData) {
        if (renderData == null) {
            mBindingPair.updateView(new Function1<ActivityMainBinding, Unit>() {
                @Override
                public Unit invoke(ActivityMainBinding activityMainBinding) {
                    View detectView = activityMainBinding.detectBox;
                    detectView.setVisibility(View.INVISIBLE);
                    TextView detectText = activityMainBinding.detectText;
                    detectText.setVisibility(View.INVISIBLE);
                    return null;
                }
            });
            return;
        }

        View rootView = getWindow().getDecorView().getRootView();
        int rootViewWidth = rootView.getWidth() / 2;
        int rootViewHeight = rootView.getHeight() / 2;

        Rectangle rectangle = renderData.getRectangle();
        int centerX = (int) (rootViewWidth * ((rectangle.getX1() + rectangle.getX2()) / 2));
        int centerY = (int) (rootViewHeight * ((rectangle.getY1() + rectangle.getY2()) / 2));
        int viewWidth = (int) (DETECT_BOX_SIZE_SCALE * rootViewWidth * (rectangle.getX2() - rectangle.getX1()));
        int viewHeight = (int) (DETECT_BOX_SIZE_SCALE * rootViewHeight * (rectangle.getY2() - rectangle.getY1()));

        mBindingPair.updateView(new Function1<ActivityMainBinding, Unit>() {
            @Override
            public Unit invoke(ActivityMainBinding activityMainBinding) {
                View detectView = activityMainBinding.detectBox;
                TextView detectText = activityMainBinding.detectText;
                detectView.setVisibility(View.VISIBLE);
                detectText.setVisibility(View.VISIBLE);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.width = viewWidth; // 设置宽度
                params.height = viewHeight; // 设置高度
                detectView.setLayoutParams(params);

                // 修改按钮位置
                detectView.setX(centerX); // 设置X坐标位置
                detectView.setY(centerY); // 设置Y坐标位置

                detectText.setX(centerX);
                detectText.setY(centerY);
                detectText.setText(renderData.getCategory().categoryName());

                return null;
            }
        });
    }
}

