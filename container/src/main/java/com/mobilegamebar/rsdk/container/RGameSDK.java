package com.mobilegamebar.rsdk.container;

import android.app.Activity;
import android.content.Context;

import com.mobilegamebar.rsdk.internal.LoadPlugin;
import com.mobilegamebar.rsdk.outer.ContextWrapper;
import com.mobilegamebar.rsdk.outer.IOperateCallback;
import com.mobilegamebar.rsdk.outer.IRSDK;
import com.mobilegamebar.rsdk.outer.model.GameParamInfo;
import com.mobilegamebar.rsdk.outer.model.PaymentInfo;
import com.mobilegamebar.rsdk.outer.model.VersionDir;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.mobilegamebar.rsdk.outer.util.StorageConfig;

import java.util.Map;

/**
 * Created by Win on 2017/4/13.
 */
public class RGameSDK implements IRSDK {

    private static RGameSDK instance = null;
    public static synchronized RGameSDK getInstance(){
        if (instance == null){
            instance = new RGameSDK();
        }
        return instance;
    }


    @Override
    public void attach(ContextWrapper contextWrapper, VersionDir versionDir) {

    }

    @Override
    public void init(Activity context, GameParamInfo info, boolean isDebug, int orientation, IOperateCallback<String> callback) {
        StorageConfig.prepare(context, isDebug);
        Log.init(context, Log.LogLevel.Verbose, getLogDir(context,info.getGameId()));
        LoadPlugin.getInstance().init(context,info,isDebug,orientation,callback);
    }

    @Override
    public void uninit(Context context, IOperateCallback<String> callback) {
        LoadPlugin.getInstance().uninit(context, callback);
    }

    @Override
    public void login(Activity activity, IOperateCallback<String> callback) {
        LoadPlugin.getInstance().login(activity,callback);
    }

    @Override
    public void setLogoutListener(IOperateCallback<String> callback) {
        LoadPlugin.getInstance().setLogoutListener(callback);
    }

    @Override
    public void logout() {
        LoadPlugin.getInstance().logout();
    }

    @Override
    public void showFloatView(Activity activity) {
        LoadPlugin.getInstance().showFloatView(activity);
    }

    @Override
    public void hideFloatView(Activity activity) {
        LoadPlugin.getInstance().hideFloatView(activity);
    }

    @Override
    public void pay(Activity activity, PaymentInfo payInfo, IOperateCallback<String> callback) {
        LoadPlugin.getInstance().pay(activity,payInfo,callback);
    }

    @Override
    public boolean isLogin() {
        return LoadPlugin.getInstance().isLogin();
    }

    @Override
    public String getGameId() {
        return LoadPlugin.getInstance().getGameId();
    }

    @Override
    public String getUid() {
        return LoadPlugin.getInstance().getUid();
    }

    @Override
    public String getSession() {
        return LoadPlugin.getInstance().getSession();
    }

    @Override
    public void submitGameRoleInfo(Activity activity, String type, String serverName, String roleID, String roleName, int roleLevel, String exInfo) {
        LoadPlugin.getInstance().submitGameRoleInfo(activity,type,serverName,roleID,roleName,roleLevel,exInfo);
    }

    @Override
    public void submitExtendData(Activity activity, Map<String, String> params) {
        LoadPlugin.getInstance().submitExtendData(activity,params);
    }

    private String getLogDir(Context context, int gameId) {
        return StorageConfig.getGameLogDirPath(gameId);
    }
}
