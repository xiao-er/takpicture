package com.xiaoxiao.takepicture;

import android.content.Context;

/**
 * Created by Raiden on 2017/6/6.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static CrashHandler instance;
    Context context;

    public static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    public void init(Context ctx) {
        context = ctx;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {

        StackTraceElement[] stackTrace = e.getStackTrace();
        StringBuilder sb = new StringBuilder();
//            sb.append("info:").append(Tools.getClientInfo(context)).append("\r\n");
//            PackageInfo packageInfo = Tools.getPackageInfo(context);
//            sb.append("version:").append(packageInfo.versionName).append("(").append(patckageInfo.versionCode).append(")");
        sb.append("message:").append(e.getMessage()).append("\r\n");
        for (int i = 0; i < stackTrace.length; i++) {
            sb.append("file:").append(stackTrace[i].getFileName())
                    .append(" class:").append(stackTrace[i].getClassName())
                    .append(" method:").append(stackTrace[i].getMethodName())
                    .append(" line:").append(stackTrace[i].getLineNumber())
                    .append("</br>\r\n");
        }
        sb.toString();
    }
}
