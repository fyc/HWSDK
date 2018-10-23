package com.mobilegamebar.rsdk.outer;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import com.mobilegamebar.rsdk.outer.model.IApkInfo;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.mobilegamebar.rsdk.outer.util.ReflectionUtils;

import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * 增加context能获取插件资源的功能
 * Created by chenshuide on 15/6/2.
 */
public class ContextWrapper {
    private static final String TAG = "RSDK: "+"ContextWrapper";
    private IApkInfo mApkinfo;
    private Context mContext;

    private ClassLoader classLoader;
    private AssetManager assetManager;
    private Resources resources;
    private Resources.Theme theme;

    public ContextWrapper(Context context, IApkInfo apkInfo) {
        this.mApkinfo = apkInfo;
        this.mContext = context;
    }

    public void init() {
        Log.d(TAG, "init" + android.os.Process.myPid());

        classLoader = new DexClassLoader(mApkinfo.getApk().getAbsolutePath(),
                mApkinfo.getOdexDir().getAbsolutePath(), mApkinfo.getLibTemp().getAbsolutePath(),
                ContextWrapper.class.getClassLoader());

        chanageSystemClassLoader();

        assetManager = createAssetManager();
        resources = createResources();
        Log.d(TAG, "init end");


    }

    private void chanageSystemClassLoader() {
        try {
            Object mPackageInfo = ReflectionUtils.getFieldValue(mContext, "mBase.mPackageInfo", true);

            ReflectionUtils.setFieldValue(mPackageInfo, "mClassLoader", classLoader, true);
            Log.d(TAG, "update application classloader succ");

        } catch (Exception e) {
            throw new RuntimeException("can not chanage application classloader " + e.getMessage());
        }


    }


    private AssetManager createAssetManager() {
        AssetManager assetManager = null;
        try {
            assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, mApkinfo.getResourceDir().getAbsolutePath());
            return assetManager;
        } catch (Exception e) {
            Log.e(TAG, " createAssetManager Exception:", e);
            return assetManager;
        }

    }

    private Resources createResources() {
        if (assetManager == null) {
            Log.e(TAG, " loadPlugin assetManager is null");
            return null;
        }
        Resources superRes = mContext.getResources();

//        superRes.getConfiguration().orientation = PluginManager.getInstance().getOrientation();
        Resources resources = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
        theme = resources.newTheme();
        theme.setTo(mContext.getTheme());

        /**
         * 针对部分版本三星手机 加载xml出异常问题
         * 替换Resource之后 由于没有默认的主题 导致找不到默认的
         * 布局文件或其它资源文件
         *
         */
        int defaultTheme = 0;
        if (Build.VERSION.SDK_INT >= 14) {
            defaultTheme = android.R.style.Theme_DeviceDefault;
        } else {
            defaultTheme = android.R.style.Theme;
        }
        try {
            theme.applyStyle(defaultTheme, true);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return resources;
    }

    public void clean() {
        classLoader = null;
        theme = null;
        resources.flushLayoutCache();
        resources = null;
        if (assetManager != null) {
            assetManager.close();
            assetManager = null;
        }

        Log.d(TAG, "clean ok");
    }


    public ClassLoader getClassLoader() {

        return classLoader == null ? mContext.getClassLoader() : classLoader;
    }

    public AssetManager getAssetManager() {
        return assetManager== null ? mContext.getAssets(): assetManager;
    }

    public Resources getResources() {
        return resources == null ? mContext.getResources() : resources;
    }

    public Resources.Theme getTheme() {
        return theme == null ? mContext.getTheme() : theme;
    }

    public void updateOrientation(int orientation) {
        Configuration configuration = getResources().getConfiguration();
        configuration.orientation = orientation;
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
    }

}
