package com.yiyou.gamesdk.core;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.mobilegamebar.rsdk.outer.ICoreManager;
import com.mobilegamebar.rsdk.outer.IOperateCallback;
import com.mobilegamebar.rsdk.outer.event.EventDispatcherAgent;
import com.mobilegamebar.rsdk.outer.model.GameParamInfo;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.api.impl.payment.PaymentAdapter;
import com.yiyou.gamesdk.core.consts.StatusCodeDef;
import com.yiyou.gamesdk.core.exception.SDKUncaughtExceptionHandler;
import com.yiyou.gamesdk.core.interceptor.DismissSplashInterceptor;
import com.yiyou.gamesdk.core.interceptor.ForceInterceptor;
import com.yiyou.gamesdk.core.interceptor.HotfixInterceptor;
import com.yiyou.gamesdk.core.interceptor.InitInterceptor;
import com.yiyou.gamesdk.core.interceptor.InitParams;
import com.yiyou.gamesdk.core.interceptor.InitStorageInterceptor;
import com.yiyou.gamesdk.core.interceptor.Interceptor;
import com.yiyou.gamesdk.core.interceptor.NoticeInterceptor;
import com.yiyou.gamesdk.core.interceptor.ShowForceDialogInterceptor;
import com.yiyou.gamesdk.core.storage.StorageAgent;
import com.yiyou.gamesdk.core.storage.sharepref.Constant;
import com.yiyou.gamesdk.core.ui.dialog.ViewControllerNavigator;
import com.yiyou.gamesdk.core.ui.floatview.AnnouncementManager;
import com.yiyou.gamesdk.core.ui.floatview.FloatViewManager;
import com.yiyou.gamesdk.core.ui.widget.dialog.CommDialog;
import com.yiyou.gamesdk.core.ui.widget.dialog.SplashDialogView;
import com.yiyou.gamesdk.util.DownloadBridge;
import com.yiyou.gamesdk.util.PermissionHelper;
import com.yiyou.gamesdk.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by levyyoung on 15/5/15.
 */
public class CoreManager implements ICoreManager {

    private static Context mContext;//todo 注意这个指向Service

    protected static final String TAG = "RSDK:CoreManager ";

    private static boolean isDebugMode = false;
    private InitParams mInitParams;

    public static boolean isDebug() {
        return isDebugMode;
    }

    public static Context getContext() {
        return mContext;
    }

    private final static int MSG_EXIT = 1;

    private MyHandler myHandler;

    public static final int REQUEST_CODE_PERMISSION = 100;



    private static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case MSG_EXIT:
                    Log.d(TAG, " msg to exit");
                    Object callback = msg.obj;
                    if (callback != null && callback instanceof IOperateCallback) {
                        IOperateCallback<String> iOperateCallback = (IOperateCallback) callback;
                        iOperateCallback.onResult(StatusCodeDef.SUCCESS, "");
                    }
                    break;
            }
        }
    }
    @Override
    public void init(Context context, boolean debugMode, final @NonNull GameParamInfo gameParamInfo,
                     int orientation, @NonNull final IOperateCallback<String> sdkInitCallback, final Activity activity) {

        mInitParams = new InitParams(context, debugMode, gameParamInfo, orientation, sdkInitCallback, activity);
        initEvent();
        myHandler = new MyHandler();
        mContext = context;
        isDebugMode = debugMode;
        Log.d(TAG, "init  " + debugMode + " gameinfo =" + gameParamInfo + " orientation = " + orientation);
        /**
         * 捕获异常
         */
        new SDKUncaughtExceptionHandler();
        doChains(activity);
    }

    private void doChains(Activity activity) {
        WindowManager manager = activity.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        Log.d(TAG,"Create SplashDialog: " + activity.getWindowManager().getDefaultDisplay().getWidth()+" X "+ activity.getWindowManager().getDefaultDisplay().getHeight());
        Log.d(TAG,"Create SplashDialog new: " + width + " X " + height);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width,height);
        CommDialog.Builder builder = new CommDialog.Builder(activity);
        builder.setView(new SplashDialogView(),params);
        builder.setFullScreen(true);
        CommDialog splashDialog = builder.create();
        splashDialog.show();

        List<Interceptor> interceptors = new ArrayList<>();
        interceptors.add(new InitStorageInterceptor());//初始化存储
        interceptors.add(new HotfixInterceptor()); //热更检查
        interceptors.add(new ForceInterceptor());// 游戏强更检查
        interceptors.add(new DismissSplashInterceptor(splashDialog));// 关闭闪屏
        interceptors.add(new NoticeInterceptor());//运维公告
        interceptors.add(new ShowForceDialogInterceptor()); //显示强更提示框
        PermissionHelper.requestNecessaryPermission(activity, REQUEST_CODE_PERMISSION);
        new InitChainImpl(interceptors, 0, mInitParams).proceed(mInitParams);
    }

    private class InitChainImpl implements Interceptor.Chain<InitParams> {
        private int index;
        private InitParams initParams;
        private List<Interceptor> interceptors;

        public InitChainImpl(List<Interceptor> interceptors, int index, InitParams initParams) {
            this.interceptors = interceptors;
            this.index = index;
            this.initParams = initParams;
        }

        @Override
        public InitParams getData() {
            return initParams;
        }

        @Override
        public void proceed(InitParams params) {

            if (index < interceptors.size()) {

                InitChainImpl iml = new InitChainImpl(interceptors, index + 1, params);
                InitInterceptor interceptor = (InitInterceptor) interceptors.get(index);
                interceptor.intercept(iml);

            } else {
//                reportActiveEvent();
                IOperateCallback<String> sdkInitCallback = params.getSdkInitCallback();
                mInitParams = null;
                sdkInitCallback.onResult(0, "sdk init 成功");
            }
        }
    }
    /**
     * 上报激活事件
     */
    private void reportActiveEvent() {
        SharedPreferences preferences = mContext.getApplicationContext()
                .getSharedPreferences(Constant.KEY_DB_NAME, Context.MODE_PRIVATE);

        boolean isFirst = preferences.getBoolean(Constant.KEY_FIRST_OPEN_APP, true);

        String gameid = preferences.getString(Constant.KEY_GAME_ID, "");

        String cur_gameid = ApiFacade.getInstance().getCurrentGameID() + "";

        if (gameid.equals(cur_gameid) && !isFirst && !"0".equals(cur_gameid)) {
            return;
        }

        ApiFacade.getInstance().reportActivate(null);
        preferences.edit().putBoolean(Constant.KEY_FIRST_OPEN_APP, false).apply();
        preferences.edit().putString(Constant.KEY_GAME_ID, cur_gameid).apply();
        Log.d(TAG, "report success!!");
    }

    @Override
    public String getSession() {
        return ApiFacade.getInstance().getSession();
    }

    @Override
    public long getUid() {
        return ApiFacade.getInstance().getSubUid();
    }

    @Override
    public int getGameId() {
        return ApiFacade.getInstance().getCurrentGameID();
    }


    @Override
    public void uninit(IOperateCallback iOperateCallback) {
        uninit();
        sendMesToExit(iOperateCallback);
    }

    @Override
    public void showFloatView() {
        if (getUid() != 0){
            FloatViewManager.getInstance().show();
        }
    }

    @Override
    public void hideFloatView() {
//        if (getUid() != 0){
            FloatViewManager.getInstance().hide();
        AnnouncementManager.getInstance().uninit();
//        }
    }

    public void uninit() {
        if (ApiFacade.getInstance().isLogin()){
            ApiFacade.getInstance().logout();
        }
        AnnouncementManager.getInstance().uninit();
        ViewControllerNavigator.getInstance().uninit();
        FloatViewManager.getInstance().destory();
        StorageAgent.uninit();
        PaymentAdapter.destroy();
        DownloadBridge.getInstance().uninit();
        ToastUtils.dismiss();
        mContext = null;
    }

    private void initEvent() {
        EventDispatcherAgent eventDispatcherAgent = EventDispatcherAgent.defaultAgent();
    }

    private void sendMesToExit(IOperateCallback<String> callback) {
        if (callback == null) {
            return;
        }
        Log.d(TAG, "send msg to exit");
        Message msg = Message.obtain();
        msg.what = MSG_EXIT;
        msg.obj = callback;
        myHandler.sendMessage(msg);
    }
}
