package com.narc.arclient.front;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import com.narc.arclient.databinding.ActivityMainBinding;
import com.rayneo.arsdk.android.ui.wiget.BaseMirrorContainerView;
import com.rayneo.arsdk.android.ui.wiget.MirrorContainerView;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ViewHandler extends BaseMirrorContainerView<ActivityMainBinding> {
    public ViewHandler(Context context) {
        super(context);
    }

    public ViewHandler(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewHandler(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onInit() {
        mBindingPair.updateView(new Function1<ActivityMainBinding, Unit>() {
            @Override
            public Unit invoke(ActivityMainBinding activityMainBinding) {
//                TextView textView = activityMainBinding.tvTitle1;
//                textView.setText("hhh");
                return null;
            }
        });
    }

//    public void drawRec() {
//        mBindingPair.updateView(new Function1<ActivityMainBinding, Unit>() {
//            @Override
//            public Unit invoke(ActivityMainBinding activityMainBinding) {
//                Canvas canvas = activityMainBinding.centerRectangleView.lockCanvas();
//                if (canvas != null) {
//                    Paint paint = new Paint();
//                    paint.setColor(0xFFFFFFFF); // 设置矩形颜色
//                    paint.setStyle(Paint.Style.STROKE); // 仅描边，不填充
//                    paint.setStrokeWidth(5); // 描边宽度
//
//                    // 获取 View 的宽度和高度
//                    int width = getWidth();
//                    int height = getHeight();
//
//                    // 矩形的尺寸
//                    int rectWidth = 200;
//                    int rectHeight = 100;
//
//                    // 计算矩形的位置，使其位于画面中心
//                    int left = (width - rectWidth) / 2;
//                    int top = (height - rectHeight) / 2;
//                    int right = left + rectWidth;
//                    int bottom = top + rectHeight;
//
//                    // 创建矩形对象
//                    Rect rect = new Rect(left, top, right, bottom);
//
//                    // 绘制矩形
//                    canvas.drawRect(rect, paint);
//
//                    activityMainBinding.centerRectangleView.unlockCanvasAndPost(canvas);
//                }
//                return null;
//            }
//        });
//    }
}
