package com.narc.arclient;

import android.app.Application;
import com.rayneo.arsdk.android.MercurySDK;
public class demoApplication extends Application {
        //初始化MercurySDK
        @Override
        public void onCreate() {
            super.onCreate();
            MercurySDK.INSTANCE.init(this);
            //java 和 Kotlin 的区别，INSTANCE体现单例

        }

}
