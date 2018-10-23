package com.mobilegamebar.rsdk.outer.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import com.mobilegamebar.rsdk.outer.RSDKSpace;
import com.mobilegamebar.rsdk.outer.TempLibHelper;
import com.mobilegamebar.rsdk.outer.util.ApkInfoUtil;
import com.mobilegamebar.rsdk.outer.util.FileUtils;
import com.mobilegamebar.rsdk.outer.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by chenshuide on 15/8/4.
 */
public class VersionDir extends File implements IApkInfo {

    protected static final String END_SUFFIX = ".apk";
    protected static final String TEMP_SUFFIX = "temp";
    /**
     * dexopt优化使用的目录
     */
    protected static final String ODEX_PATH = "dexopt";
    /**
     * dexclassloader lib使用的目录
     */
    protected static final String LIB_PATH = "lib";
    /**
     * 释放apk目录
     */
    public static final String APK_PATH = "apk";
    /**
     * app name
     */
    protected static final String TAG = "RSDK: "+"VersionDir";
    public static final String PLUGIN_APK_NAME = "rplugin.apk";
    protected static final String ASSETS_APK_NAME = "apk/rplugin.apk";
    protected String mVersion;
    protected File resourceDir;
    public void setResourceDir(File resourceDir) {
        this.resourceDir = resourceDir;
    }

    public VersionDir(@NonNull File rootPath, @NonNull String version) {
        super(rootPath, version);
        mVersion = version;
        if (!exists())
            mkdirs();

    }


    public File getVersionPath() {
        return this;
    }


    public boolean copyApk2Path(Context context, InputStream inputStream, String apkName) {
        boolean isSucc = false;
        try {
            File apkFile = new File(getApkDir(), apkName);
            if (!apkFile.exists())
                apkFile.createNewFile();

            isSucc = FileUtils.copytoFile(inputStream, apkFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        releaseSoFile(context, apkName);
        return isSucc;
    }

    public boolean copyApk4Assets(@NonNull Context context){ //copy apk from assets
        boolean isSucc = false;
        try {
            Log.d(TAG, "copy apk start");
            AssetManager assets = context.getAssets();

            InputStream stream = assets.open(ASSETS_APK_NAME);
            isSucc  = FileUtils.copytoFile(stream, getApk());
            Log.d(TAG, "copy apk complete" + isSucc);
        } catch (Exception e) {
            isSucc = false;
            e.printStackTrace();
        }

        return isSucc;

    }

    public boolean copyApk2Path4Assets(Context context) {
        boolean isSucc = false;
        try {

            AssetManager assets = context.getAssets();
            String[] listFileName = assets.list(RSDKSpace.ASSETS_APK);
            if (listFileName.length == 0)
                return false;

            Log.d(TAG, Arrays.toString(listFileName));

            for (String fileName : listFileName) {

                if (!fileName.endsWith(END_SUFFIX))//only copy apk
                    continue;

                InputStream inputStream = context.getAssets().open(RSDKSpace.ASSETS_APK_DIR + fileName);
                isSucc = copyApk2Path(context, inputStream, fileName);
                if (!isSucc) {
                    Log.d(TAG, "copy fail " + isSucc + "filename " + fileName);
                    break;
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return isSucc;
    }


    public boolean copyApk2Path(Context context, byte[] data, String apkName) {
        boolean isSucc = false;
        try {
            File apkFile = new File(getApkDir(), apkName);
            if (!apkFile.exists())
                apkFile.createNewFile();

            isSucc = FileUtils.copyByte2File(data, apkFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        releaseSoFile(context, apkName);
        return isSucc;
    }


    public boolean releaseSoFile(Context context, String apkName) {

        String cpu_architect = ApkInfoUtil.chooseByX86andArm(context);
        Log.d(TAG, "cpu_architect" + cpu_architect);

        boolean isSucc = true;
        File apkFile = new File(getApkDir(), apkName);

        try {
            ZipFile zipFile = new ZipFile(apkFile);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) entries.nextElement();
                if (zipEntry.isDirectory())
                    continue;

                String zipEntryName = zipEntry.getName();

                if (zipEntryName.endsWith(".so") && zipEntryName.contains(cpu_architect)) {
                    String[] entryNames = zipEntryName.split("/");

                    if (entryNames.length > 2) {
                        Log.d(TAG, "zipEntryName" + zipEntryName);

                        Log.d(TAG, Arrays.toString(entryNames));

                        if (entryNames[1].equals(cpu_architect)) {
                            Log.d(TAG, "cpu_architect" + cpu_architect);
                            isSucc = releaseSoFile(zipFile, zipEntry);
                            Log.d(TAG, "copy so " + isSucc);
                        }

                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isSucc;

    }


    protected boolean releaseSoFile(ZipFile zipFile, ZipEntry zipEntry) throws IOException {
        InputStream inputStream = zipFile.getInputStream(zipEntry);
        File newSoFile = new File(getLibDir(), parseSoFileName(zipEntry.getName()));
        return FileUtils.copytoFile(inputStream, newSoFile);
    }

    protected String parseSoFileName(String zipEntryName) {
        return zipEntryName.substring(zipEntryName.lastIndexOf("/") + 1);
    }


    @Override
    public File getApk() {
        File apkFile = new File(getApkDir(), PLUGIN_APK_NAME);
        return apkFile;
    }

    @Override
    public File getApkDir() {
        File apkPath = new File(getVersionPath(), APK_PATH);
        if (!apkPath.exists())
            apkPath.mkdirs();
        return apkPath;
    }

    @Override
    public File getResourceDir() {
        Log.d(TAG, "getResourceDir" + resourceDir);
        return resourceDir == null ? getApk() : resourceDir;
    }

    @Override
    public File getOdexDir() {
        File odexPath = new File(getVersionPath(), ODEX_PATH);
        if (!odexPath.exists())
            odexPath.mkdirs();
        return odexPath;
    }

    @Override
    public File getLibDir() {
        File libPath = new File(getVersionPath(), LIB_PATH);
        if (!libPath.exists())
            libPath.mkdirs();
        return libPath;
    }


    public String getApkName() {
        return PLUGIN_APK_NAME;
    }

    @Override
    public File getLibTemp() {
        Log.d(TAG, "getLibTemp" + TempLibHelper.getInstance(this).getLib_temp());
        File libPath = new File(getVersionPath(), TempLibHelper.getInstance(this).getLib_temp());
        if (!libPath.exists())
            libPath.mkdirs();
        return libPath;
    }

    public String getVersion() {
        return mVersion;

    }

    public boolean deleteVer() {
        return FileUtils.deleteDir(this);
    }

    public boolean deleteLibTemp() {
        boolean isSucc = true;
        File[] files = listFiles();
        if (files == null || files.length == 0){
            return isSucc;
        }
        for (File file : files) {
            if (file.getName().contains(TEMP_SUFFIX)) {
                boolean ret = FileUtils.deleteDir(file);
                Log.d(TAG, "delete temp " + ret);
                if (!ret)
                    isSucc = false;

            }
        }


        return isSucc;
    }

    @Override
    public String toString() {
        return "VersionDir{" +
                "mVersion='" + mVersion + '\'' +
                '}';
    }
}
