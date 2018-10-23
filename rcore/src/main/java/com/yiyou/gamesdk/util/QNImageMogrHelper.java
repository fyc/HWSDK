package com.yiyou.gamesdk.util;

import com.mobilegamebar.rsdk.outer.util.StringUtils;

/**
 * Created by parry on 17/7/7.
 */

public class QNImageMogrHelper {

    public static String hanlderGameIconUrl(String originUrl) {

        if (StringUtils.isEmpty(originUrl)) {
            return "";
        }

        return handlerImageUrl(128, 70, originUrl);
    }

    public static String handlerGameDetailImageUrl(String originUrl) {

        if (StringUtils.isEmpty(originUrl)) {
            return "";
        }

        return handlerImageUrl(420, 70, originUrl);
    }

    public static String handleCoverImgUrl(String originUrl) {

        if (StringUtils.isEmpty(originUrl)) {
            return "";
        }

        return handlerImageUrl(420, 70, originUrl);
    }

    public static String handlerImageUrl(int width, int quality, String originUrl) {

        if (StringUtils.isEmpty(originUrl)) {
            return "";
        }

        String imageMogrString = "imageMogr2/thumbnail/" + width + "/quality/" + quality;

        //已经有参数并且?不是最后一位
        if (originUrl.contains("?")) {

            if (originUrl.endsWith("?")) {
                return originUrl + imageMogrString;
            } else {
                return originUrl + "&" + imageMogrString;
            }

        } else {
            return originUrl + "?" + imageMogrString;
        }
    }

}
