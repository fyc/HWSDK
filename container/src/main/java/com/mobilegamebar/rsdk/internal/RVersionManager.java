package com.mobilegamebar.rsdk.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.mobilegamebar.rsdk.outer.RSDKSpace;
import com.mobilegamebar.rsdk.outer.model.RootDir;
import com.mobilegamebar.rsdk.outer.model.VersionDir;
import com.mobilegamebar.rsdk.outer.util.Log;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by Win on 2017/4/13.
 */
public class RVersionManager {
    private static final String TAG = "RSDK:RVersionManager";
    private static final String CORE_VERSION = "core_version";
    private static final String SDK_VERSION = "sdk_version";
    private VersionDir versionPath;

    public RVersionManager(Context context, @NonNull RootDir rootDir) {
        checkNeedPackageUpdate(context, rootDir);

        List<VersionDir> files = rootDir.listFile();

        // 删除temp文件 这里主要预防 进程异常关闭时候 没有删除临时文件
        for (VersionDir file : files) {
            boolean ret = file.deleteLibTemp();
            Log.d(TAG, "delete temp lib"+ret);
        }

        if ( files.size() == 0) {
            firstInstall(context, rootDir);

        } else if (files.size() == 1) {
            Log.d(TAG, "load history");
            versionPath = files.get(0);
        } else {
            switchVersion(context, files);
        }
    }

    private void checkNeedPackageUpdate(Context context, @NonNull RootDir rootDir) {
        List<VersionDir> files = rootDir.listFile();

        String pre_sdk_version = RSDKSpace.getInstance(context).getSharePreferences().getString(RSDKSpace.KEY_SDK_VERSION, "");
        Map<String, String> versions = RSDKSpace.getInstance(context).parseConfig();
        String assets_sdk_version = versions.get(SDK_VERSION);
        if (!pre_sdk_version.equals(assets_sdk_version)) {
            //整包更新
            Log.d(TAG, "begin package update ");

            for (VersionDir file : files) {
                boolean ret = file.deleteVer();
                Log.d(TAG, "package update and delete the old core " + file.getVersion() + " and the ret " + ret);

            }
            Log.d(TAG, "end package update ");

        }
    }

    /**
     * 第一次安装
     *
     * @param context context
     */
    private void firstInstall(Context context, File rootFile) {
        Log.d(TAG, "install");
        //第一次安装
        Map<String, String> versions = RSDKSpace.getInstance(context).parseConfig();

        String core_version = versions.get(CORE_VERSION);
        String sdk_version = versions.get(SDK_VERSION);

        Log.d(TAG, "core_version=" + core_version);
        Log.d(TAG, "sdk_version=" + sdk_version);


        if (TextUtils.isEmpty(core_version)) {
            core_version = "01";
        }

        VersionDir versionDir = new VersionDir(rootFile, core_version);
        boolean copyapkRet = versionDir.copyApk2Path4Assets(context);

        versionPath = versionDir;

        setCurVersion(context, core_version);
        RSDKSpace.getInstance(context).getSharePreferences().edit().putString(RSDKSpace.KEY_FIRST_VERSION,core_version).commit();
        RSDKSpace.getInstance(context).getSharePreferences().edit().putString(RSDKSpace.KEY_SDK_VERSION, sdk_version).commit();
    }

    /**
     * 根据配置选择一个版本
     *
     * @param context context
     * @param files   版本文件
     */
    private void switchVersion(Context context, List<VersionDir> files) {

        SharedPreferences sharePreferences = RSDKSpace.getInstance(context).getSharePreferences();
        String current_ver = sharePreferences.getString(RSDKSpace.KEY_CUR_VERSION, "");
        Log.d(TAG, "current core version: " + current_ver);
        if (TextUtils.isEmpty(current_ver)) {
            loadFirstVersion(files);
            return;
        }

        for (VersionDir file : files) {
            if (file.getName().contains(current_ver)) {
                versionPath = file;
                Log.d(TAG, "load new_version succ and the version is = " + current_ver);
                return;
            }
        }

        Log.d(TAG, "load new_version fail attemp to load first version");
        loadFirstVersion(files);

    }


    private void loadFirstVersion(List<VersionDir> files) {
        versionPath = files.get(0);

        Log.d(TAG, "load new_version fail attemp to load last version and the version is=" + versionPath.getVersion());

    }


    /**
     * 把当前版本写到配置
     *
     * @param context context
     * @param version cur_version
     */
    private void setCurVersion(Context context, String version) {
        RSDKSpace.getInstance(context).getSharePreferences().edit().putString(RSDKSpace.KEY_CUR_VERSION, version).commit();
    }


    public VersionDir getVersionPath() {
        return versionPath;
    }
}
