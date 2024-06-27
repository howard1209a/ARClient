package com.narc.arclient;

import android.app.Application;

import com.rayneo.arsdk.android.MercurySDK;

public class ARClientApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MercurySDK.INSTANCE.init(this);
    }
}
