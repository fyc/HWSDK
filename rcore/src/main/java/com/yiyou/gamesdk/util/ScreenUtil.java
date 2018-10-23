package com.yiyou.gamesdk.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by chenshuide on 16-2-16.
 */
public class ScreenUtil {


    @TargetApi(19)
    public static boolean isHostHideNAVIGATION(Activity host) {

        if (Build.VERSION.SDK_INT >= 19) {

            int hostFlag = host.getWindow().getDecorView().getSystemUiVisibility();

            //隐藏虚拟按键
            int currentFlag = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            int result = hostFlag & currentFlag;

            return result != 0;

        }

        return false;



    }

    public static String getPixels(Activity activity){
        WindowManager windowManager = activity.getWindowManager();
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);// 获取分辨率
        return dm.widthPixels + "*" + dm.heightPixels;
    }


}
