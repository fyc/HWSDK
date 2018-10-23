package com.yiyou.gamesdk.core.interceptor;

import android.content.Context;

import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.yiyou.gamesdk.model.GameUpdateInfo;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.yiyou.gamesdk.util.PackageUtil;

import java.util.Map;

/**
 * 强更拦截器
 * Created by shui on 4/11/16.
 */
public class ForceInterceptor implements InitInterceptor {

    private static final String TAG = "TTSDK: ForceInterceptor";

    @Override
    public void intercept(Chain<InitParams> chain) {

        forceUpgradeCheck(chain);

    }


    private void forceUpgradeCheck(final Chain<InitParams> chain) {

        Context activity = chain.getData().getActivity();

        Log.d(TAG, "begin check force upgrade");

        ApiFacade instance = ApiFacade.getInstance();
        instance.upgradeRequest("", instance.getCurrentGameID(), PackageUtil.getVersionName(activity),
                PackageUtil.getVersionCode(activity) + "", new TtRespListener<GameUpdateInfo>() {

                    @Override
                    public void onNetSucc(String url, Map<String, String> params, GameUpdateInfo result) {

                        chain.proceed(chain.getData().setGameUpdateInfo(result));



                    }

                    @Override
                    public void onFail(int errorNo, String errmsg) {
                        Log.d(TAG, "requestUpgrade" + errmsg);
                        chain.proceed(chain.getData());
                    }


                    @Override
                    public void onNetError(String url, Map<String, String> params, String errno, String errmsg) {
                        Log.d(TAG, "requestUpgrade" + errmsg);
                        chain.proceed(chain.getData());

                    }
                });
    }
}
