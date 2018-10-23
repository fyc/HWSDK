package com.mobilegamebar.rsdk.outer;

import android.app.Activity;
import android.content.Context;

import com.mobilegamebar.rsdk.outer.model.GameParamInfo;

/**
 * Created by Luoweiqiang 
 * 2015/05/18
 */

public interface ICoreManager   {
    /**
     * @param context
     */
     void init(Context context, boolean debugMode, GameParamInfo gameParamInfo,
               int orientation, IOperateCallback<String> sdkInitCallback, Activity activity);

     String getSession();

     long getUid();

     int getGameId();


    void uninit(IOperateCallback callback);

    void showFloatView();

    void hideFloatView();

}
