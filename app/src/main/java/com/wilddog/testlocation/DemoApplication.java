package com.wilddog.testlocation;

import android.app.Application;

import com.wilddog.client.Logger;
import com.wilddog.client.WilddogSync;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;

/**
 * Created by he on 2017/6/26.
 */

public class DemoApplication extends Application {
    private String mAppId="location123";
    static  Application application;
    @Override
    public void onCreate() {
        super.onCreate();
        application=this;

    }

    public static Application getApplicationInstance(){
        return  application;
    }
}
