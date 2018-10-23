package com.yiyou.gamesdk.core.interceptor;

/**
 * CoreManager init interceptor  all init interceptor implement from this
 * Created by shui on 4/11/16.
 */
public interface InitInterceptor extends Interceptor<InitParams> {

    @Override
    void intercept(Chain<InitParams> chain);
}
