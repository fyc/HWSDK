package com.yiyou.gamesdk.core.api.def;

import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;

/**
 * Created by win on 17/4/26.
 */
public interface ISecurityApi extends IApiWrapping {

    void setPayPassword(String password, TtRespListener callback);

    void modifyPayPassword(String oldPwd, String newPwd, TtRespListener callback);

    void modifyPassword(String oldPwd, String newPwd, TtRespListener callback);

    void forgetPayPassword(String mobile, String newPwd, String vcode,TtRespListener callback);

    void forgetPassword(String mobile, String newPwd, String vcode,TtRespListener callback);

    void bindPhone(String phoneNum, String smsVCode, TtRespListener callback);

    void unbindPhone(String phoneNum, String smsVCode, TtRespListener callback);

    void verifyPayPassword(String payPassword, TtRespListener callback);
}
