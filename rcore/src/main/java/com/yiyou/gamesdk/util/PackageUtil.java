package com.yiyou.gamesdk.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Created by chenshuide on 15/8/4.
 */
public class PackageUtil {

    private static final String TAG = "TTSDK:PackageUtil";

    /**
     * 获取签名信息
     *
     * @param context context
     * @return Signature[]
     */
    public static Signature[] getSignature(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            return packageInfo.signatures;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }


    /**
     * 从指定apk获取签名信息
     *
     * @param context context
     * @param apkPath apkpath
     * @return Signature[]
     */
    public static Signature[] getSignature(@NonNull Context context, @NonNull String apkPath) {

        try {

            PackageInfo packageArchiveInfo = context.getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_SIGNATURES);
            return packageArchiveInfo.signatures;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }


    /**
     * 获取versionCode
     *
     * @param context
     * @return
     */
    public static int getVersionCode(@NonNull Context context) {

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return packageInfo.versionCode;


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 获取versionCode
     *
     * @param context
     * @return
     */
    public static String getVersionName(@NonNull Context context) {

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return packageInfo.versionName;


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }


    public void parseSignature(byte[] signature) {
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));
            String pubKey = cert.getPublicKey().toString();
            String signNumber = cert.getSerialNumber().toString();
            System.out.println("pubKey:" + pubKey);
            System.out.println("signNumber:" + signNumber);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
    }


    /**
     * 安装apk
     *
     * @param context context
     * @param uri     uri
     */
    public static void installApk(Context context, Uri uri) {
        Log.d(TAG,"installApk, uri: " + uri);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static boolean isPkgInstalled(Context context, String packageName) {

        if (packageName == null || "".equals(packageName)){
            Log.d(TAG,"isPkgInstalled: false");
            return false;
        }
        android.content.pm.ApplicationInfo info = null;
        try {
            info = context.getPackageManager().getApplicationInfo(packageName, 0);
            Log.d(TAG,"isPkgInstalled: " + (info != null));
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG,"isPkgInstalled: false");
            return false;
        }
    }

    public static String getAppName(Context context)
    {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName =
                (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }

}
