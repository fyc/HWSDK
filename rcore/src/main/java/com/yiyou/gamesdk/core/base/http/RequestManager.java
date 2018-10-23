package com.yiyou.gamesdk.core.base.http;

import android.content.Context;
import android.util.Log;

import com.android.volley1.Request;
import com.android.volley1.RequestQueue;
import com.android.volley1.toolbox.ImageLoader;
import com.android.volley1.toolbox.Volley;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.CoreManager;
import com.yiyou.gamesdk.core.base.http.utils.HttpUtils;
import com.yiyou.gamesdk.core.base.http.volley.cache.LruBitmapCache;
import com.yiyou.gamesdk.core.ui.fragment.BaseFragment;
import com.yiyou.gamesdk.util.ToastUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenshuide on 15/6/4.
 */
public class RequestManager {

    private static final String TAG = "RSDK:RequestManager";
    public static final int FIVE_SECOND = 5 * 1000;
    private static RequestManager instance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private Map<String, Long> requests = new HashMap<>();// unique id   request time

    private Map<String, Integer> failRequests = new HashMap<>();//unique id  http code 5xx

    private RequestManager(Context context) {
        mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        mImageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache());
    }


    public synchronized static RequestManager getInstance(Context context) {
        if (instance == null)
            instance = new RequestManager(context);
        return instance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public void addFailRequests(String uniqueId, int httpCode) {
        failRequests.put(uniqueId, httpCode);
    }



    public void addRequestTime(String unique) {
        requests.put(unique, System.currentTimeMillis());

    }


    /**
     * add request to RequestQueue
     *
     * @param request see{@link com.yiyou.gamesdk.core.base.http.volley.TtRequest}
     * @param tag     设置tag到请求，当需要取消改请求时候 可以根据该tag {@link BaseFragment#getVolleyTag()}  }
     */
    public void addRequest(Request request, Object tag) {
        if (request == null)
            return;

        if (!HttpUtils.isConnect(CoreManager.getContext())) {
            ToastUtils.showMsg(R.string.no_connection);
        }

        if (tag != null) {
            request.setTag(tag);
        }


        String uniqueId = request.getUniqueId();

        if (failRequests.containsKey(uniqueId)) {

            long curTime = System.currentTimeMillis();

            Long aLong = requests.get(uniqueId);
            if (aLong != null) {
                long space = curTime - aLong;

                if (space < FIVE_SECOND) {
                    Log.d(TAG,"服务器繁忙,请稍后再试  "+request.getUrl());
                    return;
                }
            }

            failRequests.remove(uniqueId);
            requests.remove(uniqueId);

        }


        getRequestQueue().add(request);
    }


    public void cancelRequest(Object tag) {
        if (tag == null) {
            return;
        }
        getRequestQueue().cancelAll(tag);
    }


    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
