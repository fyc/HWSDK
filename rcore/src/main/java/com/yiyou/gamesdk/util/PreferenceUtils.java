package com.yiyou.gamesdk.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by levyyoung on 15/8/26.
 */
public class PreferenceUtils {
    public static class SDKPreference {
        private static final String PREFERENCE_NAME = "tt_sdk";

        public static final String KEY_MESSAGE_LIST_MAX_SEQ = "msg_list_max_seq";

        public static final String KEY_FLOAT_PERMISSION = "float_permission";
        
        public static final String KEY_GAME_DOWNLOAD_ID = "download_id" ;
        
        public static SharedPreferences instancePreferences(Context context) {
            return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        }

    }
}
