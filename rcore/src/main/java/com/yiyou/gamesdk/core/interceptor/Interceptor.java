package com.yiyou.gamesdk.core.interceptor;

/**
 * all interceptor implement from that
 * Created by shui on 4/11/16.
 */
public interface Interceptor<T> {

    void intercept(Chain<T> chain);

    interface Chain<T>{

        T getData();

        void proceed(T data);


    }
}
