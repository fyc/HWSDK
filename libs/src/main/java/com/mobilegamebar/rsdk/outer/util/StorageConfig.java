package com.mobilegamebar.rsdk.outer.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by levyyoung on 15/5/14.
 */
public class StorageConfig {

    private static final String TAG = "RSDK:StorageConfig ";
    private static final Object lock = new Object();
    private static boolean isDebug = false;

    //PATHS FIELD BEGIN
    private static String SDKRootDirPath;

    private static final String TAG_DEBUG_ROOT_DIR = "HaoWanRGameSDK";
    private static final String TAG_PUBLIC = "public";
    private static final String TAG_ROOT_DIR = "TaoZiGameSDK";
    private static final String TAG_DB = "db";
    private static final String TAG_LOG = "logs";
    private static final String TAG_CACHE = "cache";
    private static final String TAG_CHANNEL = "Rchannel";
    //PATHS FIELD END

    public static void prepare(Context context, boolean debug) {
        synchronized (lock) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                SDKRootDirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            } else {
                SDKRootDirPath = context.getApplicationContext().getCacheDir().getAbsolutePath();
                Log.w(TAG, "WARNING! External storage unmounted. use application storage instead.");
            }
            isDebug = debug;
            SDKRootDirPath = SDKRootDirPath + File.separator + (isDebug ? TAG_DEBUG_ROOT_DIR : TAG_ROOT_DIR);
        }
    }

    public static String getPublicDirPath() {
        String path = SDKRootDirPath + File.separator + TAG_PUBLIC;
        createDirIfNeed(path);
        return path;
    }

    public static String getSDKPublicChannelPath(){
        String path = SDKRootDirPath + File.separator + TAG_PUBLIC + File.separator + TAG_CHANNEL;
        createDirIfNeed(path);
        return path;
    }

    public static String getPublicDBDirPath() {
        String path = SDKRootDirPath + File.separator + TAG_PUBLIC + File.separator + TAG_DB;
        createDirIfNeed(path);
        return path;
    }

    public static String getPublicCacheDirPath() {
        String path = SDKRootDirPath + File.separator + TAG_PUBLIC + File.separator + TAG_CACHE;
        createDirIfNeed(path);
        return path;
    }

    public static String getGameDirPath(long gameId) {
        String path = SDKRootDirPath + File.separator + gameId;
        createDirIfNeed(path);
        return path;
    }

    public static String getGameLogDirPath(long gameId) {
        String path = SDKRootDirPath + File.separator + gameId + File.separator + TAG_LOG;
        createDirIfNeed(path);
        return path;
    }

    private static void createDirIfNeed(String absolutePath) {
        File file = new File(absolutePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static final class DatabaseConfig{

        public DatabaseConfig() {
        }

        public DatabaseConfig(String databaseName, String databaseStoragePath) {
            this.databaseName = databaseName;
            this.databaseStoragePath = databaseStoragePath;
        }

        public String databaseName;
        public String databaseStoragePath;
    }

}
