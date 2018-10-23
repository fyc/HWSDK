package com.yiyou.gamesdk.core.api.impl;

import android.support.v4.util.ArrayMap;

import com.yiyou.gamesdk.core.CoreManager;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.api.def.ISecurityApi;
import com.yiyou.gamesdk.core.base.http.RequestHelper;
import com.yiyou.gamesdk.core.base.http.RequestManager;
import com.yiyou.gamesdk.core.base.http.utils.Urlpath;
import com.yiyou.gamesdk.core.base.http.volley.HwRequest;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.mobilegamebar.rsdk.outer.util.StringUtils;
import com.yiyou.gamesdk.util.ByteUtils;
import com.yiyou.gamesdk.util.ToastUtils;

import java.util.Map;

/**
 * Created by win on 15/6/8.
 * 修改密码、绑定手机、重置密码
 */
class SecurityManager implements ISecurityApi {
    private static final String TAG = "RSDK:SecurityManager ";

    @Override
    public void modifyPayPassword(String oldPwd, String newPwd, TtRespListener callback) {
        if (StringUtils.isBlank(oldPwd)) {
            Log.e(TAG, "Error ModifyPassword. Old password is null or empty");
            return;
        }
        if (StringUtils.isBlank(newPwd)) {
            Log.e(TAG, "Error ModifyPassword. New password is null or empty");
            return;
        }
        Map<String, String> params = new ArrayMap<>();
        RequestHelper.buildParamsWithBaseInfo(params);
        params.put("oldpwd",ByteUtils.generateMd5(oldPwd));
        params.put("newpwd",ByteUtils.generateMd5(newPwd));
        HwRequest hwRequest = new HwRequest<>(Urlpath.MODIFY_PAY_PWD,params,null,callback);
        RequestManager.getInstance(CoreManager.getContext()).addRequest(hwRequest, null);
    }

    @Override
    public void setPayPassword(String password, TtRespListener callback) {
        if (StringUtils.isBlank(password)) {
            Log.e(TAG, "Error setPayPassword. password is null or empty");
            return;
        }
        Map<String, String> params = new ArrayMap<>();
        RequestHelper.buildParamsWithBaseInfo(params);
        params.put("accessToken", ApiFacade.getInstance().getSession());
        params.put("password",ByteUtils.generateMd5(password));
        HwRequest hwRequest = new HwRequest<>(Urlpath.SET_PAY_PWD,params,null,callback);
        RequestManager.getInstance(CoreManager.getContext()).addRequest(hwRequest, null);
    }

    @Override
    public void modifyPassword(String oldPwd, String newPwd, TtRespListener callback) {
        if (StringUtils.isBlank(oldPwd)) {
            Log.e(TAG, "Error ModifyPassword. Old password is null or empty");
            return;
        }
        if (StringUtils.isBlank(newPwd)) {
            Log.e(TAG, "Error ModifyPassword. New password is null or empty");
            return;
        }
        Map<String, String> params = new ArrayMap<>();
        RequestHelper.buildParamsWithBaseInfo(params);
        params.put("oldpwd",ByteUtils.generateMd5(oldPwd));
        params.put("newpwd",ByteUtils.generateMd5(newPwd));
        HwRequest hwRequest = new HwRequest<>(Urlpath.MODIFY_PWD,params,null,callback);
        RequestManager.getInstance(CoreManager.getContext()).addRequest(hwRequest, null);

    }

    @Override
    public void forgetPayPassword(String mobile, String newPwd, String vcode, TtRespListener callback) {
        if (StringUtils.isBlank(mobile)||StringUtils.isBlank(newPwd)||StringUtils.isBlank(vcode) ) {
            ToastUtils.showMsg("请不要输入空字符");
            return;
        }
        Map<String, String> params = new ArrayMap<>();
        RequestHelper.buildParamsWithBaseInfo(params);
        params.put("phone",mobile);
        params.put("newpwd",ByteUtils.generateMd5(newPwd));
        params.put("vcode",vcode);
        HwRequest hwRequest = new HwRequest<>(Urlpath.MODIFY_PAY_PWD,params,null,callback);
        RequestManager.getInstance(CoreManager.getContext()).addRequest(hwRequest, null);
    }

    @Override
    public void forgetPassword(String mobile, String newPwd, String vcode, TtRespListener callback) {
        if (StringUtils.isBlank(mobile)||StringUtils.isBlank(newPwd)||StringUtils.isBlank(vcode) ) {
            ToastUtils.showMsg("请不要输入空字符");
            return;
        }
        Map<String, String> params = new ArrayMap<>();
        RequestHelper.buildParamsWithBaseInfo(params);
        params.put("phone",mobile);
        params.put("newpwd",ByteUtils.generateMd5(newPwd));
        params.put("vcode",vcode);
        HwRequest hwRequest = new HwRequest<>(Urlpath.MODIFY_PWD,params,null,callback);
        RequestManager.getInstance(CoreManager.getContext()).addRequest(hwRequest, null);
    }

    @Override
    public void bindPhone(String phoneNum, String smsVCode, TtRespListener callback) {
        Map<String, String> params = new ArrayMap<>();
        RequestHelper.buildParamsWithBaseInfo(params);
        params.put("vcode",smsVCode);
        params.put("mobile",phoneNum);
        HwRequest hwRequest = new HwRequest<>(Urlpath.BIND_PHONE,params,null,callback);
        RequestManager.getInstance(CoreManager.getContext()).addRequest(hwRequest, null);
    }

    @Override
    public void unbindPhone(String phoneNum, String smsVCode, TtRespListener callback) {
        if (StringUtils.isBlank(phoneNum)||StringUtils.isBlank(smsVCode)){
            ToastUtils.showMsg("请不要输入空字符");
            return;
        }
        Map<String, String> params = new ArrayMap<>();
        RequestHelper.buildParamsWithBaseInfo(params);
        params.put("vcode",smsVCode);
        params.put("mobile",phoneNum);
        HwRequest hwRequest = new HwRequest<>(Urlpath.UNBIND_PHONE,params,null,callback);
        RequestManager.getInstance(CoreManager.getContext()).addRequest(hwRequest, null);
    }

    @Override
    public void verifyPayPassword(String payPassword, TtRespListener callback) {
        Map<String, String> params = new ArrayMap<>();
        RequestHelper.buildParamsWithBaseInfo(params);
        params.put("password",ByteUtils.generateMd5(payPassword));
        params.put("accessToken",ApiFacade.getInstance().getSession());
        HwRequest hwRequest = new HwRequest<>(Urlpath.VERIFY_PAY_PASSWORD,params,null,callback);
        RequestManager.getInstance(CoreManager.getContext()).addRequest(hwRequest, null);
    }
}
