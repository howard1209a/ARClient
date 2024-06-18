package com.narc.arclient.front;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class CenteredRectangleView extends View {
    private Paint paint;
    private Rect rect;

    public CenteredRectangleView(Context context) {
        super(context);
        init();
    }

    public CenteredRectangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CenteredRectangleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(0xFF000000); // 设置矩形的颜色，这里是黑色
        paint.setStyle(Paint.Style.STROKE); // 设置画笔风格为描边
        paint.setStrokeWidth(5); // 设置描边宽度
        rect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 获取视图的宽高
        int width = getWidth();
        int height = getHeight();

        // 确定矩形框的大小（宽高的一半）
        int rectWidth = width / 2;
        int rectHeight = height / 2;

        // 计算矩形框的左、上、右、下坐标，使其位于画面中心
        int left = (width - rectWidth) / 2;
        int top = (height - rectHeight) / 2;
        int right = left + rectWidth;
        int bottom = top + rectHeight;

        // 设置矩形的位置
        rect.set(left, top, right, bottom);

        // 绘制矩形框
        canvas.drawRect(rect, paint);
    }
}

