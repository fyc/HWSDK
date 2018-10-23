package com.yiyou.gamesdk.util;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.mobilegamebar.rsdk.outer.util.Log;

/**
 * Created by Orange on 15/8/3.
 */
public class SystemHelper {
    private static final String TAG = SystemHelper.class.getSimpleName();

    // ************************** 设备 ************************* //
    public static class Device {
        public static boolean isMeiZu() {
            String manufacturer = Build.MANUFACTURER;
            Log.d(TAG, manufacturer);
            return manufacturer != null && manufacturer.toUpperCase().contains("MEIZU");
        }

        public static boolean isVIVO() {
            String manufacturer = Build.MANUFACTURER;
            return manufacturer != null && manufacturer.toUpperCase().contains("VIVO");
        }

        public static boolean isMX4() {
            String device = Build.DEVICE;
            Log.d(TAG, "device %s", device);
            return device != null && device.equalsIgnoreCase("MX4");
        }

        public static boolean isHuawei() {
            String manufacturer = Build.MANUFACTURER;
            //Log.d(TAG, manufacturer);
            return manufacturer != null && manufacturer.toUpperCase().contains("HUAWEI");
        }

        public static boolean isOPPO() {
            String manufacturer = Build.MANUFACTURER;
            //Log.d(TAG, manufacturer);
            return manufacturer != null && manufacturer.toUpperCase().contains("OPPO");
        }

        public static boolean isSamsung() {
            String manufacturer = Build.MANUFACTURER;
            //Log.d(TAG, manufacturer);
            return manufacturer != null && manufacturer.toUpperCase().contains("SAMSUNG");
        }

        public static boolean isHongMi() {
            if (!"Xiaomi".equalsIgnoreCase(Build.MANUFACTURER)) {
                return false;
            }
            String model = Build.MODEL;
            if (TextUtils.isEmpty(model)) {
                return false;
            }
            String upper = model.toUpperCase();
            return upper.contains("HM 1S") || //红米1S
                    upper.contains("2014501") || //红米1S
//                    upper.contains("HM NOTE 1") || //红米Note
                    upper.equalsIgnoreCase("2013022") //红米
                    ;
        }

        public static boolean isXiaoMi() {
            String manufacturer = Build.MANUFACTURER;
            //Log.d(TAG, manufacturer);
            return manufacturer != null && manufacturer.toUpperCase().contains("XIAOMI");
        }

        public static boolean isBelowXiaomi2S() {
            if (!"Xiaomi".equalsIgnoreCase(Build.MANUFACTURER)) {
                return false;
            }
            String model = Build.MODEL;
            if (TextUtils.isEmpty(model)) {
                return false;
            }
            String upper = model.toUpperCase();
            // 又支持MI2s了
            return /*upper.contains("MI 2") || *///米2S 米2A。 这样写，出Mi 20的时候会不会出现类似Win 9x的问题呢？
                    upper.contains("MI2") || //小米2
//                    upper.equalsIgnoreCase("MI 1S") ||
                            upper.equalsIgnoreCase("MI-ONE Plus")
                    ;
        }


        // ************************** 系统 ************************* //
        public static class Rom {
            public static boolean isMIUI(Context context) {
                String miuiVersion = getMiuiVersionString(context);
                if (!TextUtils.isEmpty(miuiVersion)) {
                    return "V5".equalsIgnoreCase(miuiVersion) || "V6".equalsIgnoreCase(miuiVersion) || "V7".equalsIgnoreCase(miuiVersion);
                } else {
                    return false;
                }
            }

            public static boolean isMIUI_V5_OR_V6(Context context) {
                String miuiVersion = getMiuiVersionString(context);
                if (!TextUtils.isEmpty(miuiVersion)) {
                    return "V5".equalsIgnoreCase(miuiVersion) || "V6".equalsIgnoreCase(miuiVersion);
                } else {
                    return false;
                }
            }

            private static String getMiuiVersionString(Context context) {
                String miuiVersion = SystemPropertiesProxy.get(context,
                        "ro.miui.ui.version.name");
                Log.d(TAG, "miui ->" + miuiVersion);
                return miuiVersion;
            }

            public static boolean isMIUI_V7(Context context) {
                String miuiVersion = getMiuiVersionString(context);
                if (!TextUtils.isEmpty(miuiVersion)) {
                    return "V7".equalsIgnoreCase(miuiVersion);
                } else {
                    return false;
                }
            }

            public static boolean isMIUI_V6_OR_V7(Context context) {
                String miuiVersion = getMiuiVersionString(context);
                if (!TextUtils.isEmpty(miuiVersion)) {
                    return "V6".equalsIgnoreCase(miuiVersion) || "V7".equalsIgnoreCase(miuiVersion);
                } else {
                    return false;
                }
            }
        }
    }
}
