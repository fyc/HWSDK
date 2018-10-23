package com.yiyou.gamesdk;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.IBinder;

import com.mobilegamebar.rsdk.outer.ContextWrapper;
import com.mobilegamebar.rsdk.outer.ICoreManager;
import com.mobilegamebar.rsdk.outer.IOperateCallback;
import com.mobilegamebar.rsdk.outer.TempLibHelper;
import com.mobilegamebar.rsdk.outer.consts.TTCodeDef;
import com.mobilegamebar.rsdk.outer.event.EventDispatcherAgent;
import com.mobilegamebar.rsdk.outer.event.IEventListener;
import com.mobilegamebar.rsdk.outer.event.StartActivityEvent;
import com.mobilegamebar.rsdk.outer.model.GameParamInfo;
import com.mobilegamebar.rsdk.outer.model.VersionDir;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.yiyou.gamesdk.container.FloatService;
import com.yiyou.gamesdk.container.MainActivity;
import com.yiyou.gamesdk.core.CoreManager;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;


public class PluginManager {
    private static PluginManager sInstance = null;
    private static final String TAG = "RSDK:PluginManager";
    private FloatService floatService;
    private Map<String, ContextWrapper> plugins = new HashMap<>();
    private boolean isDebug = false;
    private GameParamInfo initGameParamInfo;
    private IOperateCallback<String> initCallback,logoutCallback;
    private ICoreManager coreManager;
    private WeakReference wr;
    private VersionDir mVersionDir;
    private boolean isInit;
    private int mOrientation;
    private IEventListener startActivityListener;
    private Context mContext;
    private Context appContext;

    public void attach(ContextWrapper contextWrapper ,VersionDir versionDir) {
        mVersionDir = versionDir;
        plugins.put(mVersionDir.getApkName(), contextWrapper);
    }


    public synchronized static PluginManager getInstance() {
        if (sInstance == null) {
            sInstance = new PluginManager();
        }
        return sInstance;
    }

    private PluginManager() {
    }

    public boolean isInit() {
        return isInit;
    }

    public void init(Context context, boolean debugMode, GameParamInfo info, int orientation, IOperateCallback<String> callback) {
        mOrientation = orientation;
        mContext = context;
        appContext = context.getApplicationContext();
        isDebug = debugMode;
        initGameParamInfo = info;
        initCallback = callback;
        if (wr == null) {
            wr = new WeakReference((Activity) context);
        }
        isInit = true;
        startFloatService(context);
        addEvent();

    }

    public void callBack() {
        if (coreManager != null) {
            initCallback.onResult(TTCodeDef.SUCCESS, "sdk already init");
            return;
        }
        coreManager = new CoreManager();
        coreManager.init(getFloatService(), isDebug, initGameParamInfo, mOrientation, initCallback, (Activity) wr.get());
    }

    public String getSession() {
        if (coreManager != null) {
            return coreManager.getSession();
        }
        return "";
    }

    public long getUid() {
        if (coreManager != null) {
            return coreManager.getUid();
        }
        return 0;

    }

    public int getGameId() {
        if (coreManager != null) {
            return coreManager.getGameId();
        }
        return 0;
    }


    public void uninit() {
        isInit = false;
        coreManager = null;
        stopFloatService();
        for (String pluginId : plugins.keySet()) {

            plugins.get(pluginId).clean();
        }
        plugins.clear();
        boolean ret = mVersionDir.deleteLibTemp();
        Log.d(TAG, "deleteLibTemp" + ret);
        TempLibHelper.getInstance(mVersionDir).uninit();
        mVersionDir = null;
        sInstance = null;

    }

    /**
     * @param context
     */
    private void startFloatService(Context context) {
        Log.d(TAG, "begin start floatservice");
        appContext.bindService(new Intent(context, FloatService.class), conn, Context.BIND_AUTO_CREATE);
    }


    private void stopFloatService() {
        appContext.unbindService(conn);
        if (floatService != null) {
            floatService.stopSelf();
            floatService = null;
        }
    }


    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    public FloatService getFloatService() {
        return floatService;
    }

    public void setFloatService(FloatService floatService) {
        this.floatService = floatService;
    }

    public int getOrientation() {
        return mOrientation;
    }

    public ClassLoader getClassLoader() {
        return isInit ? plugins.get(VersionDir.PLUGIN_APK_NAME).getClassLoader() : null;
    }

    public AssetManager getAssetManager() {
        return plugins.get(VersionDir.PLUGIN_APK_NAME).getAssetManager();
    }

    public Resources getResources() {
        return plugins.get(VersionDir.PLUGIN_APK_NAME).getResources();
    }

    public Resources.Theme getTheme() {
        return plugins.get(VersionDir.PLUGIN_APK_NAME).getTheme();
    }

    public void updateOrientation(int orientation) {
        mOrientation = orientation;
        plugins.get(VersionDir.PLUGIN_APK_NAME).updateOrientation(orientation);
    }

    public void uninit(final IOperateCallback iOperateCallback){
        if (coreManager != null) {
            coreManager.uninit(new IOperateCallback() {
                @Override
                public void onResult(int i, Object o) {
                    if (i == 0) {
                        uninit();
                    }
                    iOperateCallback.onResult(i, o);
                }
            });
        }
    }

    public IOperateCallback<String> getLogoutCallback(){
        return logoutCallback;
    }

    public void setLogoutCallback(IOperateCallback<String> callback){
        logoutCallback = callback;
    }

    public void showFloatView(){
        if (coreManager != null){
            coreManager.showFloatView();
        }
    }

    public void hideFloatview(){
        if (coreManager != null){
            coreManager.hideFloatView();
        }
    }

    private void addEvent() {
        startActivityListener = new IEventListener<StartActivityEvent.FragmentParam>() {

            @Override
            public void onEvent(String eventType, StartActivityEvent.FragmentParam params) {


                Intent intent = new Intent(params.data, MainActivity.class);
                intent.putExtra(MainActivity.EXTRA_FRAGMENT_NAME, params.fragmentName);
                intent.putExtra(MainActivity.EXTRA_DISPLAY_TYPE, params.displayType);
                if (params.mBundle != null) {
                    intent.putExtra(MainActivity.EXTRA_FRAGMENT_BUNDLE, params.mBundle);
                }
                //优先使用游戏activity跳转. 不然不同task 无法实现支付页面弹窗化
                if (params.startUpActivity != null && !params.startUpActivity.isFinishing()) {
                    Log.d(TAG, "use start up activity as root.");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    params.startUpActivity.startActivity(intent);
                    params.startUpActivity.overridePendingTransition(0, 0);
                    return;
                }

                //其次使用service context跳转
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (getFloatService() != null) {
                    getFloatService().startActivity(intent);
                }
            }
        };
        EventDispatcherAgent.defaultAgent()
                .addEventListener(this, StartActivityEvent.TYPE_START_ACTIVITY_ENVENT, startActivityListener);
    }

}
