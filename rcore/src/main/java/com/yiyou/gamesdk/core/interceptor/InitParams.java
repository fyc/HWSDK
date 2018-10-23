package com.yiyou.gamesdk.core.interceptor;

import android.app.Activity;
import android.content.Context;

import com.yiyou.gamesdk.model.GameUpdateInfo;
import com.mobilegamebar.rsdk.outer.IOperateCallback;
import com.mobilegamebar.rsdk.outer.model.GameParamInfo;

public class InitParams {
    GameUpdateInfo gameUpdateInfo;
    Context context;
    boolean debugMode;
    GameParamInfo gameParamInfo;
    int orientation;
    IOperateCallback<String> sdkInitCallback;
    Activity activity;

    public InitParams(Context context, boolean debugMode, GameParamInfo gameParamInfo, int orientation,
                      IOperateCallback<String> sdkInitCallback, Activity activity) {
        this.context = context;
        this.debugMode = debugMode;
        this.gameParamInfo = gameParamInfo;
        this.orientation = orientation;
        this.sdkInitCallback = sdkInitCallback;
        this.activity = activity;
    }

    public InitParams setGameUpdateInfo(GameUpdateInfo gameUpdateInfo) {
        this.gameUpdateInfo = gameUpdateInfo;
        return this;
    }

    public GameUpdateInfo getGameUpdateInfo() {
        return gameUpdateInfo;
    }

    public Context getContext() {
        return context;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public GameParamInfo getGameParamInfo() {
        return gameParamInfo;
    }

    public int getOrientation() {
        return orientation;
    }

    public IOperateCallback<String> getSdkInitCallback() {
        return sdkInitCallback;
    }

    public Activity getActivity() {
        return activity;
    }
}