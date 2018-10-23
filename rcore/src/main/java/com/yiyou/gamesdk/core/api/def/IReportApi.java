package com.yiyou.gamesdk.core.api.def;

import com.mobilegamebar.rsdk.outer.IOperateCallback;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;

import java.util.Map;

/**
 * Created by chenshuide on 15/6/8.
 */
public interface IReportApi extends IApiWrapping {

    void feedback(String content, TtRespListener<Void> callback);

    // 激活事件请求
    void reportActivate(IOperateCallback<Void> callback);

    void onLineEvent( IOperateCallback<Void> callback);

    void onCharacterEvent(Map<String, String> params, IOperateCallback<Void> callback);
}
