package com.yiyou.gamesdk.container;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.mobilegamebar.rsdk.outer.util.Log;
import com.mobilegamebar.rsdk.outer.util.ResourceHelper;
import com.yiyou.gamesdk.PluginManager;
import com.yiyou.gamesdk.core.api.ApiFacade;

/**
 * @author Luoweiqiang
 * @version V1.0
 * @date 2015年5月18日 上午11:16:30
 */
public class FloatService extends Service {

    private Handler handler;

    private static final String TAG = "RSDK:FloatService ";

    @Override
    public IBinder onBind(Intent intent) {

        PluginManager pluginManager = PluginManager.getInstance();
        ResourceHelper.prepare(this);
        pluginManager.setFloatService(this);
        if (pluginManager.isInit()) {
            Log.d(TAG, "callback");
            pluginManager.callBack();
        } else {
            stopSelf();
            Log.d(TAG, "service 非正常方式启动");
        }

        handler = new Handler(Looper.getMainLooper());
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, " onCreate");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");


        return Service.START_NOT_STICKY;

    }

    @Override
    public void onDestroy() {
        PluginManager.getInstance().setFloatService(null);
        ResourceHelper.uninit();
        super.onDestroy();
        Log.d(TAG, " onDestroy");
        handler.removeCallbacksAndMessages(null);
        handler = null;
    }

    @Override
    public ClassLoader getClassLoader() {
        ClassLoader classLoader = PluginManager.getInstance().getClassLoader();
        Log.d(TAG, "classloader" + classLoader);
        return classLoader == null ? super.getClassLoader() : classLoader;
    }

    @Override
    public AssetManager getAssets() {
        return PluginManager.getInstance().getAssetManager();
    }

    @Override
    public Resources getResources() {
        return PluginManager.getInstance().getResources();
    }

    @Override
    public Theme getTheme() {
        return PluginManager.getInstance().getTheme();
    }

    public void startCountDown() {
        if (handler != null) {
            handler.postDelayed(runnable, 60000 * 10);
        }
    }

    public void stopCountDown(){
        if (handler!=null){
            handler.removeCallbacks(runnable);
        }
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ApiFacade.getInstance().onLineEvent(null);
            Log.d(TAG, "run: ");
            handler.postDelayed(runnable, 60000 * 10);
        }
    };
}
