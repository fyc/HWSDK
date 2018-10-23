package com.yiyou.gamesdk.core.interceptor;

import android.content.Context;

import com.mobilegamebar.rsdk.outer.IOperateCallback;
import com.mobilegamebar.rsdk.outer.consts.TTCodeDef;
import com.mobilegamebar.rsdk.outer.event.EventDispatcherAgent;
import com.mobilegamebar.rsdk.outer.event.IEventListener;
import com.mobilegamebar.rsdk.outer.model.GameParamInfo;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.consts.StatusCodeDef;
import com.yiyou.gamesdk.core.storage.StorageAgent;
import com.yiyou.gamesdk.core.storage.events.StorageEvent;
import com.yiyou.gamesdk.util.ToastUtils;

/**
 * 初始化数据库
 * Created by shui on 4/11/16.
 */
public class InitStorageInterceptor implements InitInterceptor {

    private static final String TAG = "RSDK:InitStorageInterceptor";

    @Override
    public void intercept(final Chain<InitParams> chain) {

        InitParams data = chain.getData();
        final GameParamInfo gameParamInfo = data.getGameParamInfo();
        final Context mContext = data.getContext();

        final IOperateCallback<String> sdkInitCallback = data.getSdkInitCallback();


        IEventListener<Integer> dbPreListener = new IEventListener<Integer>() {
            @Override
            public void onEvent(String s, Integer code) {
                if (code == StatusCodeDef.SUCCESS) {

                    ApiFacade.preload();
                    ApiFacade.getInstance().setCurrentGameID(peekGameId(gameParamInfo));
                    ApiFacade.getInstance().setSdkKey(gameParamInfo.getSdkKey());
                    ApiFacade.getInstance().setupChannelInfo();

                    chain.proceed(chain.getData());


                } else {

                    sdkInitCallback.onResult(StatusCodeDef.FAIL_INIT_SDK_DB_OPEN_ERROR,
                            mContext.getResources().getString(R.string.fail_init_sdk_db_error));
                }
            }
        };

        EventDispatcherAgent.defaultAgent().addEventListener(InitStorageInterceptor.this,
                StorageEvent.TYPE_ALL_DB_PREPARED, dbPreListener);
        int sdkGameId = peekGameId(gameParamInfo);
        if (sdkGameId == 0) {
            sdkInitCallback.onResult(TTCodeDef.ERROR_INIT_INVALID_GAME_ID,
                    mContext.getResources().getString(R.string.error_init_invaild_gameid));
            return;
        }

        StorageAgent.init(mContext.getApplicationContext(), sdkGameId);

    }


    /*
     *使用GameParamInfo传递的gameId
     * @param paramInfo 初始化游戏参数
     * @return 游戏id
     */
    private int peekGameId(GameParamInfo paramInfo) {
        int gameId = paramInfo.getGameId();
        if (gameId == 0) {
            ToastUtils.showMsg("no available game id.");
        } else {
            Log.d(TAG, "peek game id " + gameId);
        }
        return gameId;
    }

}

