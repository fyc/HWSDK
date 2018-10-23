package com.yiyou.gamesdk.core.api.impl;


import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import com.yiyou.gamesdk.core.CoreManager;
import com.yiyou.gamesdk.core.api.def.IUpgradeApi;
import com.yiyou.gamesdk.core.base.http.RequestHelper;
import com.yiyou.gamesdk.core.base.http.RequestManager;
import com.yiyou.gamesdk.core.base.http.utils.Urlpath;
import com.yiyou.gamesdk.core.base.http.volley.FileDownLoadRequest;
import com.yiyou.gamesdk.core.base.http.volley.HwRequest;
import com.yiyou.gamesdk.core.base.http.volley.listener.FileDownListener;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.yiyou.gamesdk.model.GameUpdateInfo;
import com.yiyou.gamesdk.model.PatchUpdateBean;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.mobilegamebar.rsdk.outer.util.StringUtils;

import java.io.File;
import java.util.Map;

/**
 * Created by levyyoung on 15/6/8.
 * 检查游戏升级
 */
class UpgradeManager implements IUpgradeApi {
	
	private static final String TAG = "RSDK: "+"[IUpgradeApi]";

	@Override
	public void upgradeRequest(String cpId, int gameId,String versionName,String versionCode ,
			final TtRespListener<GameUpdateInfo> lisenter) {
		if (StringUtils.isBlank(cpId)) {
            Log.e(TAG, "Error reqUpGrade. cpId is null or empty");
        }
        if (StringUtils.isBlank(String.valueOf(gameId))) {
            Log.e(TAG, "Error reqUpGrade. gameID is null or empty");
        }
		Map<String, String> params = new ArrayMap<>();
        RequestHelper.buildParamsWithBaseInfo(params);
        params.put("versionName", versionName) ;
        params.put("versionCode", versionCode) ;
        HwRequest gameUpdateCheck = new HwRequest(Urlpath.GAME_UPDATE_CHECK, params, GameUpdateInfo.class, lisenter) ;

        Log.d(TAG, "Enqueue reqUpGrade req.");

        RequestManager.getInstance(CoreManager.getContext()).addRequest(gameUpdateCheck,null);
        
	}

    @Override
    public void updateCheck(@NonNull Map<String, String> params, TtRespListener<PatchUpdateBean> listener) {
        Log.e(TAG, "hitfix  check");
        RequestHelper.buildParamsWithBaseInfo(params);
        HwRequest request = new HwRequest<>(Urlpath.UPDATE_CHECK,params,PatchUpdateBean.class,listener);
        RequestManager.getInstance(CoreManager.getContext()).addRequest(request, null);
    }


    @Override
    public void downLoadFile(String url, File saveFile, String fileName, String md5, FileDownListener listener) {

        FileDownLoadRequest request = new FileDownLoadRequest(url, saveFile, fileName, md5, listener);
        RequestManager.getInstance(CoreManager.getContext()).addRequest(request, null);
    }

}
