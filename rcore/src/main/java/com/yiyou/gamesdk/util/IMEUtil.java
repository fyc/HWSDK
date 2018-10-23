package com.yiyou.gamesdk.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mobilegamebar.rsdk.outer.util.Log;

/**
 * Created by levyyoung on 15/8/24.
 */
public class IMEUtil {
    public static void hideIME(final View view) {
        if (view == null) {
            return;
        }
        InputMethodManager ims
                = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean ret = ims.hideSoftInputFromWindow(view.getApplicationWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        Log.d("[RSDK-IMEUtil]", String.format("win-id: %d hide ret : %b",
                view.getApplicationWindowToken().hashCode(), ret));
    }
}
