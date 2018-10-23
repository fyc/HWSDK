package com.yiyou.gamesdk.core.api.impl.payment;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.mobilegamebar.rsdk.outer.IOperateCallback;
import com.mobilegamebar.rsdk.outer.consts.TTCodeDef;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.yiyou.gamesdk.util.ToastUtils;


/**
 * Created by LY on 2015/9/29.
 */
public class AliPayWrapper implements IPaySDKWrapper {
    private static final String TAG = "RSDK:AliPay";
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_CHECK_FLAG = 2;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();
                    String memo = payResult.getMemo();
                    if (resultStatus != null) {
                        // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                        if (TextUtils.equals(resultStatus, "9000")) {
                            PaymentAdapter.sharedInstance()
                                    .notifyPayResult(TTCodeDef.SUCCESS,
                                            memo);
                            ToastUtils.showMsg("交易状态: 支付成功");
                            return;
                        } else {
                            // 判断resultStatus 为非“9000”则代表可能支付失败
                            // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                            if (TextUtils.equals(resultStatus, "8000")) {
                                PaymentAdapter.sharedInstance()
                                        .notifyPayResult(TTCodeDef.PAY_RESULT_CHECKING,
                                                memo);

                            } else {
                                // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                                PaymentAdapter.sharedInstance()
                                        .notifyPayResult(TTCodeDef.PAY_RESULT_FAIL,
                                                memo);
                            }
                        }
                        ToastUtils.showMsg("交易状态: " + memo);
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    break;
                }
                default:
                    break;
            }
        }
    };
    @Override
    public void startPay(Activity startUpActivity, String paymentData, IOperateCallback<String> callback) {

        final String payInfo = paymentData;
        final Activity PayActivity = startUpActivity;
        if (startUpActivity == null) {
            Log.e(TAG, "no start up activity for payment.");
            if (callback != null) {
                callback.onResult(TTCodeDef.ERROR_NO_START_UP_ACTIVITY_FOR_PAY, "支付失败");
            }
            return;
        }

        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(PayActivity);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }
}
