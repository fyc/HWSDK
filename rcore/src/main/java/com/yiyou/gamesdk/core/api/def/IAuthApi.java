package com.yiyou.gamesdk.core.api.def;

import com.mobilegamebar.rsdk.outer.IOperateCallback;
import com.yiyou.gamesdk.core.base.http.volley.bean.VerifyCodeBean;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.yiyou.gamesdk.model.AuthModel;
import com.yiyou.gamesdk.model.BalanceInfo;
import com.yiyou.gamesdk.model.CouponCountInfo;
import com.yiyou.gamesdk.model.CouponInfo;
import com.yiyou.gamesdk.model.GameDiscountInfo;
import com.yiyou.gamesdk.model.GamePackages;
import com.yiyou.gamesdk.model.GetCouponInfo;
import com.yiyou.gamesdk.model.InventoriesInfo;

/**
 * Created by win on 17/4/26.
 */
public interface IAuthApi extends IApiWrapping {

    public static final int VCODE_TYPE_REGISTER = 2;
    public static final int VCODE_TYPE_REAL_VERIFY = 3;
    public static final int VCODE_TYPE_RESET_PASSWORD = 4;
    public static final int VCODE_TYPE_BIND_POHNE = 5;
    public static final int VCODE_TYPE_UNBIND_PHONE = 6;
    public static final int VCODE_TYPE_RESET_PAY_PASSWORD = 7;

    /**
     * 用户注册
     *
     * @param password   明文密码
     */
    void registerByUserName(String password,String userName, TtRespListener<AuthModel> callback);

    /**
     * 手机注册
     *
     * @param phone            手机号
     * @param password         明文密码
     * @param verificationCode 短信验证码
     */
    void registerByPhone(String phone, String password, String verificationCode, TtRespListener<AuthModel> callback);

    void registerChildAccount(String childUserName, TtRespListener<AuthModel.childAccount> callback);

    /**
     * 获取验证码
     *
     * @param phone  手机号
     * @param type  //1-保留，2-注册，3-实名认证，4-找回密码,5-解绑手机，6-解除绑定手机
     */
    void requestVerificationCode(String phone, int type, TtRespListener<Void> callback);

    /**
     * 获取验证码
     *
     * @param phone  手机号
     * @param type  //1-保留，2-注册，3-实名认证，4-找回密码,5-解绑手机，6-解除绑定手机
     * @param retry  重试次数
     */
    void requestVerificationCode(String phone, int type, int retry, TtRespListener<Void> callback);
    void requestVerificationCode2(String phone, int type, int retry, TtRespListener<VerifyCodeBean> callback);
    /**
     * 账号登录，用于用户手工登录
     *
     * @param account 账号
     * @param pwd     明文密码
     */
    void login(String account, String pwd,TtRespListener<AuthModel> callback);

    /**
     * 登出当前账号带CP回调
     */
    void logout(IOperateCallback<String> callback);

    /**
     * 登出当前账号
     */
    void logout();
    /**
     * 获取当前用户登录态
     * 登录前为null时
     *
     * @return 登录态
     */
    String getSession();

    /**
     * 获取当前用户uid
     * 登录前为0
     *
     * @return uid
     */
    long getMainUid();

    long getSubUid();

    /**
     * @return 用户名
     */
    String getUserName();

    String getSubUserName();

    String getPhone();

    /**
     * @return 是否登录
     */
    boolean isLogin();

    /**
     * 获取余额
     * @param callback
     */
    void getBalance(TtRespListener<BalanceInfo> callback);

    /**
     * 获取优惠券列表
     * @param type 优惠券状态('UNUSED':未使用,'USED':已使用,'EXPIRED':已过期,'DISABLE':失效)
     * @param callback
     */
    void getCouponInfos(String type ,TtRespListener<CouponInfo> callback);

    /**
     * 获取优惠券数量
     * @param type 优惠券状态('UNUSED':未使用,'USED':已使用,'EXPIRED':已过期,'DISABLE':失效)
     * @param callback
     */
    void getCouponCount(String type ,TtRespListener<CouponCountInfo> callback);


    /**
     * 小号登录事件
     */
    void childAccountEvent();

    /**
     * 优惠券页面
     */
    void getCouponCenter(TtRespListener<CouponInfo> callback);

    /**
     * 小号交易列表
     */
    void getInventories(TtRespListener<InventoriesInfo> callback);

    /**
     * 获取礼包列表
     */
    void getGamePackage(TtRespListener<GamePackages> callback);

    /**
     * 领取礼包
     * @param packageId  礼包ID
     */
    void receiveGamePackage(String packageId, TtRespListener<GamePackages.GamePackageInfo> callback);


    void getGamePackageDetail(String packageId, TtRespListener<GamePackages.GamePackageInfo> callback);

    void requestGameDiscount(TtRespListener<GameDiscountInfo> callback);

    void getCoupon(int actId,TtRespListener<GetCouponInfo> callback);

    void getCouponByRule(int ruleId,TtRespListener<GetCouponInfo> callback);

    AuthModel getAuth();
}
