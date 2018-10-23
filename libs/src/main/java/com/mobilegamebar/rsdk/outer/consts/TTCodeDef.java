package com.mobilegamebar.rsdk.outer.consts;

/**
 * Created by chenshuide on 15/7/23.
 */
public class TTCodeDef {
    public static  final int SUCCESS = 0;
    public static  final int UNKNOWN = -1;

    //Logout status
    public static  final int LOGOUT_NO_INIT     =  -110100;
    public static  final int LOGOUT_NO_LOGIN    =  -110101;
    public static  final int LOGOUT_FAIL        =  -110102;

    //Login
    public static final int  ERROR_USER_CANCEL_LOGIN   =  -110103;

    //init
    public static final int ERROR_INIT_INVALID_GAME_ID = -110104;

    //payment
    public static final int ERROR_PAY_WAY_NOT_SUPPORT = -110200;
    public static final int ERROR_NO_START_UP_ACTIVITY_FOR_PAY = -110201;
    public static final int ERROR_USER_CANCEL_ORDER = -110202;
    public static final int ERROR_ORDER_FAIL_BY_NEW_ONE = -110203;
    public static final int ERROR_ORDER_FAIL_ALL_IN_PAY = -110204;
    public static final int PAY_RESULT_CHECKING = -110205; //支付宝：因为支付渠道原因或者系统原因还在等待支付结果确认
    public static final int PAY_RESULT_FAIL = -110206;  //支付宝支付失败：包括用户主动取消支付，或者系统返回的错误
    public static final int ERROR_PAY_CONTEXT = -110207; //支付页面上下文界面被回收.导致不能启动三方支付界面.
    public static final int ERROR_3RD_SERVICE_NEED_INSTALL_OR_UPDATE = -110208;
    public static final int IPAY_NOW_RESULT_FAIL = -110209;  //聚合银联、微信支付失败
    public static final int IPAY_NOW_RESULT_CANCEL = -110210; //聚合银联、微信支付取消
    public static final int ORDER_OVER_FREQUENCY = -110211; //支付接口调用过于频繁
}
