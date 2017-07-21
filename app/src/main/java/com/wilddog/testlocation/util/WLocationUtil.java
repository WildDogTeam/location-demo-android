package com.wilddog.testlocation.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.wilddog.client.Logger;
import com.wilddog.client.SyncReference;
import com.wilddog.client.WilddogSync;
import com.wilddog.location.CircleQuery;
import com.wilddog.location.WilddogLocation;
import com.wilddog.testlocation.DemoApplication;
import com.wilddog.testlocation.WelcomeActivity;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by he on 2017/7/11.
 */

public class WLocationUtil {


    public static final int LOCATION = 0;
    public static final int PATH = 1;
    public static final int CICLE = 2;
    private static WLocationUtil instance;
    private SyncReference reference;
    private String key = "47D63607-CA1D-4E96-8E75-CCA0D434B402";
    HashMap<Integer, WilddogLocation> locationmap = new HashMap();
    HashMap<Integer, CircleQuery> circlemap = new HashMap<>();

    private WLocationUtil() {

    }

    public static WLocationUtil getInstance() {
        if (instance == null)
            instance = new WLocationUtil();
        return instance;
    }

    public SyncReference getReferece() {
        return reference;
    }


    public void initWilddogApp(String mAppId) {
        String syncUrl = "https://" + mAppId + ".wilddogio.com";
        WilddogOptions.Builder builder = new WilddogOptions.Builder().setSyncUrl(syncUrl);
        WilddogOptions options = builder.build();

        WilddogApp wilddogApp = null;
        try {
            wilddogApp = WilddogApp.getInstance(mAppId);
        } catch (Exception e) {
                wilddogApp = WilddogApp.initializeApp(DemoApplication.getApplicationInstance(), options, mAppId);
        }


//        reference = WilddogSync.getInstance(wilddogApp).getReferenceFromUrl(syncUrl);
        reference=WilddogSync.getInstance(wilddogApp).getReference();

    }

    public void setCircleState(CircleQuery circleQuery, int state) {
        circlemap.put(state, circleQuery);
    }


    public void removeCircleState(int state) {
        circlemap.remove(state);
    }

    public boolean isCircleStartState(int state) {
        return circlemap.containsKey(state);
    }

    public CircleQuery getCircle(int state) {
        return circlemap.get(state);
    }

    public void setState(WilddogLocation location, int state) {
        locationmap.put(state, location);
    }

    public void removeState(int state) {
        locationmap.remove(state);
    }

    public boolean isStartState(int state) {
        return locationmap.containsKey(state);
    }

    public WilddogLocation getLocation(int state) {
        return locationmap.get(state);
    }

    public void reset() {
        for (WilddogLocation wl :
                locationmap.values()) {
            wl.stopTracingPosition(key);
            wl.stopRecordingPath(key);
        }

        for (CircleQuery cq : circlemap.values()) {
            cq.removeAllCircleQueryListeners();
        }
        locationmap.clear();
        circlemap.clear();
    }

    public void checkNetworkState(Activity activity) {
        boolean flag = false;
        //得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) DemoApplication.getApplicationInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        //去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        if (!flag) {
            setNetwork(activity);
        }
    }

    private void setNetwork(final Activity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("网络提示信息");
        builder.setMessage("网络不可用，如果继续，请先设置网络！");
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0;
            }
        }).setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                /**
                 * 判断手机系统的版本！如果API大于10 就是3.0+
                 * 因为3.0以上的版本的设置和3.0以下的设置不一样，调用的方法不同
                 */
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                } else {
                    intent = new Intent();
                    ComponentName component = new ComponentName(
                            "com.android.settings",
                            "com.android.settings.WirelessSettings");
                    intent.setComponent(component);
                    intent.setAction("android.intent.action.VIEW");
                }
                context.startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
}
