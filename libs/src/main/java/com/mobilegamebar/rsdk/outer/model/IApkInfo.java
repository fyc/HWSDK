package com.mobilegamebar.rsdk.outer.model;

import java.io.File;

/**
 * Created by chenshuide on 15/6/2.
 */
public interface IApkInfo {
    File getApk();

    File getApkDir();

    File getResourceDir();

    File getOdexDir();

    File getLibDir();

    String getApkName();

    File getLibTemp();

//    File getPreApkDir();

}
