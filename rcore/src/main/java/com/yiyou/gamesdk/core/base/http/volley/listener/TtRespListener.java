package com.yiyou.gamesdk.core.base.http.volley.listener;

import android.text.TextUtils;

import com.mobilegamebar.rsdk.outer.util.Log;
import com.yiyou.gamesdk.util.ToastUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by chenshuide on 15/6/3.
 * 网络回调接口
 */
public class TtRespListener<T> {

    private static final String TAG = "RSDK:TtRespListener ";


    /**
     * 当网络结束时候回调此方法
     * 默认不做任何处理
     */
    public void onNetworkComplete(){
        //default do nothing
    }

    /**
     * 当网络成功时候回调此方法 默认不做任何处理
     * 此方法针对单个对象类型
     *
     * @param url 请求时候url
     * @param params 请求时候的参数
     * @param result 返回的类型
     */
    public void onNetSucc(String url,Map<String,String> params,T result){

    }

    /**
     *
     * 当网络成功时候回调此方法 默认不做任何处理
     * 此方法针对数组类型
     * @param url 请求时候url
     * @param params 请求时候的参数
     * @param result 返回的类型
     */
    public void onNetSucc(String url,Map<String,String> params,List<T> result){

    }


    /**
     * 当网络请求失败时候回调此方法(result 为非零情况) 这种情况为非网络类型错误 即能正确访问到服务器 并接受到数据返回
     * @param url 请求时候url
     * @param params 请求时候的参数
     * @param errno 错误码
     * @param errmsg 错误信息
     */
    public void onNetError(String url,Map<String,String> params,String errno,String errmsg){
        //默认以toast显示错误信息

        if (TextUtils.isEmpty(errno) && TextUtils.isEmpty(errmsg)) {
            return;
        }

        ToastUtils.showMsg(errno + "  " + errmsg);

    }

    /**
     * 网络类型错误 比如超时 无网络等
     *
     * @param errorNo 错误码
     * @param errmsg 错误信息
     */
    public void onFail(int errorNo,String errmsg){
        //默认以toast显示错误信息

        Log.d(TAG, "onFail errorNo =" + errorNo + "errmsg= " + errmsg);

        switch (errorNo){
            case 1000:
                errmsg = "请求失败，请稍后再试";
                break;

            case 1001:
                errmsg = "请求超时";

                break;
        }

        ToastUtils.showMsg(errorNo + "  " + errmsg);



    }



}
