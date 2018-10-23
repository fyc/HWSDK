package com.yiyou.gamesdk.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import com.yiyou.gamesdk.core.base.http.utils.HttpUtils;
import java.lang.reflect.Field;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by chenshuide on 15/6/9.
 */
public class PhoneUtils {

    private static String DEVICES = "";

    public static String getDeviceId(Context context) {
        if(!TextUtils.isEmpty(DEVICES)){
            return DEVICES;
        }
        byte[] bytes = TdkUtil.getA();
        if (bytes == null) {
            return "";
        }
        DEVICES = ByteUtils.bufferToHex(bytes);
        return DEVICES;
    }

    public static String getImei(Context context) {

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = "";
        }


        return deviceId;

    }


    /**
     * 获取ip
     *
     * @param context
     * @return
     */
    public static String getIp(Context context) {
        if (HttpUtils.isWiFiActive(context)) {
            return getLocalIpAddress(context);
        } else {
            return getIpAddress();
        }
    }

    public static String getIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取本机WIFI ip
     *
     * @return ip
     */
    private static String getLocalIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        // 获取32位整型IP地址
        int ipAddress = wifiInfo.getIpAddress();

        //返回整型地址转换成“*.*.*.*”地址
        return String.format("%d.%d.%d.%d",
                (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }


    /**
     * 获取手机mac地址
     */
    public static String getMacAddress(Context context) {
        String macAddress = "";

        try {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            macAddress = info.getMacAddress();

        } catch (Exception e) {

        }

        return macAddress;
    }


    /**
     * 收集设备参数信息
     *
     * @param context
     */
    public static String collectDeviceInfo(Context context) {
        StringBuilder sb = new StringBuilder();
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                sb.append("versionName = " + versionName);
                sb.append("  versionCode = " + versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                sb.append("  " + field.getName() + " = " + field.get(null).toString());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    /**
     * 关闭软键盘
     *
     * @param context
     */
    public static void closeInput(Context context) {
        if (((Activity) context).getCurrentFocus() != null) {
            ((InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


}
