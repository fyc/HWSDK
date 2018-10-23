package com.yiyou.gamesdk.util;

import android.widget.Toast;

import com.yiyou.gamesdk.core.CoreManager;
import com.yiyou.gamesdk.R;

/**
 * Created by levyyoung on 15/6/30.
 */
public class DebugUtil {
    public static void warningNoLogin() {
        Toast.makeText(CoreManager.getContext(),
                R.string.error_no_login, Toast.LENGTH_LONG).show();
    }
}
