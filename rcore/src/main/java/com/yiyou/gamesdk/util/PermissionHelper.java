package com.yiyou.gamesdk.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by cq on 2017/5/16.
 */

public class PermissionHelper {
    public static final String LOG_TAG = "permission";

    @TargetApi(16)
    public static boolean requestNecessaryPermission(Activity activity, int requestCode) {
        String[] permissions =
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ? new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                } : new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };
        return requestDangerousPermission(activity, permissions, requestCode);
    }

    public static boolean checkPermissionRequest(String[] permissions, int[] grantResults) {
        if (permissions.length == 0 || grantResults.length == 0) {
            return false;
        }
        // If request is cancelled, the result arrays are empty.
        boolean hasPermission = true;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                hasPermission = false;
                break;
            }
        }
        //或许需要提示到底哪个权限没给？就要返回String[]了。  然而，提示个毛，不给还想玩？
        return hasPermission;
    }

    public static boolean requestDangerousPermission(Activity activity, String[] permissions,
                                                     int requestCode) {
        boolean needRequestPermission = false;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                needRequestPermission = true;
                break;
            }
        }

        if (needRequestPermission) {
            boolean shouldShowRequestPermissionRationale = false;
            for (String permission : permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    shouldShowRequestPermissionRationale = true;
                    break;
                }
            }

            if (shouldShowRequestPermissionRationale) {
                // Should we show an explanation?
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(activity, permissions, requestCode);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity, permissions, requestCode);
            }
            return false;
        } else {
            return true;
        }
    }

    public static class Storage {
        public static boolean hasStoragePermission(Context context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                        context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        && PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                        context, Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                        context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
    }
}
