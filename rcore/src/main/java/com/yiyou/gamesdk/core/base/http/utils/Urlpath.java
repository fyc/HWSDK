package com.yiyou.gamesdk.core.base.http.utils;

import com.yiyou.gamesdk.core.CoreManager;
import com.mobilegamebar.rsdk.outer.util.Log;

/**
 * Created by chenshuide on 15/6/4.
 */
public class Urlpath {
    private static final String TAG = "RSDK:UrlPath ";

    /**
     * 内网测试环境
     */
    public static final String INTER_TEST_HEAD = "";

    /**
     * 线上测试环境
     */
//    public static final String OUTER_TEST_HEAD = "https://sdktest.52hwgame.com:8001/sdk.server";
    public static final String APP_OUTER_TEST_HEAD = "https://api.52hwgame.com";
    public static final String OUTER_TEST_HEAD = "http://192.168.0.115:8001/sdk.server";

    /**
     * 线上环境
     */
    public static final String HEAD_ONLINE = "https://sdk.52tzgame.com/sdk";
    public static final String APP_HEAD_ONLINE = "https://api.52tzgame.com";

    public static String HEAD;
    public static String APP_HEAD;
    static {
        init();
    }

    private static void init() {
        if (CoreManager.isDebug()) {
            HEAD = OUTER_TEST_HEAD;
            APP_HEAD = APP_OUTER_TEST_HEAD;
        } else {
            HEAD = HEAD_ONLINE;
            APP_HEAD = APP_HEAD_ONLINE;
        }

        Log.d(TAG, HEAD);
    }


    // *******************************  注册登录相关  ************************* //
    /**
     * 注册
     */
    public static final String REGISTER = HEAD + "/rest/user/register.view";
    //小号注册
    public static final String REGISTER_CHILD = HEAD + "/rest/user/registerSlave.view";
    //小号改名
    public static final String CHILD_ACCOUNT_UPDATE = HEAD + "/rest/user/childUserUpdate.view";

    /**
     * 获取手机验证码
     */
    public static final String GET_PHONE_VERIFY_CODE = HEAD + "/rest/vcode/get.view";

    /**
     * 账号登录
     */
    public static final String LOGIN = HEAD + "/rest/user/login.view";

    /**
     * 用户事件上报
     */
    public static final String REPORT = HEAD + "/rest/user/gameOnlineTime.view";

    /**
     * 登出
     */
    public static final String LOGOUT = HEAD + "/rest/user/logout.view";

    // *******************************  注册登录相关  ************************* //


    /**
     * 修改密码
     */
    public static final String MODIFY_PWD = HEAD + "/rest/user/changepwd.view";

    //修改支付密码
    public static final String MODIFY_PAY_PWD = HEAD + "/rest/user/changepaypwd.view";

    //实名认证接口
    public static final String REALNAME_INFO = HEAD + "/rest/user/realInfo.view";

    //修改支付密码
    public static final String SET_PAY_PWD = HEAD + "/rest/user/setpaypwd.view";


    /**
     * 绑定手机，验证码发到新的手机号
     */
    public static final String BIND_PHONE = HEAD + "/rest/user/bindMobile.view";

    //
    public static final String UNBIND_PHONE = HEAD + "/rest/user/unbindMobile.view";


    /**
     * 游戏检查更新
     */
    public static final String GAME_UPDATE_CHECK = HEAD + "/rest/game/updatecheck.view";

    /**
     * 获取充值记录
     */
    public static final String ACCOUNT_TRADE_LIST = HEAD + "";

    /**
     * 充值下单
     */
    public static final String PAYMENT_ORDER = HEAD + "/rest/pay/order.view";

    /**
     * 热更检查
     */
    public static final String UPDATE_CHECK = HEAD + "/rest/game/checkupdatever.view";

    /**
     * 校验支付密码
     */
    public static final String VERIFY_PAY_PASSWORD = HEAD + "/rest/user/verifypaypwd.view";

    /**
     * 用户反馈
     */
    public static final String FEEDBACK = HEAD + "/rest/user/feedback.view";
    /**
     * 小号登录事件
     */
    public static final String CHILD_ACCOUNT_EVENT = HEAD + "/rest/user/childUserLogin.view";

    /**
     * 获取公告
     */
    public static final String ANNOUNCEMENT = CoreManager.isDebug() ? "https://sdktest.52hwgame.com:8004/syb.bulletin/api/getlist.view"
            : "https://sdk.52tzgame.com/bulletin/api/getlist.view";
    /**
     * 获取优惠券
     */
    public static final String COUPON = CoreManager.isDebug() ? "https://sdktest.52hwgame.com:8005/portal/rest/api/coupon/listCoupons.shtml"
            : "https://sdk.52tzgame.com/coupon/rest/api/coupon/listCoupons.shtml";

    /**
     * 获取优惠券数量
     */
    public static final String COUPON_COUNT = CoreManager.isDebug() ? "https://sdktest.52hwgame.com:8005/portal/rest/api/coupon/getSdkCouponTotal.shtml"
            : "https://sdk.52tzgame.com/coupon/rest/api/coupon/getSdkCouponTotal.shtml";
    //运维公告
    public static final String YUNWEI_1 = "http://106.75.177.175:9000/v1/operation/notice";
    public static final String YUNWEI_2 = "http://120.132.54.34:9000/v1/operation/notice";

    /**
     * 礼包中心
     */
    public static final String GIFT_CENTER = CoreManager.isDebug() ? "https://channeladmin.52hwgame.com/app/sdk/package/index.html"
            : "https://channeladmin.52tzgame.com/app/sdk/package/index.html";

    public static final String SERVICE_TERMS = CoreManager.isDebug() ? "https://channeladmin.52hwgame.com/app/info/TZfuwuxieyi.html"
            : "https://channeladmin.52tzgame.com/app/info/TZfuwuxieyi.html";
    /**
     * 钱包host url
     */
    public static final String WALLET = CoreManager.isDebug() ? "https://tg.52hwgame.com/channel/app/sdk_pay_history/index.html"
            :"https://tg.52tzgame.com/channel/app/sdk_pay_history/index.html";

    /**
     * 消费记录host url
     */
    public static final String PAY_HISTORY = CoreManager.isDebug() ? "https://channeladmin.52hwgame.com/app/pay_history/index.html"
            :"https://channeladmin.52tzgame.com/app/pay_history/index.html";

    /**
     * 获取余额
     */
    public static final String GET_BALANCE = CoreManager.isDebug() ? "https://sdktest.52hwgame.com:8002/portal/rest/api/getBalanceByPlatform.shtml"
            :"https://sdk.52tzgame.com/rcoin/rest/api/getBalanceByPlatform.shtml";


    public static final String FROM_APP_OLDER = CoreManager.isDebug() ? "https://sdktest.52hwgame.com:8003/rpay/pay/restPay.shtm"
            :"https://sdk.52tzgame.com/rpay/pay/restPay.shtm";

    /**
     * 官网下载app链接
     */
    public static final String DOWNLOAD_APP = "https://tg.52tzgame.com/?channelId=%s";

    /**
     * 小号交易列表
     */
    public static final String INVENTORY_LIST = APP_HEAD + "/trade/inventories";

    /**
     * 游戏礼包列表
     */
    public static final String GAME_PACKAGE = APP_HEAD + "/gamepackage/getbygameid";

    /**
     * 领取礼包
     */
    public static final String RECEIVE_PACKAGE = APP_HEAD + "/gamepackage/receivepackage";

    /**
     * 礼包详情
     */
    public static final String GAME_PACKAGE_DETAIL = APP_HEAD + "/gamepackage/getpackagebyid";

    /**
     * 支付请求
     */
    public static final String PAY_URL = CoreManager.isDebug() ? "https://sdktest.52hwgame.com:8003/rpay/consume/restConsume.shtm"
            : "https://sdk.52tzgame.com/rpay/consume/restConsume.shtm";

    public static final String GAME_DISCOUNT = CoreManager.isDebug() ? "https://sdktest.52hwgame.com:8005/portal/rest/api/discounts/show.shtml"
            : "https://sdk.52tzgame.com/coupon/rest/api/discounts/show.shtml";

    public static final String GET_COUPON = CoreManager.isDebug() ? "https://sdktest.52hwgame.com:8005/portal/rest/api/coupon/grant.shtml"
            : "https://sdk.52tzgame.com/coupon/rest/api/coupon/grant.shtml";

    public static final String GET_COUPON_BY_RULE = CoreManager.isDebug() ? "https://sdktest.52hwgame.com:8005/portal/rest/api/coupon/grantByRule.shtml"
            : "https://sdk.52tzgame.com/coupon/rest/api/coupon/grantByRule.shtml";
}
