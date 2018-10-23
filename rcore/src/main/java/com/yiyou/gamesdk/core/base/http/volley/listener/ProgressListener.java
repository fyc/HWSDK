package com.yiyou.gamesdk.core.base.http.volley.listener;

/**
 * Created by chenshuide on 15/6/12.
 */
public interface ProgressListener {

    void onStart(long total);

    void onProgress(long curPos, long total);

    void onFinish(long total);

}
