package com.yiyou.gamesdk.rcore;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;

import com.mobilegamebar.rsdk.outer.ContextWrapper;
import com.mobilegamebar.rsdk.outer.IOperateCallback;
import com.mobilegamebar.rsdk.outer.IRSDK;
import com.mobilegamebar.rsdk.outer.RSDKSpace;
import com.mobilegamebar.rsdk.outer.event.AuthEvent;
import com.mobilegamebar.rsdk.outer.event.EventDispatcherAgent;
import com.mobilegamebar.rsdk.outer.model.GameParamInfo;
import com.mobilegamebar.rsdk.outer.model.PaymentInfo;
import com.mobilegamebar.rsdk.outer.model.VersionDir;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.yiyou.gamesdk.PluginManager;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.interceptor.Interceptor;
import com.yiyou.gamesdk.core.interceptor.LoginInterceptor;
import com.yiyou.gamesdk.core.interceptor.LoginNoticeInterceptor;
import com.yiyou.gamesdk.core.ui.dialog.ViewControllerNavigator;
import com.yiyou.gamesdk.core.ui.floatview.FloatViewManager;
import com.yiyou.gamesdk.core.ui.widget.ExitAlertDialogView;
import com.yiyou.gamesdk.model.AccountHistoryInfo;
import com.yiyou.gamesdk.util.PermissionHelper;
import com.yiyou.gamesdk.util.ToastUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Win on 2017/4/13.
 */
public class RSDKImpl implements IRSDK{

    private static final String TAG = "RSDK:RSDKImpl";
    private IOperateCallback<String> logoutCallback;

    @Override
    public void attach(ContextWrapper contextWrapper ,VersionDir versionDir) {
        PluginManager.getInstance().attach(contextWrapper,versionDir);
    }

    @Override
    public void init(Activity context, GameParamInfo rGameInfo, boolean debugMode, int orientation, IOperateCallback<String> rsdkCallback) {
        PluginManager.getInstance().init(context,debugMode,rGameInfo,orientation,rsdkCallback);
    }

    @Override
    public void uninit(final Context context, final IOperateCallback<String> rsdkCallback) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ExitAlertDialogView alertDialogView = ViewControllerNavigator.getInstance().toExitAlertDialogView(context);
                alertDialogView.setMessageTip(R.string.confirm_exit);
                alertDialogView.setEnsureText(R.string.exit);
                alertDialogView.setListener(new ExitAlertDialogView.DialogClickListener() {
                    @Override
                    public void onEnsureClick() {
                        RSDKSpace.getInstance(context.getApplicationContext()).uninit();
                        exit();
                        PluginManager.getInstance().uninit(rsdkCallback);
                    }
                });
            }
        });

    }

    private void exit() {
        Log.d(TAG, "exit");
        EventDispatcherAgent.defaultAgent().removeAllEventListeners();
        try {
            Field sConstructorMapField = LayoutInflater.class.getDeclaredField("sConstructorMap");
            sConstructorMapField.setAccessible(true);
            Object o = sConstructorMapField.get(null);
            if (o != null && o instanceof HashMap) {
                HashMap sConstructorMap = (HashMap) o;
                sConstructorMap.clear();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        logoutCallback = null;
    }

    @Override
    public void login(Activity activity, final IOperateCallback<String> callback) {

        if (!PermissionHelper.Storage.hasStoragePermission(activity)){
            ToastUtils.showMsg("未获得文件存储权限，请前往权限管理打开。");
            callback.onResult(1,"登录失败");
            return;
        }
        ApiFacade.getInstance().setupChannelInfo();

        final AuthEvent.LoginParams loginParams = new AuthEvent.LoginParams(activity,callback,"RSDK");
        final AuthEvent.LoginParams loginParamsProxy = new AuthEvent.LoginParams(activity,
                new IOperateCallback<String>() {
                    @Override
                    public void onResult(int i, String s) {
                        List<Interceptor> interceptors = new ArrayList<>();
                        interceptors.add(new LoginNoticeInterceptor());
                        LoginInterceptor.LoginParams params = new LoginInterceptor.LoginParams(loginParams, i, s);
                        LoginChain loginChain = new LoginChain(params, 0, interceptors);
                        loginChain.proceed(params);
                    }
                }, "RSDK");
        FloatViewManager.getInstance().hide();
        if(isHasAccountInLocal()){
            ViewControllerNavigator.getInstance().toLogin( loginParamsProxy);
        }else{
            ViewControllerNavigator.getInstance().toRegister(loginParamsProxy);
        }
    }

    @Override
    public void setLogoutListener(IOperateCallback<String> rsdkCallback) {
        logoutCallback = rsdkCallback;
        PluginManager.getInstance().setLogoutCallback(rsdkCallback);
    }

    @Override
    public void logout() {
        if (logoutCallback==null){
            ApiFacade.getInstance().logout();
        }else{
            ApiFacade.getInstance().logout(logoutCallback);
        }
    }

    @Override
    public void showFloatView(Activity activity) {
        PluginManager.getInstance().showFloatView();
    }

    @Override
    public void hideFloatView(Activity activity) {
        PluginManager.getInstance().hideFloatview();
    }

    @Override
    public void pay(Activity activity, PaymentInfo payOrderInfo, IOperateCallback<String> rsdkCallback) {
        ApiFacade.getInstance().order(payOrderInfo, activity, rsdkCallback);
    }

    @Override
    public boolean isLogin() {
        return ApiFacade.getInstance().isLogin();
    }

    @Override
    public String getGameId() {
        return PluginManager.getInstance().getGameId()+"";
    }

    @Override
    public String getUid() {
        return PluginManager.getInstance().getUid()+"";
    }

    @Override
    public String getSession() {
        return PluginManager.getInstance().getSession();
    }

    @Override
    public void submitGameRoleInfo(Activity activity, String type, String serverName, String roleID, String roleName, int roleLevel, String exInfo) {
        Map<String, String> params = new TreeMap<>();
        params.put("serverName", serverName);
        params.put("roleID", roleID);
        params.put("roleName", roleName);
        params.put("level", String.valueOf(roleLevel));
        ApiFacade.getInstance().onCharacterEvent(params, null);

    }

    @Override
    public void submitExtendData(Activity activity, Map<String, String> map) {
        ApiFacade.getInstance().onCharacterEvent(map, null);
    }

    private boolean isHasAccountInLocal(){
        List<AccountHistoryInfo> AccountHistories = ApiFacade.getInstance().getAccountHistories();
        if(AccountHistories == null){
            return false;
        }
        if(AccountHistories.isEmpty()){
            return false;
        }
        return true;
    }

    private class LoginChain implements Interceptor.Chain<LoginInterceptor.LoginParams> {

        private LoginInterceptor.LoginParams params;
        private int index;
        private List<Interceptor> interceptors;

        public LoginChain(final LoginInterceptor.LoginParams params, int index, List<Interceptor> interceptors) {
            this.params = params;
            this.index = index;
            this.interceptors = interceptors;
        }

        @Override
        public LoginInterceptor.LoginParams getData() {
            return params;
        }

        @Override
        public void proceed(LoginInterceptor.LoginParams data) {

            if (index < interceptors.size()) {
                LoginChain loginChain = new LoginChain(data, index + 1, interceptors);
                LoginInterceptor interceptor = (LoginInterceptor) interceptors.get(index);
                interceptor.intercept(loginChain);
            } else {
                data.getLoginParams().callback.onResult(data.getCode(), data.getResult());
            }
        }
    }
}
