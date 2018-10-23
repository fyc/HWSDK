package com.yiyou.gamesdk.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

import com.mobilegamebar.rsdk.outer.RSDKSpace;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.yiyou.gamesdk.core.CoreManager;

public class VersionUtil {
    private static String CORE_VERSION;

    private static final String TAG = "TTSDK: "+"VersionUtil";

    static int sLocalVer[] = null;
    static String sLocalName = null;
    private static final String SNAPSHOT = "-SNAPSHOT";

    private static final String DOT = ".";
    private static final String CONJ = "-";

    public static Ver getVerFromStr(String version) {
        String normalVer = version;
        if (version != null && version.matches("\\d{1,}.\\d{1,}.\\d{1,}(-(\\w*))*")) {
            normalVer = version.split(CONJ)[0];
        }
        if (normalVer != null && normalVer.matches("\\d{1,}.\\d{1,}.\\d{1,}")) {
            //conjPos to anti version like d.d.d-AppPrefixId
            int conjPos = normalVer.indexOf(CONJ);
            Ver ver = new Ver();
            int dotPos = normalVer.indexOf(DOT);
            int prevPos = 0;
            ver.mMajor = Integer.valueOf(normalVer.substring(prevPos, dotPos));
            prevPos = dotPos + 1;
            dotPos = normalVer.indexOf(DOT, prevPos);
            ver.mMinor = Integer.valueOf(normalVer.substring(prevPos, dotPos));
            prevPos = dotPos + 1;
            ver.isSnapshot = version.contains(SNAPSHOT);
            if (!ver.isSnapshot && conjPos > prevPos) {
                ver.mBuild = Integer.valueOf(normalVer.substring(prevPos, conjPos));
            } else {
                ver.mBuild = Integer.valueOf(normalVer.substring(prevPos));
            }
            return ver;
        }
        return null;
    }

    public static Ver getLocalVer(Context c) {
        Ver v = new Ver();
        int ver[] = getLocal(c);
        v.mMajor = ver[0];
        v.mMinor = ver[1];
        v.mBuild = ver[2];
        v.isSnapshot = ver[3] == 1 ? true : false;
        return v;
    }

    public static String getLocalName(Context c) {
        if (sLocalName != null) {
            return sLocalName;
        }

        try {
            loadLocalVer(c);
        } catch (Exception e) {
            sLocalVer = new int[4];
            sLocalVer[0] = 0;
            sLocalVer[1] = 0;
            sLocalVer[2] = 0;
            sLocalVer[3] = 0;
        }

        return sLocalName;
    }

    public static int[] getLocal(Context c) {
        if (sLocalVer != null) {
            return sLocalVer.clone();
        }
        try {
            loadLocalVer(c);
        } catch (Exception e) {
            sLocalVer = new int[4];
            sLocalVer[0] = 0;
            sLocalVer[1] = 0;
            sLocalVer[2] = 0;
            sLocalVer[3] = 0;
        }

        return sLocalVer.clone();
    }

    public static int getVersionCode(Context c) {
        int verCode = 0;
        try {
            verCode = c.getPackageManager().getPackageInfo(c.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {

        }

        return verCode;
    }

    static void loadLocalVer(Context c) {
        try {
            sLocalName = c.getPackageManager().getPackageInfo(c.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            throw new RuntimeException("Local Ver Package Error");
        }

        if (sLocalName == null) {
            throw new RuntimeException("Local Ver VersionName Not Exist");
        }

        Ver ver = VersionUtil.getVerFromStr(sLocalName);
        sLocalVer = ver.toVerCode();
    }

    /**
     * Version: reserved(4).Major(4).Minor(8).release(16)
     * 比如：
     * 0x3010064
     * 也就是 3.1版本第100个release
     * @param c
     * @return
     */
    public static int getVersionInBit(Context c) {
        if (sLocalVer == null) {
            try {
                loadLocalVer(c);
            } catch (Exception e) {
                Log.w(TAG, e.getMessage());
                sLocalVer = new int[4];
                sLocalVer[0] = 0;
                sLocalVer[1] = 0;
                sLocalVer[2] = 0;
                sLocalVer[3] = 0;
            }
        }

        /*TEST BEGIN*/
        /*
        sLocalVer[0] = 3;
        sLocalVer[1] = 1;
        sLocalVer[2] = 100;
        */
        /*TEST END*/

        return intArrToVerBit(sLocalVer);
    }

    /**
     * @param version  format in "%d.%d.%d"
     * @return
     */
    public static int toVersionBit(String version) {
        Ver ver = getVerFromStr(version);
        if (ver != null) {
            return intArrToVerBit(ver.toVerCode());
        }
        return 0;
    }

    private static int intArrToVerBit(int[] intArr) {
        if (intArr != null && intArr.length >= 3) {
            int result = 0;
            result |= (intArr[0] << 24);
            result |= (intArr[1] << 16);
            result |= (intArr[2] << 0);
            return result;
        }
        return 0;
    }

    public static String getSdkVersion(){
        SharedPreferences sharePreferences = RSDKSpace.getInstance(CoreManager.getContext()).getSharePreferences();
        return   sharePreferences.getString(RSDKSpace.KEY_SDK_VERSION, "");

    }


    public static String getCoreVersion(){
        if (TextUtils.isEmpty(CORE_VERSION)) {
            SharedPreferences sharePreferences = RSDKSpace.getInstance(CoreManager.getContext()).getSharePreferences();
            CORE_VERSION = sharePreferences.getString(RSDKSpace.KEY_CUR_VERSION, "");
        }
        return CORE_VERSION;

    }
}
