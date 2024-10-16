package com.narc.arclient.yolo.biz;

import com.narc.arclient.yolo.model.Context;

public interface Handler {
    Context handle(Context s);
}
