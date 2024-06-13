package com.narc.arclient.front;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.narc.arclient.databinding.ActivityMainBinding;
import com.rayneo.arsdk.android.ui.wiget.BaseMirrorContainerView;
import com.rayneo.arsdk.android.ui.wiget.MirrorContainerView;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class CenterRectangleView extends TextureView {

    public CenterRectangleView(@NonNull Context context) {
        super(context);
    }

    public CenterRectangleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CenterRectangleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}

