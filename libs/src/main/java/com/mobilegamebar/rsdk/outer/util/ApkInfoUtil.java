package com.mobilegamebar.rsdk.outer.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Created by chenshuide on 15/6/11.
 */
public class ApkInfoUtil {
    private static final String CPU_ARMEABI = "armeabi";
    private static final String CPU_ARMEABI_V7A = "armeabi-v7a";
    private static final String CPU_X86 = "x86";
    private static final String CPU_X86_64 = "x86_64";
    private static final String ARM64_V8A = "arm64-v8a";
    private static final String TAG = "RSDK:ApkInfoUtil ";

    /**
     * get cpu name, according cpu type parse relevant so lib
     *
     * @return ARM、ARMV7、X86、MIPS
     */
    @SuppressLint("NewApi")
    public static String[] getCpuABIs() {
        if (Build.VERSION.SDK_INT >= 21) {
            return Build.SUPPORTED_ABIS;
        } else {
            return new String[]{Build.CPU_ABI, Build.CPU_ABI2};
        }
    }


    /**
     * 获取匹配的cpu架构 default see {@link ApkInfoUtil#CPU_ARMEABI}
     *
     * @param supportCpuABIs we support cpuabi
     * @return match cpuabi
     */
    public static String getCpuArch(String[] supportCpuABIs) {
        String[] cpuABIs = getCpuABIs();
        Log.d(TAG, Arrays.toString(cpuABIs));
        if (cpuABIs != null && supportCpuABIs != null) {
            for (String cpuABI : cpuABIs) {
                for (String supportCpuABI : supportCpuABIs) {
                    if (cpuABI.equalsIgnoreCase(supportCpuABI)) {
                        return supportCpuABI;
                    }
                }
            }
        }
        return CPU_ARMEABI;
    }


    public static String chooseByX86andArm(Context context) {

        String hostCpu = getCpuArchByAppInfo(context);
        String[] supportCpuABIs = {CPU_ARMEABI, CPU_ARMEABI_V7A,CPU_X86, ARM64_V8A, CPU_X86_64};

        for (String supportCpuABI : supportCpuABIs) {
            if (hostCpu.equalsIgnoreCase(supportCpuABI)) {
                return hostCpu;
            }
        }


        for (String supportCpuABI : supportCpuABIs) {
            if (hostCpu.startsWith(supportCpuABI)) {
                return supportCpuABI;
            }
        }

        return getCpuArch(supportCpuABIs);

    }

    public static String getCpuArchByAppInfo(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        String cpuArch = "";
        try {

            Field primaryCpuAbiField = ApplicationInfo.class.getDeclaredField("primaryCpuAbi");
            primaryCpuAbiField.setAccessible(true);


            Object o1 = primaryCpuAbiField.get(applicationInfo);
            if (o1 != null) {
                cpuArch = o1.toString();
                Log.d(TAG, "application" + cpuArch);

            }


        } catch (Exception e) {
            Log.d(TAG, "getCpuArchByAppInfo" + e.getMessage());
        }


        return cpuArch;

    }

}
