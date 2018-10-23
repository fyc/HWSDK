package com.yiyou.gamesdk.core.api.impl.payment;

import android.app.Activity;

import com.mobilegamebar.rsdk.outer.IOperateCallback;

/**
 * Created by levyyoung on 15/9/16.
 */
public interface IPaySDKWrapper {

    void startPay(Activity startUpActivity, String paymentData, IOperateCallback<String> callback);

}
