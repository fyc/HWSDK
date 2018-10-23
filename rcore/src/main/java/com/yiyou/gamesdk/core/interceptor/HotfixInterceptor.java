package com.yiyou.gamesdk.core.interceptor;

import com.yiyou.gamesdk.core.update.DefaultUpdateImpl;

/**
 * Created by shui on 4/11/16.
 */
public class HotfixInterceptor implements InitInterceptor {



    @Override
    public void intercept(Chain<InitParams> chain) {
        DefaultUpdateImpl iml = new DefaultUpdateImpl();
        iml.check();
        chain.proceed(chain.getData());
    }
}
