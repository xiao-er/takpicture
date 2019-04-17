package com.xiaoxiao.takepicture;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * @author: 潇潇
 * @create on:  2019/4/17
 * @describe:DOTO
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(this);
        // 在加载图片之前，你必须初始化Fresco类
        Fresco.initialize(this);
    }
}
