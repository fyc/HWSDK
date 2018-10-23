package com.yiyou.gamesdk.core.api.def;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.mobilegamebar.rsdk.outer.IOperateCallback;
import com.mobilegamebar.rsdk.outer.model.PaymentInfo;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;

import java.util.Map;

/**
 * Created by levyyoung on 15/7/7.
 */
public interface IPaymentApi extends IApiWrapping {

    /**
     * 发起订单
     * @param paymentInfo    包含游戏方订单号，订单金额,商品名称,商品描述,
     *                       以及CP扩展信息，(该字段将会在支付成功后原样返回给CP)
     * @param orderCallback       回调订单信息
     * @param startUpActivity 来源页面
     */
    void order(PaymentInfo paymentInfo, @Nullable Activity startUpActivity, IOperateCallback<String> orderCallback);

    /**
     * 支付操作部分在webview中进行，sdk-client会一直持有orderCallback,直到支付页面调用js接口
     * notifyPayResult(String orderInfo)通知支付完毕。
     * 然后sdk-client会调用此接口回调游戏订单状态变化。
     *
     * @param payState 支付状态
     * @param orderInfo 订单信息
     */
    void notifyOrderState(int payState, String orderInfo);

    /**
     * 通过客户端调用三方sdk支付
     * 默认不是sdk充值钱包
     * @param payWay
     * @param dataJsonStr
     */
    void orderThroughClient(int payWay, String dataJsonStr);


    /**通过客户端调用三方sdk支付
     * @param payWay
     * @param dataJsonStr
     * @param isFromApp 是否通过sdk充值钱包
     */
    void orderThroughClient(int payWay, String dataJsonStr, boolean isFromApp);

    /**
     * 更新支付页面引用。 部分三方支付SDK接口需要此数据。
     * @param activity
     */
    void updatePaymentActivity(Activity activity);

    void closeNotify();

    void getOrderFromApp(Map<String,String> params);

    void orderPay(Map<String, String> params, TtRespListener<String> callback);

    void clearOrderCache();
}