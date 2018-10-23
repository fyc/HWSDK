package com.yiyou.gamesdk.core.base.http;

import android.os.Build;

import com.mobilegamebar.rsdk.outer.util.ResourceHelper;
import com.yiyou.gamesdk.core.CoreManager;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.util.PhoneUtils;
import com.yiyou.gamesdk.util.VersionUtil;

import java.util.Map;

/**
 * Created by win on 17/4/24.
 */
public class RequestHelper {
    /**
     * 构建参数 添加ip mac imei deviceid cpid gameid
     *
     * @param params
     * @return params
     */
    public static Map<String, String> buildParamsWithBaseInfo(Map<String, String> params) {
        if (params == null)
            return null;
        buildParamsWithPhoneInfo(params);
        buildParamsWithGameInfo(params);
        return params;
    }

    /**
     * 构建参数 添加ip mac imei deviceid cpid gameid
     *
     * @param params
     * @return params
     */
    public static Map<String, String> buildParamsWithAppInfo(Map<String, String> params) {
        if (params==null)
            return null;
        params.put("deviceID", PhoneUtils.getDeviceId(CoreManager.getContext()));
        params.put("model", Build.MODEL);
        params.put("osVer", String.valueOf(Build.VERSION.SDK_INT));
        params.put("platform", "android");
        params.put("fromApp", "android_sdk");
        params.put("bundleID", ResourceHelper.getPackageName());
        params.put("appVer", VersionUtil.getSdkVersion()+"-R"+ VersionUtil.getCoreVersion());
        params.put("accessToken",ApiFacade.getInstance().getSession());
        params.put("channelID", ApiFacade.getInstance().getChannel());
        return params;
    }
    /**
     * 构建参数 添加ip mac imei deviceid
     *
     * @param params pre-params
     * @return params append phoneinfo params
     */
    public static Map<String, String> buildParamsWithPhoneInfo(Map<String, String> params) {
        if (params == null)
            return null;
        params.put("deviceID", PhoneUtils.getDeviceId(CoreManager.getContext()));
        params.put("imei", PhoneUtils.getImei(CoreManager.getContext()));
        params.put("mac", PhoneUtils.getMacAddress(CoreManager.getContext()));
        params.put("ip", PhoneUtils.getIp(CoreManager.getContext()));
        params.put("model", Build.MODEL);
        params.put("osVer",Build.VERSION.RELEASE);
        params.put("appVer", VersionUtil.getSdkVersion()+"-R"+ VersionUtil.getCoreVersion());
        return params;

    }

    /**
     * 构建参数 添加 gameid cpid userid
     *
     * @param params pre-params
     * @return params append gameinfo params
     */
    public static Map<String, String> buildParamsWithGameInfo(Map<String, String> params) {
        if (params == null)
            return null;
        params.put("userID", ApiFacade.getInstance().getMainUid() + "");
        params.put("gameID", String.valueOf(ApiFacade.getInstance().getCurrentGameID() + ""));
        params.put("channel", ApiFacade.getInstance().getChannel());
        params.put("bundleID",CoreManager.getContext().getPackageName());
        params.put("platform","2");
        return params;

    }

}
