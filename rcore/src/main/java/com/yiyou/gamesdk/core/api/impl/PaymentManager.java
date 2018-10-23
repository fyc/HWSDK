package com.yiyou.gamesdk.core.api.impl;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;

import com.google.gson1.Gson;
import com.mobilegamebar.rsdk.outer.IOperateCallback;
import com.mobilegamebar.rsdk.outer.consts.TTCodeDef;
import com.mobilegamebar.rsdk.outer.event.BackToMainFragmentEvent;
import com.mobilegamebar.rsdk.outer.event.EventDispatcherAgent;
import com.mobilegamebar.rsdk.outer.event.StartActivityEvent;
import com.mobilegamebar.rsdk.outer.model.PaymentInfo;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.mobilegamebar.rsdk.outer.util.StringUtils;
import com.yiyou.gamesdk.core.CoreManager;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.api.def.IPaymentApi;
import com.yiyou.gamesdk.core.api.impl.payment.PaymentAdapter;
import com.yiyou.gamesdk.core.base.http.RequestHelper;
import com.yiyou.gamesdk.core.base.http.RequestManager;
import com.yiyou.gamesdk.core.base.http.utils.HttpUtils;
import com.yiyou.gamesdk.core.base.http.utils.Urlpath;
import com.yiyou.gamesdk.core.base.http.volley.HwJsonResquest;
import com.yiyou.gamesdk.core.base.http.volley.HwRequest;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.yiyou.gamesdk.core.consts.StatusCodeDef;
import com.yiyou.gamesdk.core.ui.fragment.PayCenterFragment;
import com.yiyou.gamesdk.core.ui.fragment.PayFragment;
import com.yiyou.gamesdk.model.InternalOrderInfo;
import com.yiyou.gamesdk.model.PayInfo;
import com.yiyou.gamesdk.util.ExtraDef;
import com.yiyou.gamesdk.util.ToastUtils;
import com.yiyou.gamesdk.util.VersionUtil;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Created by levyyoung on 15/6/8.
 * 发起订单
 */
public class PaymentManager implements IPaymentApi{

    private static final String TAG = "RSDK:PaymentManager";

    private class OrderCache{
        public IOperateCallback<String> callback = null;
        public String cpOrderId = null;

        public OrderCache() {
        }

        public void clear() {
            callback = null;
            cpOrderId = null;
        }

        public boolean isEmpty() {
            return callback == null && cpOrderId == null;
        }

    }
    private final OrderCache orderCache = new OrderCache();
    private static final String PAYMENT_WEB_CLZ = PayFragment.class.getName();
    private WeakReference<Activity> paymentActivityHolder;

    @Override
    public synchronized void order(PaymentInfo paymentInfo, @Nullable Activity startUpActivity, IOperateCallback<String> callback) {

        if (StringUtils.isBlank(paymentInfo.getCpOrderId())) {
            Log.e(TAG, "Error order. invalid cp order id.");
            return;
        }

        //break by old order
        if (!orderCache.isEmpty()) {
            ToastUtils.showMsg("请勿重复操作.");
            return;
        }
        //update order cache
        orderCache.callback = callback;
        orderCache.cpOrderId = paymentInfo.getCpOrderId();

        Map<String, String> params = new ArrayMap<>();
        RequestHelper.buildParamsWithBaseInfo(params);
        String sdk_version = VersionUtil.getSdkVersion();
        String core_version = VersionUtil.getCoreVersion();
        params.put("serverID", paymentInfo.getServerId());
        params.put("userID", String.valueOf(ApiFacade.getInstance().getMainUid()));
        params.put("childUserID", String.valueOf(ApiFacade.getInstance().getSubUid()));
        params.put("userName",ApiFacade.getInstance().getUserName());
        params.put("sdkVersion",sdk_version +"-C"+ core_version);
        params.put("cpOrderID", paymentInfo.getCpOrderId());
        params.put("cpFee", String.valueOf(paymentInfo.getCpFee()));
        params.put("subject", paymentInfo.getSubject());
        params.put("body", paymentInfo.getBody());
        params.put("exInfo", paymentInfo.getExInfo());
        params.put("cpCallbackUrl", paymentInfo.getCpCallbackUrl());
        params.put("accessToken",ApiFacade.getInstance().getSession());

        // TODO: 2017/11/14 新支付
        HwRequest<InternalOrderInfo> request = new HwRequest<>(Urlpath.PAYMENT_ORDER,params,InternalOrderInfo.class,new OrderRespListener(paymentInfo.getCpOrderId()));
        RequestManager.getInstance(CoreManager.getContext()).addRequest(request, null);

    }

    @Override
    public void clearOrderCache() {
        orderCache.clear();
    }

    @Override
    public synchronized void notifyOrderState(final int payState,final String orderInfo) {

        paymentActivityHolder.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                IOperateCallback<String> callback = orderCache.callback;
                if (callback == null) {
                    Log.w(TAG, "fail to notify order state => callback not found.");
                    orderCache.clear();
                    return;
                }
                ToastUtils.showMsg(orderInfo);
                callback.onResult(payState, orderInfo);
                orderCache.clear();
            }
        });



//        IOperateCallback<String> callback = orderCache.callback;
//        if (callback == null) {
//            Log.w(TAG, "fail to notify order state => callback not found.");
//            orderCache.clear();
//            return;
//        }
//        callback.onResult(payState, orderInfo);
//        orderCache.clear();
    }

    /**
     * 通过客户端调用三方sdk支付
     * 默认不是sdk充值钱包
     * @param payWay
     * @param dataJsonStr
     */
    @Override
    public void orderThroughClient(int payWay, String dataJsonStr) {
        orderThroughClient(payWay, dataJsonStr, false);
    }

    /**通过客户端调用三方sdk支付
     *
     * @param payWay
     * @param dataJsonStr
     * @param isFromApp 是否通过sdk充值钱包
     */
    @Override
    public void orderThroughClient(int payWay, String dataJsonStr, final boolean isFromApp) {
        Activity paymentActivity = paymentActivityHolder.get();
        if (paymentActivity == null) {
            releaseOrderCallbackOnFail(TTCodeDef.ERROR_PAY_CONTEXT, "调起第三方支付失败");
            return;
        }
        PaymentAdapter.sharedInstance().startPay(payWay, paymentActivity, dataJsonStr, new IOperateCallback<String>() {
            @Override
            public void onResult(int i, String orderInfo) {
                notifyOrderState(i, orderInfo);
                Activity paymentActivity = paymentActivityHolder.get();
                if (paymentActivity != null && !paymentActivity.isFinishing()) {
                    if (isFromApp){
                        EventDispatcherAgent.defaultAgent().broadcast(BackToMainFragmentEvent.TYPE_BACK_TO_MAIN_FRAGMENT,
                                new BackToMainFragmentEvent.Param(0,paymentActivity,null));
                    }else {
                        paymentActivity.finish();
                    }
                }
            }
        });
    }

    /**
     * 更新支付页面引用。 部分三方支付SDK接口需要此数据。
     *
     * @param activity
     */
    @Override
    public void updatePaymentActivity(Activity activity) {
        if (paymentActivityHolder != null) {
            paymentActivityHolder.clear();
        }
        if (activity == null) {
            Log.w(TAG,"skip update payment activity - invalid activity.");
        }
        paymentActivityHolder = new WeakReference<Activity>(activity);
    }

    @Override
    public void closeNotify() {//关闭页面时候回调
        Log.d(TAG, "click close");
        if (paymentActivityHolder.get() != null){
        paymentActivityHolder.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                releaseOrderCallbackOnFail(TTCodeDef.ERROR_USER_CANCEL_ORDER, "cancel pay");

            }
        });}
//        releaseOrderCallbackOnFail(TTCodeDef.ERROR_USER_CANCEL_ORDER, "cancel pay");
    }

    @Override
    public void orderPay(Map<String, String> params, TtRespListener<String> callback) {
        params.put("accessToken", ApiFacade.getInstance().getSession());
        String url = HttpUtils.buildUrl(Urlpath.PAY_URL, params);
        HwRequest request = new HwRequest<>(url,null,String.class,callback);
        RequestManager.getInstance(CoreManager.getContext()).addRequest(request, null);
    }

    private class OrderRespListener extends TtRespListener<InternalOrderInfo>{
        private String cpOrderId;
        public OrderRespListener(String orderId) {
            cpOrderId = orderId;
        }

        @Override
        public void onNetSucc(String url, Map<String, String> params, InternalOrderInfo result) {
            super.onNetSucc(url, params, result);
            //error processing
            if (result == null) {
                Log.e(TAG,"wtf! order error => invalid result.");
                releaseOrderCallbackOnFail(StatusCodeDef.UNKNOWN, cpOrderId);
                return;
            }
            if (result.getCpOrderID() == null || !result.getCpOrderID().equals(cpOrderId)) {
                Log.e(TAG,"wtf! order error => unmatched.");
                releaseOrderCallbackOnFail(StatusCodeDef.UNKNOWN, cpOrderId);
                return;
            }
            if (StringUtils.isBlank(result.getPayUrl())) {
                Log.e(TAG,"wtf! invalid payment url.");
                releaseOrderCallbackOnFail(StatusCodeDef.UNKNOWN, cpOrderId);
                return;
            }

            //result processing : jump web view
//            String paymentUrl = result.getPayUrl();
//            Bundle arguments = new Bundle();
//            arguments.putString(ExtraDef.EXTRA_URL, paymentUrl);
//            StartActivityEvent.FragmentParam param = new StartActivityEvent.FragmentParam(0, CoreManager.getContext(),
//                    StartActivityEvent.DISPLAY_TYPE_FULLSCREEN, null, PAYMENT_WEB_CLZ, arguments);
//            EventDispatcherAgent.defaultAgent().broadcast(StartActivityEvent.TYPE_START_ACTIVITY_ENVENT, param);

//            ToastUtils.showMsg(result.getOrderInfoUrl());
            HwRequest<PayInfo> request = new HwRequest<>(result.getOrderInfoUrl(), params, PayInfo.class, new TtRespListener<PayInfo>() {
                @Override
                public void onNetSucc(String url, Map params, PayInfo result) {
                    Bundle arguments = new Bundle();
                    Gson gson = new Gson();
                    String str = gson.toJson(result);
                    android.util.Log.d(TAG, "onNetSucc: " + str);
                    arguments.putString(ExtraDef.EXTRA_PAY_INFO, str);
                    StartActivityEvent.FragmentParam fragmentParam = new StartActivityEvent.FragmentParam(0, CoreManager.getContext(),
                            StartActivityEvent.DISPLAY_TYPE_DIALOG,
                            null,PayCenterFragment.class.getName(), arguments);
                    EventDispatcherAgent.defaultAgent().broadcast(StartActivityEvent.TYPE_START_ACTIVITY_ENVENT, fragmentParam);
                }

                @Override
                public void onNetError(String url, Map<String, String> params, String errno, String errmsg) {
                    super.onNetError(url, params, errno, errmsg);
                    releaseOrderCallbackOnFail(StatusCodeDef.NetworkError, errmsg);
                }

                @Override
                public void onFail(int errorNo, String errmsg) {
                    super.onFail(errorNo, errmsg);
                    releaseOrderCallbackOnFail(errorNo, errmsg);
                }
            });
            RequestManager.getInstance(CoreManager.getContext()).addRequest(request, null);

        }

        @Override
        public void onNetError(String url, Map<String, String> params, String errno, String errmsg) {
            super.onNetError(url, params, errno, errmsg);
            Log.e(TAG, "order network error => error no:" + errno + ", error msg:" + errmsg);
            releaseOrderCallbackOnFail(StatusCodeDef.NetworkError, errmsg);
        }

        @Override
        public void onFail(int errorNo, String errmsg) {
            super.onFail(errorNo, errmsg);
            Log.e(TAG, "order fail => error no:" + errorNo + ",error msg:" + errmsg);
            releaseOrderCallbackOnFail(errorNo, errmsg);
        }
    }

    private synchronized void releaseOrderCallbackOnFail(int errorCode, String msg) {
        if (!orderCache.isEmpty()){
            Log.d(TAG, "releaseOrderCallbackOnFail: ");
            orderCache.callback.onResult(errorCode, msg);
            orderCache.clear();
        }
    }

    @Override
    public void getOrderFromApp(Map<String,String> params){
        HwJsonResquest jsonResquest = new HwJsonResquest(Urlpath.FROM_APP_OLDER,params,new TtRespListener<JSONObject>(){
            @Override
            public void onNetSucc(String url, Map<String, String> params, JSONObject result) {
                JSONObject head = result.optJSONObject("head");
                String code = head.optString("result");
                if (code.equals("0")){
                    String dataJsonStr = result.optString("body");
                    Log.d(TAG, "js invoke Pay - data : " + dataJsonStr);
                    if (params.get("payChannel").equals("ALIPAY")){
                        ApiFacade.getInstance().orderThroughClient(PaymentAdapter.PAY_WAY_ALI_PAY, dataJsonStr, true);
                    }else{
                        ApiFacade.getInstance().orderThroughClient(PaymentAdapter.PAY_WAY_IPAYNOW, dataJsonStr, true);
                    }
                }
            }

            @Override
            public void onNetError(String url, Map<String, String> params, String errno, String errmsg) {
                Log.d(TAG,"url: "+url + " params: "+ params + " errno: "+errno + "" + "errmsg: "+errmsg );
            }

            @Override
            public void onFail(int errorNo, String errmsg) {
                Log.d(TAG," errno: "+errorNo + "errmsg: "+errmsg );
            }
        });

        RequestManager.getInstance(CoreManager.getContext()).addRequest(jsonResquest, null);
    }
}
