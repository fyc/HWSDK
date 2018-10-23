package com.yiyou.gamesdk.core.api.impl.payment;

import android.app.Activity;
import android.support.v4.util.SparseArrayCompat;
import com.mobilegamebar.rsdk.outer.IOperateCallback;
import com.mobilegamebar.rsdk.outer.consts.TTCodeDef;
import com.mobilegamebar.rsdk.outer.util.Log;

/**
 * Created by levyyoung on 15/9/16.
 */
public class PaymentAdapter {

    public static final int PAY_WAY_ALI_PAY = 0;//支付宝
    public static final int PAY_WAY_IPAYNOW = 1;//现在支付

    private static final String TAG = "RSDK:PaymentAdapter";
    private static PaymentAdapter _instance;

    ///////////////////////////////////////////////////////////////////////////
    // PAYMENT IMPLEMENT REGISTER BEGIN
    ///////////////////////////////////////////////////////////////////////////
    private SparseArrayCompat<IPaySDKWrapper> payImplMapping = new SparseArrayCompat<IPaySDKWrapper>()
    {{
            put(PAY_WAY_ALI_PAY,new AliPayWrapper());
            put(PAY_WAY_IPAYNOW,new IPayNowWrapper());
    }};
    ///////////////////////////////////////////////////////////////////////////
    // PAYMENT IMPLEMENT REGISTER END
    ///////////////////////////////////////////////////////////////////////////

    private IOperateCallback<String> paymentCallback = null;

    private PaymentAdapter(){

    }

    ///////////////////////////////////////////////////////////////////////////
    // INSTANCE CREATE/DESTROY BEGIN
    ///////////////////////////////////////////////////////////////////////////
    public static synchronized PaymentAdapter sharedInstance() {
        if (_instance == null) {
            _instance = new PaymentAdapter();
        }
        return _instance;
    }

    //!!!插件销毁时要保证没有class被缓存。所以单例都需提供销毁方法。
    public static synchronized void destroy() {
        if (_instance != null) {
            _instance = null;
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    // INSTANCE CREATE/DESTROY END
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 客户端支持通用接口
     * @param payWay 客户端支持的三方SDK支付方式。见头部定义。
     * @param startUpActivity 三方支付需要用到的
     * @param paymentData 支付数据，JSON-String。
     * @param callback 到PaymentManager的回调方法。这样到CP回调都统一由 PaymentManager 处理，与 web 一致。
     */
    public synchronized void startPay(int payWay, Activity startUpActivity, String paymentData, IOperateCallback<String> callback) {
        IPaySDKWrapper payImpl = payImplMapping.get(payWay);

        if (payImpl == null) {
            Log.e(TAG, String.format("pay-way %d not found.", payWay));
            if (callback != null) {
                callback.onResult(TTCodeDef.ERROR_PAY_WAY_NOT_SUPPORT, "支付失败");
            }
            return;
        }

        paymentCallback = callback;

        payImpl.startPay(startUpActivity, paymentData, callback);

    }

    /**
     * 三方支付结果流程中调用
     * @param code  支付结果
     */
    public synchronized void notifyPayResult(int code, String Paymsg) {
        if (paymentCallback != null) {
            paymentCallback.onResult(code, Paymsg);
            paymentCallback = null;
        }
    }


}
