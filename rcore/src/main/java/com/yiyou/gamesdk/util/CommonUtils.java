package com.yiyou.gamesdk.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.mobilegamebar.rsdk.outer.event.EventDispatcherAgent;
import com.mobilegamebar.rsdk.outer.event.StartActivityEvent;
import com.yiyou.gamesdk.container.MainActivity;
import com.yiyou.gamesdk.core.CoreManager;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.consts.StringConstant;
import com.yiyou.gamesdk.core.ui.fragment.UnInternalLinkFragment;

import java.util.Locale;

public class CommonUtils {
    private static final String TAG = "CommonUtils";

    /**
     * 判断App是否已经安装
     *
     * @param pkgName 包名
     * @param context 上下文
     * @return
     */
    public static boolean isAppInstalled(String pkgName, Context context) {
        if (context != null) {
            PackageManager pm = context.getPackageManager();
            if (pm != null) {
                try {
                    return pm.getPackageInfo(pkgName,
                            PackageManager.GET_SIGNATURES) != null;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 打开本机的app
     *
     * @param activity 上下文
     * @return
     */
    public static boolean openApp(Activity activity) {
        Log.d(TAG, "openApp: ");
        if (CoreManager.isDebug()) {
            if (!(PackageUtil.isPkgInstalled(activity, StringConstant.APP_DEBUG_PACKAGE))) {
                ((MainActivity) activity).startFragment(new UnInternalLinkFragment());
//                ToastUtils.showMsg("未安装桃子手游APP");
                return true;
            }
            if (activity != null) {
                PackageManager pm = activity.getPackageManager();
                if (pm != null) {
                    String pkgName = StringConstant.APP_DEBUG_PACKAGE;
                    Intent intent = pm.getLaunchIntentForPackage(pkgName);
                    if (intent != null) {
                        activity.startActivity(intent);
                        return true;
                    }
                }
            }
        } else {
            if (!(PackageUtil.isPkgInstalled(activity, StringConstant.APP_PACKAGE))) {
                ((MainActivity) activity).startFragment(new UnInternalLinkFragment());
//                ToastUtils.showMsg("未安装桃子手游APP");
                return true;
            }
            if (activity != null) {
                PackageManager pm = activity.getPackageManager();
                if (pm != null) {
                    String pkgName = StringConstant.APP_PACKAGE;
                    Intent intent = pm.getLaunchIntentForPackage(pkgName);
                    if (intent != null) {
                        activity.startActivity(intent);
                        return true;
                    }
                }
            }
        }
//        if (!(PackageUtil.isPkgInstalled(activity, StringConstant.APP_DEBUG_PACKAGE) || PackageUtil.isPkgInstalled(activity, StringConstant.APP_PACKAGE))) {
////            ToastUtils.showMsg("未安装桃子手游APP");
//            if (((MainActivity) activity) != null) {
//                ((MainActivity) activity).showDownloadFragment();
//            }
//            return true;
//        }
//        if (activity != null) {
//            PackageManager pm = activity.getPackageManager();
//            if (pm != null) {
//                String pkgName = CoreManager.isDebug() ? StringConstant.APP_DEBUG_PACKAGE : StringConstant.APP_PACKAGE;
//                Intent intent = pm.getLaunchIntentForPackage(pkgName);
//                if (intent != null) {
//                    activity.startActivity(intent);
//                    return true;
//                }
//            }
//        }
        return false;
    }

    public static boolean handleUrl(String url, Activity activity, boolean isOpenInBrowser) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        Log.d(TAG, "handleUrl: " + url);
        if (url.startsWith("tzsy")) {
            if (!(PackageUtil.isPkgInstalled(activity, StringConstant.APP_DEBUG_PACKAGE) || PackageUtil.isPkgInstalled(activity, StringConstant.APP_PACKAGE))) {
                if (activity instanceof MainActivity){
                    ((MainActivity) activity).startFragment(new UnInternalLinkFragment());
                }else {
                    EventDispatcherAgent.defaultAgent().broadcast(StartActivityEvent.TYPE_START_ACTIVITY_ENVENT,
                            new StartActivityEvent.FragmentParam(0, activity,
                                    StartActivityEvent.DISPLAY_TYPE_FULLSCREEN,
                                    null,UnInternalLinkFragment.class.getName(), null));
                }
//                ToastUtils.showMsg("未安装桃子手游APP");
                return true;
            }
            int gameId = ApiFacade.getInstance().getCurrentGameID();
            try {
                Uri uri = Uri.parse("tzsy://home/navigation");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.putExtra("game_id", gameId);
                intent.putExtra("target_navigation_uri", url);
                intent.putExtra("target_navigation_from", "sdk");
                activity.startActivity(intent);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showMsg("没有可打开的页面！");
                return true;
            }
        } else {
            if (isOpenInBrowser) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                intent.addCategory("android.intent.category.DEFAULT");
                intent.addCategory("android.intent.category.BROWSABLE");
                activity.startActivity(intent);
                return true;
            } else {
                return false;
            }

        }
    }


    /**
     * 分 为单位，  转化为 元为单位，  小于一元保留小数
     *
     * @return
     */
    public static String pennyToYuan(long penny) {
        String sAmount;

        if (penny % 100 == 0) {
            sAmount = Math.round(((float) penny / 100)) + "";
        } else {
            sAmount = ((float) (Math.round(((float) penny / 100) * 10)) / 10) + "";
        }
        return sAmount;
    }

    public static void setClipboardText(Context context, String text) {
        try {
            ClipboardManager cm = (ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setPrimaryClip(ClipData.newPlainText(null, text));
            ToastUtils.showMsg("已复制");
        } catch (Exception e) {
        }
    }

    public static String format(String str, Object... args) {
        return String.format(Locale.getDefault(), str, args);
    }

}