package com.yiyou.gamesdk.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import com.mobilegamebar.rsdk.outer.util.Log;


/**
 * Created by parry on 17/4/25.
 */

public class CustomerServiceHelper {

    private static final String TAG = "CustomerServiceHelper";

    public static boolean connectCustomerService (Context context) {

        //TODO 客服QQ
        String qqNum = "800819365";

        if (checkHasInstallQQ(context)){
//            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin="+qqNum+"&version=1")));

            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=crm&uin="+qqNum+"&version=1&src_type=web&web_src=null")));

            return true;
        }else{
            Toast.makeText(context,"请先安装QQ",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //检查是否安装QQ app
    public static boolean checkHasInstallQQ (Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            packageManager.getPackageInfo("com.tencent.mobileqq", 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "checkHasInstallQQ: ", e);
            return false;
        } catch (Exception e) {
            //不鸟了...
            return false;
        }

        return true;
    }


//    private static boolean checkApkExist(Context context, String packageName) {
//        if (packageName == null || "".equals(packageName))
//            return false;
//        try {
//            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
//                    PackageManager.GET_UNINSTALLED_PACKAGES);
//            return true;
//        } catch (PackageManager.NameNotFoundException e) {
//            return false;
//        }
//    }

}
