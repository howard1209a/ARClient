package com.narc.arclient.front;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.narc.arclient.databinding.ActivityMainBinding;
import com.narc.arclient.databinding.WidgetTitleLayoutBinding;
import com.rayneo.arsdk.android.ui.wiget.BaseMirrorContainerView;
import com.rayneo.arsdk.android.ui.wiget.MirrorContainerView;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class CenterRectangleView extends BaseMirrorContainerView<ActivityMainBinding> {
    private Paint paint;
    private Rect rect;

    public CenterRectangleView(Context context) {
        super(context);
    }

    public CenterRectangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CenterRectangleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onInit() {
        paint = new Paint();
        paint.setColor(0xFFFFFFFF); // 设置矩形颜色
        paint.setStyle(Paint.Style.STROKE); // 仅描边，不填充
        paint.setStrokeWidth(5); // 描边宽度

        mBindingPair.updateView(new Function1<ActivityMainBinding, Unit>() {
            @Override
            public Unit invoke(ActivityMainBinding activityMainBinding) {
                activityMainBinding.tvTitle2.postInvalidate();
                return null;
            }
        });

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 获取 View 的宽度和高度
        int width = getWidth();
        int height = getHeight();

        // 矩形的尺寸
        int rectWidth = 200;
        int rectHeight = 100;

        // 计算矩形的位置，使其位于画面中心
        int left = (width - rectWidth) / 2;
        int top = (height - rectHeight) / 2;
        int right = left + rectWidth;
        int bottom = top + rectHeight;

        // 创建矩形对象
        rect = new Rect(left, top, right, bottom);

        // 绘制矩形
        canvas.drawRect(rect, paint);
    }
}

