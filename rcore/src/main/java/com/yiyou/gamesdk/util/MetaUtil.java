package com.yiyou.gamesdk.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.yiyou.gamesdk.core.CoreManager;
import com.mobilegamebar.rsdk.outer.util.StringUtils;

/**
 * Created by levyyoung on 15/12/16.
 */
public class MetaUtil {
    public static String getMeta(String key){
        Context appCtx = CoreManager.getContext().getApplicationContext();
        Bundle metaDatas = null;
        try {
            metaDatas = appCtx.getPackageManager()
                    .getApplicationInfo(appCtx.getPackageName(), PackageManager.GET_META_DATA)
                    .metaData;
            if (metaDatas != null) {
                String value = metaDatas.getString(key);
                if (!StringUtils.isBlank(value)) {
                    return Uri.decode(value);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
