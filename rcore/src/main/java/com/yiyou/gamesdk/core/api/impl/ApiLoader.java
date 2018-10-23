package com.yiyou.gamesdk.core.api.impl;

import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;

import com.yiyou.gamesdk.core.api.def.IAccountHistoryApi;
import com.yiyou.gamesdk.core.api.def.IAnnouncementApi;
import com.yiyou.gamesdk.core.api.def.IApiWrapping;
import com.yiyou.gamesdk.core.api.def.IAuthApi;
import com.yiyou.gamesdk.core.api.def.IChannelApi;
import com.yiyou.gamesdk.core.api.def.IChildrenAccountHistoryApi;
import com.yiyou.gamesdk.core.api.def.IPaymentApi;
import com.yiyou.gamesdk.core.api.def.IReportApi;
import com.yiyou.gamesdk.core.api.def.ISecurityApi;
import com.yiyou.gamesdk.core.api.def.IUpgradeApi;
import com.mobilegamebar.rsdk.outer.util.Log;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by levyyoung on 15/6/10.
 */
public class ApiLoader {

    private static final String TAG = "RSDK:ApiLoader ";

    /**
     * 预加载结果缓存
     */
    private static final Map<Class<? extends IApiWrapping>, IApiWrapping> preloadInstances
            = new ArrayMap<>();

    /**
     * 定义接口与实现的映射
     */
    private static final Map<Class<?>, Class<? extends IApiWrapping>> API_IMPL_MAPPING =
            new ArrayMap<Class<?>, Class<? extends IApiWrapping>>(){
                {
                    put(IAuthApi.class, AuthManager.class);
                    put(IReportApi.class, ReportManager.class);
                    put(ISecurityApi.class, SecurityManager.class);
                    put(IUpgradeApi.class, UpgradeManager.class);
                    put(IChildrenAccountHistoryApi.class, ChildrenAccountHistoryManager.class);
                    put(IChannelApi.class, ChannelManager.class);
                    put(IAccountHistoryApi.class, AccountHistoryManager.class);
                    put(IPaymentApi.class, PaymentManager.class);
                    put(IAnnouncementApi.class, AnnouncementManager.class);
                }
            };

    /**
     * 预加载接口列表
     */
    @SuppressWarnings("unchecked")
    private static final Class<? extends IApiWrapping>[] PreloadClassList = new Class[]{
            IChannelApi.class, IAuthApi.class, IPaymentApi.class,IChildrenAccountHistoryApi.class,IAccountHistoryApi.class
    };

    /**
     * 执行预加载
     */
    @SuppressWarnings("unchecked")
    public static void preload() {
        synchronized (preloadInstances) {
            for (Class<? extends IApiWrapping> clz : PreloadClassList) {
                preloadInstances.put(clz, generateApiInstance(clz));
            }
            Log.d(TAG, "preload api : " + Arrays.toString(PreloadClassList));
        }
    }

    /**
     * 从预加载结果中获取 Api 实例
     * @param instanceKey API 类
     * @return API 实例，可能为空
     */
    private static @Nullable
    IApiWrapping getApiInstanceFromPreLoad(Class<? extends IApiWrapping> instanceKey) {
        return preloadInstances.remove(instanceKey);
    }

    /**
     * 创建一个Api实例
     * @param instanceKey API接口Class
     * @return Api实例
     */
    private static IApiWrapping generateApiInstance(Class<? extends IApiWrapping> instanceKey) {
        Class<? extends IApiWrapping> clz = API_IMPL_MAPPING.get(instanceKey);
        if (clz != null) {
            try {
                return clz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("Api not register in API_IMPL_MAPPING");
        }
        return null;
    }

    /**
     * 从预加载中获取 Api 实例，如果失败则创建一个该Api的新实例
     * @param instanceKey API接口Class
     * @return Api实例
     */
    public synchronized static IApiWrapping getApiInstance(Class<? extends IApiWrapping> instanceKey) {
        String apiName = instanceKey.getSimpleName();
        IApiWrapping instance = ApiLoader.getApiInstanceFromPreLoad(instanceKey);;
        if (instance == null) {
            Log.d(TAG, "init " + apiName);
            //该 API 没有进行预加载， 创建一个新实例
            instance = ApiLoader.generateApiInstance(instanceKey);
        } else {
            Log.d(TAG, apiName + " hit in preload.");
        }
        return instance;
    }

}
