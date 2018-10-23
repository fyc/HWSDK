package com.mobilegamebar.rsdk.outer;

import android.content.Context;
import android.content.SharedPreferences;

import com.mobilegamebar.rsdk.outer.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by win on 2017/4/27.
 */
public class RSDKSpace {

    private static final String TAG = "RSDK:RSDKSpace";

    private static final String ASSETS_VERSUIN_CONFIG = "haowan_rsdk_config/version.config";
    public static final String ASSETS_APK_DIR = "apk" + File.separator;
    public static final String ASSETS_APK = "apk";

    public static final String SHAREP_NAME = "rsdk_update_config";
    public static final String KEY_CUR_VERSION = "key_cur_version";
    public static final String KEY_OLD_VERSION = "key_old_version";
    public static final String KEY_FIRST_VERSION = "key_first_version";
    public static final String KEY_SDK_VERSION = "sdk_version";

    private static RSDKSpace instance;

    private Context mContext;//application context get file form assets

    private RSDKSpace(Context context) {
        mContext = context.getApplicationContext();
    }

    public synchronized static RSDKSpace getInstance(Context context) {
        if (instance == null)
            instance = new RSDKSpace(context);

        return instance;
    }

    public void uninit() {
        mContext = null;
        instance = null;
    }


    public SharedPreferences getSharePreferences() {

        return mContext.getSharedPreferences(SHAREP_NAME, Context.MODE_PRIVATE);
    }


    /**
     * 第一次安装 从assets 读出配置
     *
     * @return config
     */
    public Map<String, String> parseConfig() {

        Map<String, String> verison = new HashMap<>();

        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(mContext.getAssets().open(ASSETS_VERSUIN_CONFIG)));

            String line = "";
            while ((line = br.readLine()) != null) {

                String[] contents = line.split("=");

                if (contents.length > 1) {

                    String key = contents[0];
                    String value = contents[1];

                    Log.d(TAG, "key= " + key);
                    Log.d(TAG, "value= " + value);
                    verison.put(key, value);
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return verison;

    }
}
