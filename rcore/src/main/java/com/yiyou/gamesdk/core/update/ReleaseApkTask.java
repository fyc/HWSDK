package com.yiyou.gamesdk.core.update;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.yiyou.gamesdk.core.CoreManager;
import com.mobilegamebar.rsdk.outer.RSDKSpace;
import com.mobilegamebar.rsdk.outer.model.RootDir;
import com.mobilegamebar.rsdk.outer.model.VersionDir;
import com.mobilegamebar.rsdk.outer.util.FileUtils;
import com.mobilegamebar.rsdk.outer.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by chenshuide on 2015/11/2.
 */
public class ReleaseApkTask implements Runnable {
    private static final String TAG = "RSDK: "+ DefaultUpdateImpl.TAG;
    public static final String ZIP_REGULAR_SUFFIX = "/";
    private static final String APK_SUFFIX = ".apk";


    private String version;
    private File file;
    private VersionDir versionDir;

    public ReleaseApkTask(String version, File file, VersionDir versionDir) {
        this.version = version;
        this.file = file;
        this.versionDir = versionDir;
    }

    @Override
    public void run() {
        Log.d(TAG, " release thread begin");
        Log.d(TAG, "down file succ");
        //2.校验文件完整性
        //校验一致
        Log.d(TAG, "md5 secret match");
        boolean ret = unZipApk(file, versionDir);
        Log.d(TAG, "unzip result" + ret);
        if (!ret) {
            Log.e(TAG,"unzip error");
            return;
        }


        //3.校验文件成功之后 更新配置文件 设置当前版本 newV->curVer curV->oldVer

        SharedPreferences sharePreferences = RSDKSpace.getInstance(CoreManager.getContext()).getSharePreferences();
        String cur_version = sharePreferences.getString(RSDKSpace.KEY_CUR_VERSION, "");
        Log.d(TAG, "cur_version=" + cur_version);
        Log.d(TAG, "new_version=" + version);
        sharePreferences.edit().putString(RSDKSpace.KEY_CUR_VERSION, version).commit();
        String newCur_version = sharePreferences.getString(RSDKSpace.KEY_CUR_VERSION, "");
        Log.d(TAG, "newCur_version=" + newCur_version);

        //4.删掉 老版本
        deleteOldVersion(cur_version);

    }

    /**
     * delete old veriosn
     * @param old_version version
     */
    private void deleteOldVersion(String old_version) {
        List<VersionDir> list = RootDir.getInstance(CoreManager.getContext()).listFile();
        if (list.size() == 2) {//只有两个版本,无需删除
            Log.d(TAG, "cur dir only two version");
            return;
        }

        //找到最老版本 执行删除

        if (TextUtils.isEmpty(old_version)) {
            Log.d(TAG, "old version is empty return");
            return;
        }

        Log.d(TAG, "attemp to delete old version=  " + old_version);

        VersionDir deleteVersionDir = RootDir.getInstance(CoreManager.getContext()).getVersionDir(old_version);
        if (deleteVersionDir != null) {
            boolean deleteRet = deleteVersionDir.deleteVer();
            Log.d(TAG, "delete old version=  " + old_version + "and the result is = " + deleteRet);
        }

    }

    /**
     * 解压文件 only unzip apk and delete zip after unzip
     *
     * @param zip zipfile
     * @return boolean
     */
    private boolean unZipApk(@NonNull File zip, VersionDir versionDir) {
        boolean isSucc = false;
        try {

            ZipFile zipFile = new ZipFile(zip);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();

                Log.d(TAG, zipEntry.toString());
                InputStream stream = zipFile.getInputStream(zipEntry);

                String path = zipEntry.getName();
                String[] names = path.split(ZIP_REGULAR_SUFFIX);
                String name = "";
                if (names.length >= 1) {
                    name = names[names.length - 1];
                    Log.d(TAG, name);

                    if (!name.endsWith(APK_SUFFIX))
                        continue;

                    File item = new File(zip.getParentFile(), name);
                    isSucc = FileUtils.copytoFile(stream, item);

                    //释放so
                    boolean releaseSoret = versionDir.releaseSoFile(CoreManager.getContext(), name);

                    Log.d(TAG, "releaseSoret ret " + releaseSoret);
                }
            }

            boolean ret = zip.delete();
            Log.d(TAG, "delete zip ret" + ret);

        } catch (IOException e) {
            isSucc = false;
            e.printStackTrace();
        }


        return isSucc;

    }
}
