package com.yiyou.gamesdk.util;

import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Win on 17-11-29.
 */

public class FloatWinCompatHelper {

    private static Boolean stateMIUIOverV8 = null;
    private static Boolean stateIsXiaoMi = null;

    public static boolean isDeviceNotSupportNewWindowType() {
        return isMIUIOverV8() || is360() || isSOP();
    }

    private static boolean checkManufacturerOrBoard(@NonNull String uppercaseKeyword) {
        return (Build.MANUFACTURER != null && Build.MANUFACTURER.toUpperCase()
            .contains(uppercaseKeyword)) || (Build.BOARD != null && Build.BOARD.toUpperCase()
            .contains(uppercaseKeyword));
    }

    public static boolean isSOP() {
        return checkManufacturerOrBoard("SOP");
    }

    public static boolean is360() {
        return checkManufacturerOrBoard("360");
    }

    public static boolean isXiaoMi() {
        if (stateIsXiaoMi == null)
        {
            String manufacturer = Build.MANUFACTURER;
            stateIsXiaoMi =  (manufacturer != null && manufacturer.toUpperCase().contains("XIAOMI"));
        }
        return stateIsXiaoMi;
    }

    public static boolean isMIUIOverV8() {
        if (stateMIUIOverV8 == null) {
            stateMIUIOverV8 =
                isXiaoMi() && (convertMIUIVersionToInt(getMIUIVersion()) >= 8);
        }
        return stateMIUIOverV8;
    }

    public static String getMIUIVersion() {
        return getSystemProperty("ro.miui.ui.version.name");
    }

    public static int convertMIUIVersionToInt(String version) {
        Log.e("FLT", "MIUI VERSION 》" + version);
        try {
            if (!TextUtils.isEmpty(version) && version.length() > 1) {
                String sub = version.substring(1, version.length());
                return Integer.parseInt(sub);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static String getSystemProperty(String property) {
        BufferedReader in = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + property);
            in = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            return  in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

}
