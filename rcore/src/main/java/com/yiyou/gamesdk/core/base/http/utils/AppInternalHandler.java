package com.yiyou.gamesdk.core.base.http.utils;

import android.app.Activity;
import android.util.Log;

import com.yiyou.gamesdk.util.CommonUtils;

import java.util.Locale;

/**
 * Created by BM on 2017/11/15.
 * <p>
 * desc:
 */

public class AppInternalHandler {
    public static final String TAG = "AppInternalHandler";


    public static String tabTrade(Activity activity) {
        if (activity == null) {
            return null;
        }
        String link = "tzsybm://nav/home?selectedTab=trade";
        CommonUtils.handleUrl(link, activity, false);
        Log.d(TAG, link);
        return link;
    }

    public static String inventoryDetail(Activity activity, int inventoryId, int gameId) {
        if (activity == null) {
            return null;
        }
        String link = String.format(Locale.getDefault(),
                "tzsybm://nav/trade/detail?inventoryID=%d&gameId=%d",
                inventoryId, gameId);
//        tabTrade(activity);
        CommonUtils.handleUrl(link, activity, false);
        Log.d(TAG, link);
        return link;
    }

    public static String singleInventoryList(Activity activity, int gameId) {
        if (activity == null) {
            return null;
        }
        String link = String.format(Locale.getDefault(),
                "tzsybm://nav/trade/single_inventory?gameId=%d ",
                gameId);
//        tabTrade(activity);
        CommonUtils.handleUrl(link, activity, false);
        Log.d(TAG, link);
        return link;
    }

    public static String sellMyPlayed(Activity activity) {
        if (activity == null) {
            return null;
        }
        String link = "tzsybm://nav/trade/sell_my_played";
//        tabTrade(activity);
        CommonUtils.handleUrl(link, activity, false);
        Log.d(TAG, link);
        return link;
    }

    public static String gameDetail(Activity activity, int gameId) {
        if (activity == null) {
            return null;
        }
        String link = String.format(Locale.getDefault(),
                "tzsybm://nav/game/detail?gameId=%d",
                gameId);
        CommonUtils.handleUrl(link, activity, false);
        Log.d(TAG, link);
        return link;
    }

    public static String changeBindPhone(Activity activity) {
        if (activity == null) {
            return null;
        }
        String link = "tzsybm://nav/mine/change_bind_phone";
//        tabTrade(activity);
        CommonUtils.handleUrl(link, activity, false);
        Log.d(TAG, link);
        return link;
    }

//    public static String bindPhone(Activity activity) {
//        if (activity == null) {
//            return null;
//        }
//        String link = "tzsybm://nav/mine/bind_phone";
////        tabTrade(activity);
//        CommonUtils.handleUrl(link, activity, false);
//        Log.d(TAG, link);
//        return link;
//    }

    public static String changePassword(Activity activity) {
        if (activity == null) {
            return null;
        }
        String link = "tzsybm://nav/mine/change_password";
//        tabTrade(activity);
        CommonUtils.handleUrl(link, activity, false);
        Log.d(TAG, link);
        return link;
    }

    public static String currentInventoryList(Activity activity, int gameId) {
        if (activity == null) {
            return null;
        }
        String link = String.format(Locale.getDefault(),
                "tzsybm://nav/trade/single_inventory?gameId=%d",
                gameId);
        CommonUtils.handleUrl(link, activity, false);
        Log.d(TAG, link);
        return link;
    }
}
