package com.yiyou.gamesdk.core.api.impl;


import com.mobilegamebar.rsdk.outer.IOperateCallback;
import com.yiyou.gamesdk.core.CoreManager;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.api.def.IReportApi;
import com.yiyou.gamesdk.core.base.http.RequestHelper;
import com.yiyou.gamesdk.core.base.http.RequestManager;
import com.yiyou.gamesdk.core.base.http.utils.Urlpath;
import com.yiyou.gamesdk.core.base.http.volley.HwRequest;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.yiyou.gamesdk.util.Base64;
import com.yiyou.gamesdk.util.TaoziSignUtils;
import com.yiyou.gamesdk.util.TimeUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPOutputStream;

/**
 * Created by levyyoung on 15/6/8.R
 * 上报游戏、角色信息
 */
class ReportManager implements IReportApi {

    private final static String TAG = "RSDK: " + ReportManager.class.getSimpleName();

    private final static String TYPE_ACTIVATE = "ACTIVATE";
    private final static String TYPE_ONLINE = "ONLINE";
    private final static String TYPE_CHARACTER = "CHARACTER";

    @Override
    public void feedback(String content, TtRespListener<Void> callback) {
        Map<String, String> params = new TreeMap<>();
        params.put("content", content);
        RequestHelper.buildParamsWithBaseInfo(params);
        HwRequest request = new HwRequest<>(Urlpath.FEEDBACK,params,null,callback);
        RequestManager.getInstance(CoreManager.getContext()).addRequest(request,null);
    }

    // 激活事件请求
    @Override
    public void reportActivate(final IOperateCallback<Void> callback) {
        Map<String, String> params = new TreeMap<>();
        params.put("type", TYPE_ACTIVATE);
        params.put("ctime", TimeUtils.formatNowTime());


        RequestHelper.buildParamsWithBaseInfo(params);

    }


    /**
     * 上报在线
     *
     * @param callback 回调接口
     */
    @Override
    public void onLineEvent(final IOperateCallback<Void> callback) {
        if (!ApiFacade.getInstance().isLogin()){
            return;
        }
        Map<String,String> params = new HashMap<>();

        params.put("timestamp", String.valueOf(new Date().getTime()));
        params.put("onlineTime", "600");
        RequestHelper.buildParamsWithBaseInfo(params);
        TaoziSignUtils.addSign(params);

        HwRequest request = new HwRequest<>(Urlpath.REPORT, params, Void.class, new TtRespListener<Void>() {
            @Override
            public void onNetSucc(String url, Map<String, String> params, Void result) {
                if (callback != null)
                    callback.onResult(0, result);
            }

            @Override
            public void onNetError(String url, Map<String, String> params, String errno, String errmsg) {
                if (CoreManager.isDebug()) {
                    super.onNetError(url, params, errno, errmsg);
                }
                if (callback != null)
                    callback.onResult(0, null);
            }

            @Override
            public void onFail(int errorNo, String errmsg) {
                if (CoreManager.isDebug()) {
                    super.onFail(errorNo, errmsg);
                }
                if (callback != null)
                    callback.onResult(errorNo, null);
            }
        });

        RequestManager.getInstance(CoreManager.getContext()).addRequest(request, null);

    }

    /**
     * 上报英雄属性
     *
     * @param params   参数
     * @param callback callback
     */
    @Override
    public void onCharacterEvent(Map<String, String> params, final IOperateCallback<Void> callback) {
        if (params == null)
            return;
        params.put("type", TYPE_CHARACTER);
        params.put("ctime", TimeUtils.formatNowTime());
        RequestHelper.buildParamsWithBaseInfo(params);

//        TtRequest request = new TtRequest<>(Urlpath.REPORT, params, Void.class, new TtRespListener<Void>() {
//            @Override
//            public void onNetSucc(String url, Map<String, String> params, Void result) {
//                if (callback != null)
//                    callback.onResult(0, result);
//            }
//
//            @Override
//            public void onNetError(String url, Map<String, String> params, String errno, String errmsg) {
//                if (CoreManager.isDebug()) {
//                    super.onNetError(url, params, errno, errmsg);
//                }
//                //fixme
//                if (callback != null)
//                    callback.onResult(0, null);
//            }
//
//            @Override
//            public void onFail(int errorNo, String errmsg) {
//                if (CoreManager.isDebug()) {
//                    super.onFail(errorNo, errmsg);
//                }
//                if (callback != null)
//                    callback.onResult(errorNo, null);
//            }
//        });
//
//        RequestManager.getInstance(CoreManager.getContext()).addRequest(request, null);

    }

    private String getFileContent(File file) {
        try {

            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(os);

            int len = fis.available();
            byte[] data = new byte[len];
            fis.read(data, 0, len);
            gzip.write(data);

            gzip.flush();
            gzip.close();
            fis.close();

            return Base64.encode(os.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";

    }

}
