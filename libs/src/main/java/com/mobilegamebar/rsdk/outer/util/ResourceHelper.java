package com.mobilegamebar.rsdk.outer.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Set;

/**
 * Created by levyyoung on 14/7/28.
 */
public class ResourceHelper {
    private static final String TAG = "RSDK: "+"ResourceHelper";

    private static Context sContextHolder = null;

    public synchronized static void prepare(Context context) {
        if (sContextHolder == null && context != null) {
            sContextHolder = context;
        }
    }

    public static void uninit() {
        sContextHolder = null;
    }

    public static WeakReference<Context> getContextHolder() {
        if (sContextHolder != null) {
            return new WeakReference<Context>(sContextHolder);
        }
        return null;
    }

    public static String getPackageName() {
        if (sContextHolder != null) {
            return sContextHolder.getPackageName();
        }
        return null;
    }

    public static PackageManager getPackageManager() {
        if (sContextHolder != null) {
            return sContextHolder.getPackageManager();
        }
        return null;
    }

    public static String getStringFromAssets(String fileName, String encodeing) {
        int bufferSize = 4096;
        try {
            InputStream is = openAssetsFile(fileName);
            if (is == null) {
                return null;
            }
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] data = new byte[bufferSize];
            int count;
            while ((count = is.read(data, 0, bufferSize)) != -1)
                outStream.write(data, 0, count);
            return new String(outStream.toByteArray(), encodeing);
        } catch (Exception e) {
            return null;
        }
    }

    public static InputStream openAssetsFile(String fileName, int accessMode) throws IOException {
        Context context = sContextHolder;
        if (context != null) {
            return context.getResources().getAssets().open(fileName, accessMode);
        }
        return null;
    }

    public static InputStream openAssetsFile(String fileName) throws IOException {
        Context context = sContextHolder;
        if (context != null) {
            return context.getResources().getAssets().open(fileName);
        }
        return null;
    }

    public static CharSequence[] getTextArray(int id) {
        Context context = sContextHolder;
        if (context != null) {
            return context.getResources().getTextArray(id);
        }
        return null;
    }

    public static CharSequence gerText(int id, CharSequence def) {
        Context context = sContextHolder;
        if (context != null) {
            return context.getResources().getText(id, def);
        }
        return null;
    }

    public static CharSequence gerText(int id) {
        Context context = sContextHolder;
        if (context != null) {
            return context.getResources().getText(id);
        }
        return null;
    }

    public static int getDimensionPixelOffset(int id) {
        Context context = sContextHolder;
        if (context != null) {
            return context.getResources().getDimensionPixelOffset(id);
        }
        Log.w(TAG, "warring:lost context, getDimensionPixelOffset will return 0.");
        return 0;
    }

    public static float getDimension(int id) {
        Context context = sContextHolder;
        if (context != null) {
            return context.getResources().getDimension(id);
        }
        Log.w(TAG, "warring:lost context, getDimension will return 0.");
        return 0;
    }

    public static boolean getBoolean(int id) {
        Context context = sContextHolder;
        if (context != null) {
            return context.getResources().getBoolean(id);
        }
        Log.w(TAG, "warring:lost context, getBoolean will return false.");
        return false;
    }

    public static DisplayMetrics getDisplayMetrics() {
        Context context = sContextHolder;
        if (context != null) {
            return context.getResources().getDisplayMetrics();
        }
        return null;
    }

    public static Configuration getConfiguration() {
        Context context = sContextHolder;
        if (context != null) {
            return context.getResources().getConfiguration();
        }
        return null;
    }

    public static ColorStateList getColorStateList(int id) {
        Context context = sContextHolder;
        if (context != null) {
            return context.getResources().getColorStateList(id);
        }
        return null;
    }

    public static int getColor(int resId) {
        Context context = sContextHolder;
        if (context != null) {
            return context.getResources().getColor(resId);
        }
        Log.w(TAG, "warring:lost context, getColor will return 0.");
        return 0;
    }

    public static Drawable getDrawable(int resId) {
        Context context = sContextHolder;
        if (context != null) {
            return context.getResources().getDrawable(resId);
        }
        return null;
    }

    public static String getString(int resId, Object... args) {
        Context context = sContextHolder;
        if (context != null) {
            return context.getString(resId, args);
        }
        return null;
    }

    public static String getString(int resId) {
        Context context = sContextHolder;
        if (context != null) {
            return context.getString(resId);
        }
        return null;
    }

    public static String[] getStringArray(int id) {
        Context context = sContextHolder;
        if (context != null) {
            return context.getResources().getStringArray(id);
        }
        return null;
    }

    public static File getExternalCacheDir() {
        Context context = sContextHolder;
        if (context != null) {
            return context.getExternalCacheDir();
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static File[] getExternalCacheDirs() {
        Context context = sContextHolder;
        if (context != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return context.getExternalCacheDirs();
            } else {
                File[] dirs = {context.getExternalCacheDir()};
                return dirs;
            }
        }
        return null;
    }

    public static File getDatabasePath(String name) {
        Context context = sContextHolder;
        if (context != null) {
            return context.getDatabasePath(name);
        }
        return null;
    }

    public static File getCacheDir() {
        Context context = sContextHolder;
        if (context != null) {
            return context.getCacheDir();
        }
        return null;
    }

    public static File getFilesDir() {
        Context context = sContextHolder;
        if (context != null) {
            return context.getFilesDir();
        }
        return null;
    }

    public static PreferencesProxy getPreferencesProxy(String name) {
        Context context = sContextHolder;
        if (context != null) {
            return new PreferencesProxy(context, name);
        }
        return null;
    }

    public static Resources getRes() {
        Context context = sContextHolder;
        if (context != null) {
            return context.getResources();
        }
        return null;
    }

    public static int getHeapSize() {
        return ((ActivityManager) sContextHolder.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass() * 1024 * 1024;
    }

    public static ContentResolver getContentResolver() {
        if (sContextHolder != null) {
            return sContextHolder.getContentResolver();
        }
        return null;
    }

    public static class PreferencesProxy {
        private SharedPreferences mPreferences;
        private boolean autocommit = true; //默认打开
        private SharedPreferences.Editor editor;

        private PreferencesProxy(Context context, String name) {
            mPreferences = context.getSharedPreferences(name, Context.MODE_MULTI_PROCESS);
        }

//        public SharedPreferences.Editor getEditor() {
//        	return mPreferences.edit();
//        }

        public boolean isAutocommit() {
            return autocommit;
        }

        //需要批量提交修改的，设置autocommit为false，并最后调用commit
        @SuppressLint("CommitPrefEdits")
        public void setAutocommit(boolean autocommit) {

            this.autocommit = autocommit;
            if (autocommit) {
                editor = null;
            } else {
                editor = mPreferences.edit();
            }
        }

        //调用此函数会在commit后将autocommit设回true
        public boolean commit() {

            autocommit = true;
            if (editor != null) {
                boolean ret = editor.commit();
                editor = null;
                return ret;
            } else {
                return true;
            }
        }

        public boolean putString(String key, String value) {

            if (autocommit) {
                return mPreferences.edit().putString(key, value).commit();
            } else {
                editor = editor.putString(key, value);
                return true;
            }
        }

        public String getString(String key) {
            return getString(key, null);
        }

        public String getString(String key, String defaultValue) {
            return mPreferences.getString(key, defaultValue);
        }

        public boolean putInt(String key, int value) {

            if (autocommit) {
                return mPreferences.edit().putInt(key, value).commit();
            } else {
                editor = editor.putInt(key, value);
                return true;
            }
        }

        public int getInt(String key) {
            return getInt(key, -1);
        }

        public int getInt(String key, int defaultValue) {
            return mPreferences.getInt(key, defaultValue);
        }

        public boolean putLong(String key, long value) {

            if (autocommit) {
                return mPreferences.edit().putLong(key, value).commit();
            } else {
                editor = editor.putLong(key, value);
                return true;
            }
        }

        public long getLong(String key) {
            return getLong(key, -1);
        }

        public long getLong(String key, long defaultValue) {
            return mPreferences.getLong(key, defaultValue);
        }

        public boolean putFloat(String key, float value) {

            if (autocommit) {
                return mPreferences.edit().putFloat(key, value).commit();
            } else {
                editor = editor.putFloat(key, value);
                return true;
            }
        }

        public float getFloat(String key) {
            return getFloat(key, -1);
        }

        public float getFloat(String key, float defaultValue) {
            return mPreferences.getFloat(key, defaultValue);
        }

        public boolean putBoolean(String key, boolean value) {

            if (autocommit) {
                return mPreferences.edit().putBoolean(key, value).commit();
            } else {
                editor = editor.putBoolean(key, value);
                return true;
            }
        }

        public boolean getBoolean(String key) {
            return getBoolean(key, false);
        }

        public boolean getBoolean(String key, boolean defaultValue) {
            return mPreferences.getBoolean(key, defaultValue);
        }

        @TargetApi(11)
        public boolean putStringSet(String key, Set<String> values) {
            if (Build.VERSION.SDK_INT >= 11) {
                if (autocommit) {
                    return mPreferences.edit().putStringSet(key, values).commit();
                } else {
                    editor = editor.putStringSet(key, values);
                    return true;
                }
            } else {
                return false;
            }
        }

        @TargetApi(11)
        public Set<String> getStringSet(String key, Set<String> defSet) {
            if (Build.VERSION.SDK_INT >= 11) {
                return mPreferences.getStringSet(key, defSet);
            } else {
                return null;
            }
        }

    }

}
