package com.yiyou.gamesdk.core.api.impl;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley1.AuthFailureError;
import com.mobilegamebar.rsdk.outer.IOperateCallback;
import com.mobilegamebar.rsdk.outer.consts.TTCodeDef;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.mobilegamebar.rsdk.outer.util.ResourceHelper;
import com.mobilegamebar.rsdk.outer.util.StringUtils;
import com.yiyou.gamesdk.PluginManager;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.CoreManager;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.api.def.IAuthApi;
import com.yiyou.gamesdk.core.base.http.RequestHelper;
import com.yiyou.gamesdk.core.base.http.RequestManager;
import com.yiyou.gamesdk.core.base.http.utils.HttpUtils;
import com.yiyou.gamesdk.core.base.http.utils.Urlpath;
import com.yiyou.gamesdk.core.base.http.volley.HwAppRequest;
import com.yiyou.gamesdk.core.base.http.volley.HwRequest;
import com.yiyou.gamesdk.core.base.http.volley.QtRequest;
import com.yiyou.gamesdk.core.base.http.volley.bean.VerifyCodeBean;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.yiyou.gamesdk.core.consts.StatusCodeDef;
import com.yiyou.gamesdk.core.memcache.LoginInfo;
import com.yiyou.gamesdk.core.storage.db.global.AccountTable;
import com.yiyou.gamesdk.core.ui.floatview.FloatViewManager;
import com.yiyou.gamesdk.model.AccountHistoryInfo;
import com.yiyou.gamesdk.model.AuthModel;
import com.yiyou.gamesdk.model.BalanceInfo;
import com.yiyou.gamesdk.model.ChildrenAccountHistoryInfo;
import com.yiyou.gamesdk.model.CouponCountInfo;
import com.yiyou.gamesdk.model.CouponInfo;
import com.yiyou.gamesdk.model.GameDiscountInfo;
import com.yiyou.gamesdk.model.GamePackages;
import com.yiyou.gamesdk.model.GetCouponInfo;
import com.yiyou.gamesdk.model.InventoriesInfo;
import com.yiyou.gamesdk.util.ByteUtils;
import com.yiyou.gamesdk.util.PhoneUtils;
import com.yiyou.gamesdk.util.TaoziSignUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by win on 17/4/25.
 * 注册、登录、登出
 */
class AuthManager implements IAuthApi {

    private static final String TAG = "RSDK:AuthManager ";
    private static final String MD5_PWD_PREFIX = "\u0d62\u0d63\u135f";
    private AuthModel authModel = null;
    private String password="";
    private String phone = "";





    //用户注册
    @Override
    public void registerByUserName(String password, String userName, TtRespListener<AuthModel> callback) {
        if (StringUtils.isBlank(password) || password.length() < 6) {
            if (callback != null) {
                callback.onFail(StatusCodeDef.REG_FAIL_INVALID_PWD, ResourceHelper.getString(R.string.please_input_at_least_6_len));
            }
            return;
        }

        if (StringUtils.isBlank(userName) || userName.length() < 4) {
            if (callback != null) {
                callback.onFail(StatusCodeDef.REG_FAIL_INVALID_NAME, "请输入最少4位字符");
            }
            return;
        }

        Map<String, String> params = new TreeMap<>();
        RequestHelper.buildParamsWithBaseInfo(params);
        params.put("userName",userName);
        params.put("password",ByteUtils.generateMd5(password));
        TaoziSignUtils.addSign(params);
        HwRequest<AuthModel> hwRequest = new HwRequest<>(Urlpath.REGISTER,params,AuthModel.class,newAuthModelRespListenerWrapper(callback));
        RequestManager.getInstance(CoreManager.getContext()).addRequest(hwRequest, null);

    }

    //手机注册
    @Override
    public void registerByPhone(String phone, String password, String verificationCode, TtRespListener<AuthModel> callback) {
        if (StringUtils.isBlank(phone) || phone.length() < 11) {
            if (callback != null) {
                callback.onFail(StatusCodeDef.REG_FAIL_INVALID_PHONE, ResourceHelper.getString(R.string.please_input_valid_11_phone_num));
            }
            return;
        }
        if (StringUtils.isBlank(password) || password.length() < 6) {
            if (callback != null) {
                callback.onFail(StatusCodeDef.REG_FAIL_INVALID_PWD, ResourceHelper.getString(R.string.please_input_at_least_6_len));
            }
            return;
        }

        Map<String, String> params = new TreeMap<>();
        RequestHelper.buildParamsWithBaseInfo(params);
        params.put("userName",phone);
        params.put("password",ByteUtils.generateMd5(password));
        params.put("vcode",verificationCode);
        TaoziSignUtils.addSign(params);
        HwRequest<AuthModel> hwRequest = new HwRequest<>(Urlpath.REGISTER,params,AuthModel.class,newAuthModelRespListenerWrapper(callback));
        RequestManager.getInstance(CoreManager.getContext()).addRequest(hwRequest, null);

    }

    @Override
    public void registerChildAccount(String childUserName, TtRespListener<AuthModel.childAccount> callback) {

        Map<String, String> params = new TreeMap<>();
        RequestHelper.buildParamsWithBaseInfo(params);
        params.put("childUserName",childUserName);
        params.put("accessToken",ApiFacade.getInstance().getSession());
        HwRequest request = new HwRequest<>(Urlpath.REGISTER_CHILD,params,AuthModel.childAccount.class,callback);
        RequestManager.getInstance(CoreManager.getContext()).addRequest(request, null);
    }

    @Override
    public void requestVerificationCode(String phone, int type, int retry, TtRespListener<Void> callback) {
        Map<String, String> params = new TreeMap<>();
        RequestHelper.buildParamsWithBaseInfo(params);
        params.put("mobile", phone);
        params.put("type",type+"");
        params.put("retry", retry+"");

        if(type == IAuthApi.VCODE_TYPE_REGISTER){
            TaoziSignUtils.addSign(params);
        }
        HwRequest request = new HwRequest<>(Urlpath.GET_PHONE_VERIFY_CODE, params, null, callback);
        Log.d(TAG, "requestVerificationCode:Urlpath.GET_PHONE_VERIFY_CODE");
        RequestManager.getInstance(CoreManager.getContext()).addRequest(request, null);
    }
    @Override
    public void requestVerificationCode2(String phone, int type, int retry, TtRespListener<VerifyCodeBean> callback) {
        Map<String, String> params = new TreeMap<>();
//        String game_id = String.valueOf(ApiFacade.getInstance().getCurrentGameID() + "");
        String game_id = QtRequest.GAMW_ID;
        String ctime = String.valueOf(System.currentTimeMillis()/1000);
        params.put("mobile_phone", phone);
        params.put("game_id", game_id);
        params.put("ctime", ctime);
        String src = String.format("ctime=%s&game_id=%s&mobile_phone=%s",ctime,game_id,phone);
        params.put("src",src);
        QtRequest request = new QtRequest<>(Urlpath.GET_PHONE_VERIFY_CODE, params, VerifyCodeBean.class, callback);
        try {
            Log.d(TAG, "requestVerificationCode:requestHeader:"+request.getHeaders().toString());
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
        RequestManager.getInstance(CoreManager.getContext()).addRequest(request, null);
    }

    /**
     * 获取验证码
     *
     * @param phone
     * @param type  //1-保留，2-注册，3-实名认证，4-找回密码,5-解绑手机，6-解除绑定手机
     */
    @Override
    public void requestVerificationCode(String phone, int type, TtRespListener<Void> callback) {
        /*
        "head":{
            "result":"0",
            "message":"成功"
        }
        */
       requestVerificationCode(phone, type, 0, callback);
    }

    //** /rest/user/verifyMC


    public AuthModel getAuth() {
        return authModel;
    }



    //登录
    @Override
    public void login(String account, String pwd, TtRespListener<AuthModel> callback) {
        if (StringUtils.isBlank(account)) {
            if (callback != null) {
                callback.onFail(StatusCodeDef.LOGIN_FAIL_INVALID_PWD, ResourceHelper.getString(R.string.account_null));
            }
            return;
        }
        if (StringUtils.isBlank(pwd) || pwd.length() < 6) {
            if (callback != null) {
                callback.onFail(StatusCodeDef.LOGIN_FAIL_INVALID_PWD, ResourceHelper.getString(R.string.please_input_at_least_6_len));
            }
            return;
        }

        Map<String, String> params = new TreeMap<>();
        if (pwd.startsWith(MD5_PWD_PREFIX) && pwd.length() == (MD5_PWD_PREFIX.length() + 32)){
            String md5pwd = pwd.substring(MD5_PWD_PREFIX.length(), pwd.length());
            params.put("password", md5pwd);
        }else {
            params.put("password", ByteUtils.generateMd5(pwd));
        }
        params.put("userName", account);
        RequestHelper.buildParamsWithBaseInfo(params);

        HwRequest hwRequest = new HwRequest(Urlpath.LOGIN,params,AuthModel.class,newAuthModelRespListenerWrapper(callback));
        RequestManager.getInstance(CoreManager.getContext()).addRequest(hwRequest, null);

    }

    //** /rest/user/logout

    /**
     * 登出当前账号带CP回调
     */
    @Override
    public void logout(final IOperateCallback<String> iOperateCallback) {
        /*
        "head":{
            "result":"0",
            "message":"成功"
        }
        */
        //发登出请求。 不成功也不要紧
        Map<String, String> params = new TreeMap<>();
        RequestHelper.buildParamsWithBaseInfo(params);

        HwRequest hwRequest = new HwRequest<>(Urlpath.LOGOUT, params, null,new TtRespListener<Object>() {
            @Override
            public void onNetSucc(String url, Map params, Object result) {
                if (params != null) {
                    Log.d(TAG, "logout " + result);
                } else {
                    Log.d(TAG, "logout return null;");
                }
                authModel = null;
                iOperateCallback.onResult(TTCodeDef.SUCCESS, "Logout Success!!");
            }
        });
        RequestManager.getInstance(CoreManager.getContext()).addRequest(hwRequest, null);
        //清本地状态
        reset();
    }

    /**
     * 登出当前账号
     */
    @Override
    public void logout() {
        /*
        "head":{
            "result":"0",
            "message":"成功"
        }
        */
        //发登出请求。 不成功也不要紧
        Map<String, String> params = new TreeMap<>();
        RequestHelper.buildParamsWithBaseInfo(params);

        HwRequest request = new HwRequest<>(Urlpath.LOGOUT, params, null, new TtRespListener<Object>() {
            @Override
            public void onNetSucc(String url, Map params, Object result) {
                if (params != null) {
                    Log.d(TAG, "logout " + result);
                } else {
                    Log.d(TAG, "logout return null;");
                }
                authModel = null;
            }
        });
        RequestManager.getInstance(CoreManager.getContext()).addRequest(request, null);
        //清本地状态
        reset();
    }

    @Override
    public void getBalance(TtRespListener<BalanceInfo> callback) {
        Map<String, String> params = new TreeMap<>();
        RequestHelper.buildParamsWithBaseInfo(params);
        params.put("accessToken",ApiFacade.getInstance().getSession());
        HwRequest request = new HwRequest<>(Urlpath.GET_BALANCE, params,BalanceInfo.class,callback);
        RequestManager.getInstance(CoreManager.getContext()).addRequest(request, null);
    }

    @Override
    public String getSession() {
        return authModel != null ? authModel.getAccessToken() : null;
    }

    @Override
    public long getMainUid() {
        return authModel != null ? authModel.getUserID() : 0 ;
    }

    @Override
    public long getSubUid() {
        SharedPreferences preferences = CoreManager.getContext().getSharedPreferences(getMainUid()+"", Context.MODE_PRIVATE);
        Long uid = preferences.getLong("rsdk_childUserID",0);
        return uid;
    }

    @Override
    public String getUserName() {
        return authModel != null ? authModel.getUserName() : null ;
    }

    @Override
    public String getSubUserName() {
        SharedPreferences preferences = CoreManager.getContext().getSharedPreferences(getMainUid()+"", Context.MODE_PRIVATE);
        String UserName = preferences.getString("rsdk_childUserName","");
        return UserName;
    }

    @Override
    public String getPhone(){
        AccountHistoryInfo info = ApiFacade.getInstance().getCurrentHistoryAccount();
        if (info != null){
            return info.phone;
        }
        return "";
    }

    /**
     * @return 是否登录
     */
    @Override
    public boolean isLogin() {
        return !StringUtils.isBlank(getSession());
    }

    @Override
    public void getCouponCenter(TtRespListener<CouponInfo> callback) {
//
    }

    @Override
    public void getInventories(TtRespListener<InventoriesInfo> callback) {
        Map<String, String> params = new TreeMap<>();
        params.put("gameID", ApiFacade.getInstance().getCurrentGameID()+"");
        params.put("startIndex","0");
        String url = HttpUtils.buildUrl(Urlpath.INVENTORY_LIST, params);
        HwAppRequest request = new HwAppRequest<>(url, params,InventoriesInfo.class,callback);
        RequestManager.getInstance(CoreManager.getContext()).addRequest(request, null);
    }

    @Override
    public void getGamePackage(TtRespListener<GamePackages> callback) {
        Map<String, String> params = new TreeMap<>();
        params.put("gameId", ApiFacade.getInstance().getCurrentGameID()+"");
        RequestHelper.buildParamsWithAppInfo(params);
        String url = HttpUtils.buildUrl(Urlpath.GAME_PACKAGE, params);
        HwAppRequest request = new HwAppRequest<>(url, GamePackages.class,callback);
        RequestManager.getInstance(CoreManager.getContext()).addRequest(request, null);
    }

    @Override
    public void receiveGamePackage(String packageId, TtRespListener<GamePackages.GamePackageInfo> callback) {
        Map<String, String> params = new TreeMap<>();
        params.put("packageId", packageId);
        params.put("userName", ApiFacade.getInstance().getUserName());
//        params.put("userId", String.valueOf(ApiFacade.getInstance().getMainUid()));
        RequestHelper.buildParamsWithAppInfo(params);
        String url = HttpUtils.buildUrl(Urlpath.RECEIVE_PACKAGE, params);
        HwAppRequest request = new HwAppRequest<>(url,GamePackages.GamePackageInfo.class,callback);
        RequestManager.getInstance(CoreManager.getContext()).addRequest(request, null);
    }

    @Override
    public void getGamePackageDetail(String packageId, TtRespListener<GamePackages.GamePackageInfo> callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userName", ApiFacade.getInstance().getUserName());
        params.put("packageId", packageId);
        RequestHelper.buildParamsWithAppInfo(params);
        String url = HttpUtils.buildUrl(Urlpath.GAME_PACKAGE_DETAIL, params);
        HwAppRequest request = new HwAppRequest<>(url,GamePackages.GamePackageInfo.class,callback);
        RequestManager.getInstance(CoreManager.getContext()).addRequest(request, null);
    }

    private TtRespListener<AuthModel> newAuthModelRespListenerWrapper(final TtRespListener<AuthModel> callback) {
        return new TtRespListener<AuthModel>() {
            @Override
            public void onNetSucc(String url, Map<String, String> params, AuthModel result) {
                authModel = result;
                password = params.get("password");
                LoginInfo.getInstance().setAuthModel(result);

                if (result.getPhone() == null){
                    phone = "";
                }else {
                    phone = result.getPhone();
                }
                updateAccountHistory(result);
                updateChildAccountHistory(result);
                long subUid = getSubUid();
                if (subUid == 0){
                    ApiFacade.getInstance().setLastLoginChildAccount(result.getChildAccounts().get(0));
                }else {
                    boolean flag = false;
                    List<ChildrenAccountHistoryInfo> list = ApiFacade.getInstance().getCurrentChildrenAccountHistory();
                    for (ChildrenAccountHistoryInfo info : list){
                        if (subUid == info.childrenUserID){
                            flag = true;
                        }
                    }
                    if (!flag){
                        ApiFacade.getInstance().setLastLoginChildAccount(result.getChildAccounts().get(0));
                    }
                }
                childAccountEvent();
                initLoginSuccessUiTip();
                PluginManager.getInstance().getFloatService().startCountDown();
                if (callback != null) {
                    callback.onNetSucc(url, params, result);
                }

            }

            @Override
            public void onNetError(String url, Map<String, String> params, String errno, String errmsg) {
                super.onNetError(url, params, errno, errmsg);
                if (callback != null) {
                    callback.onNetError(url, params, errno, errmsg);
                }
            }

            @Override
            public void onFail(int errorNo, String errmsg) {
                super.onFail(errorNo, errmsg);
                if (callback != null) {
                    callback.onFail(errorNo, errmsg);
                }
            }

            @Override
            public void onNetworkComplete() {
                super.onNetworkComplete();
                if (callback != null) {
                    callback.onNetworkComplete();
                }
            }
        };
    }

    private void updateAccountHistory(AuthModel result) {
        ContentValues cv = new ContentValues();
        cv.put(AccountTable.COL_USERID, result.getUserID());
        cv.put(AccountTable.COL_TT_ACCOUNT, result.getTTAccount());
        cv.put(AccountTable.COL_USERNAME, result.getUserName());
        cv.put(AccountTable.COL_PHONE, phone);
        cv.put(AccountTable.COL_PWD, password);
        cv.put(AccountTable.COL_AVATAR_URL, result.getAvatarURL());
        cv.put(AccountTable.COL_LAST_LOGIN_TIME, new Date().getTime());
        cv.put(AccountTable.COL_HAS_PAYPASSWORD, result.isHasPayPassword());
        ApiFacade.getInstance().insertOrUpdateAccountHistory(cv);
    }

    private void updateChildAccountHistory(AuthModel result) {
        List<AuthModel.childAccount> list = result.getChildAccounts();
        List<ChildrenAccountHistoryInfo> childrenAccountHistoryInfoList = new ArrayList<>();
        for (AuthModel.childAccount childAccount : list){
            ChildrenAccountHistoryInfo history = new ChildrenAccountHistoryInfo();
            history.userID = childAccount.getUserID();
            history.bundleID = childAccount.getBundleID();
            history.childrenUserID = childAccount.getChildUserID();
            history.childrenUsername = childAccount.getChildUserName();
            history.gameId = ApiFacade.getInstance().getCurrentGameID()+"";
            history.TTAccount = childAccount.getTTAccount();
            history.lastLoginTime = new Date().getTime();
            childrenAccountHistoryInfoList.add(history);
        }
        ApiFacade.getInstance().updateCurrentChildrenAccount(childrenAccountHistoryInfoList);
    }


    private void reset() {
        PluginManager.getInstance().getFloatService().stopCountDown();
        FloatViewManager.getInstance().destory();
        LoginInfo.getInstance().clear();
        ApiFacade.getInstance().refresAccounthHistory();
    }

    private void initLoginSuccessUiTip() {
        Toast toast = new Toast(CoreManager.getContext());
        View toastRoot = LayoutInflater.from(CoreManager.getContext()).inflate(R.layout.tt_sdk_loginsuccess_tip, null);
        TextView txtContent = (TextView) toastRoot.findViewById(R.id.account);
        txtContent.setText("你好, <" +getUserName() + ">");
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastRoot);
        toast.show();
    }

    @Override
    public void getCouponInfos(String type, TtRespListener<CouponInfo> callback) {
//        Log.d(TAG, "getCouponInfos: ");
        Map<String, String> params = new TreeMap<>();
        RequestHelper.buildParamsWithBaseInfo(params);
        params.put("accessToken",ApiFacade.getInstance().getSession());
        params.put("gameId",String.valueOf(ApiFacade.getInstance().getCurrentGameID()));
        params.put("status",type);
        HwRequest request = new HwRequest<>(Urlpath.COUPON,params,CouponInfo.class,callback);
        RequestManager.getInstance(CoreManager.getContext()).addRequest(request, null);
    }

    @Override
    public void getCouponCount(String type, TtRespListener<CouponCountInfo> callback) {
//        Log.d(TAG, "getCouponCount: ");
        Map<String, String> params = new TreeMap<>();
        RequestHelper.buildParamsWithBaseInfo(params);
        params.put("accessToken",ApiFacade.getInstance().getSession());
        params.put("gameId",String.valueOf(ApiFacade.getInstance().getCurrentGameID()));
        params.put("status",type);
        HwRequest request = new HwRequest<>(Urlpath.COUPON_COUNT,params,CouponCountInfo.class,callback);
        RequestManager.getInstance(CoreManager.getContext()).addRequest(request, null);
    }

    @Override
    public void childAccountEvent() {
        Map<String, String> params = new TreeMap<>();
        RequestHelper.buildParamsWithBaseInfo(params);
        params.put("childUserId",String.valueOf(getSubUid()));
        HwRequest request = new HwRequest<>(Urlpath.CHILD_ACCOUNT_EVENT,params,null,null);
        RequestManager.getInstance(CoreManager.getContext()).addRequest(request,null);
    }

    @Override
    public void requestGameDiscount(TtRespListener<GameDiscountInfo> callback) {
        Map<String, String> params = new TreeMap<>();
        params.put("devId", PhoneUtils.getDeviceId(CoreManager.getContext()));
        params.put("gameId",String.valueOf(ApiFacade.getInstance().getCurrentGameID()));
        params.put("accessToken", ApiFacade.getInstance().getSession());
        params.put("bundleId", ResourceHelper.getPackageName());
        HwRequest<GameDiscountInfo> request = new HwRequest<>(Urlpath.GAME_DISCOUNT,params,GameDiscountInfo.class,callback);
        RequestManager.getInstance(CoreManager.getContext()).addRequest(request,null);
    }

    @Override
    public void getCoupon(int actId, TtRespListener<GetCouponInfo> callback) {
        Map<String, String> params = new TreeMap<>();
        params.put("actId",String.valueOf(actId));
        params.put("channelId",ApiFacade.getInstance().getChannel());
        params.put("devId", PhoneUtils.getDeviceId(CoreManager.getContext()));
        params.put("accessToken", ApiFacade.getInstance().getSession());
        HwRequest<GetCouponInfo> request = new HwRequest<>(Urlpath.GET_COUPON,params,GetCouponInfo.class,callback);
        RequestManager.getInstance(CoreManager.getContext()).addRequest(request,null);
    }

    @Override
    public void getCouponByRule(int ruleId, TtRespListener<GetCouponInfo> callback) {
        Map<String, String> params = new TreeMap<>();
        params.put("ruleId",String.valueOf(ruleId));
        params.put("channelId",ApiFacade.getInstance().getChannel());
        params.put("devId", PhoneUtils.getDeviceId(CoreManager.getContext()));
        params.put("accessToken", ApiFacade.getInstance().getSession());
        HwRequest<GetCouponInfo> request = new HwRequest<>(Urlpath.GET_COUPON_BY_RULE,params,GetCouponInfo.class,callback);
        RequestManager.getInstance(CoreManager.getContext()).addRequest(request,null);
    }
}
