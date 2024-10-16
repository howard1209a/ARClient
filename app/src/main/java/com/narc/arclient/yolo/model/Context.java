package com.narc.arclient.yolo.model;

import android.graphics.Bitmap;

import org.pytorch.IValue;
import org.pytorch.Tensor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Context {
    public volatile int taskID;
    public volatile String imgName;
    public volatile int localComputeCount;
    public volatile Bitmap originBitmap;
    public volatile Tensor inputTensor;
    public volatile List<IValue> middleTensorList = Collections.synchronizedList(new ArrayList<>());
    public volatile Tensor outputTensor;
    public volatile Bitmap finalBitmap;
    public volatile OutcomeAssessment outcomeAssessment = new OutcomeAssessment();
}
