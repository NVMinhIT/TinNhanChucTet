package com.tinnhantet.nhantin.hengio;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.zer.android.newsdk.ZAndroidSDK;

public class MyApplication extends Application {

    private static MyApplication sInstance;

    public static MyApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        ZAndroidSDK.initApplication(this, getApplicationContext().getPackageName());
//        CalligraphyConfig.initDefault(
//                new CalligraphyConfig.Builder()
//                        .setDefaultFontPath("fonts/font_app.ttf")
//                        .setFontAttrId(R.attr.fontPath)
//                        .build()
//        );
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
