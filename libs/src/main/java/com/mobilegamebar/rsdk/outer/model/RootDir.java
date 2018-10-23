package com.mobilegamebar.rsdk.outer.model;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by chenshuide on 15/8/4.
 */
public class RootDir extends File {
    private static final String ROOT_PATH_FOR_R = "haowansdk";
    List<VersionDir> list = new ArrayList<>();

    private static RootDir instance;


    private RootDir(Context context) {
        super(context.getApplicationInfo().dataDir, ROOT_PATH_FOR_R);//dataDir:/data/data/packageName/
    }

    public static RootDir getInstance(Context context) {
        if (instance == null)
            instance = new RootDir(context);

        return instance;
    }

    public List<VersionDir> listFile() {

        list.clear();

        File[] files = listFiles();

        if (files == null || files.length == 0)
            return list;

        Arrays.sort(files);

        for (File file : files) {

            VersionDir versionDir = new VersionDir(this, file.getName());

            list.add(versionDir);

        }


        return list;


    }


    public VersionDir getVersionDir(@NonNull String version) {
        for (VersionDir versionDir : list) {
            if (version.equals(versionDir.getVersion())) {
                return versionDir;
            }
        }

        return null;
    }


}
