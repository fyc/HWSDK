package com.yiyou.gamesdk.core.consts;

import android.support.v4.util.ArrayMap;

import com.mobilegamebar.rsdk.outer.consts.TTCodeDef;
import com.mobilegamebar.rsdk.outer.util.StringUtils;

/**
 * Created by levyyoung on 15/4/30.
 */
public class StatusCodeDef {
    public static final int SUCCESS = TTCodeDef.SUCCESS;
    public static final int UNKNOWN = TTCodeDef.UNKNOWN;

    //FAIL STATUS
    public static final int FAIL_INIT_SDK_DB_OPEN_ERROR = -100100;
    public static final int FAIL_EXIT_INVALID_CONTEXT = -100110;


    //DB STATUS
    public static final int READ_WRITE_DB_NOT_FOUND = -100200;
    public static final int INSERT_FAIL_WITH_ERROR = -100201;

    //NET STATUS
    public static int NetworkError = -100300;

    //AUTH STATUS
    public static final int REG_FAIL_INVALID_PHONE = -100400;
    public static final int REG_FAIL_INVALID_PWD = -100401;
    public static final int LOGIN_FAIL_INVALID_PHONE = -100402;
    public static final int LOGIN_FAIL_INVALID_PWD = -100403;
    public static final int LOGIN_FAIL_INVALID_GAME_ID = -100404;
    public static final int LOGIN_FAIL_NO_AUTH_HISTORY = -100405;
    public static final int LOGIN_FAIL_INVALID_ACCOUNT = -100406;
    public static final int LOGIN_FAIL_REMOTE_ERROR = -100407;
    public static final int LOGIN_FAIL_NETWORK = -100408;
    public static final int REG_FAIL_INVALID_NAME = -100409;

    //游戏下载
    public static final int GAME_DOWNLOAD_PLZ_HOLD_CALLBACK = -100500;

    //=================== REMOTE ERROR =====================
    private  static final ArrayMap<String,Integer> REMOTE_CODE_MAP = new ArrayMap<String,Integer>(){
        {
            put("0",                    0);
            put("SDKS-00-0000-100",     SVR_ERROR);
            put("SDKS-00-0000-101",     SVR_RETURN_NULL);
            put("SDKS-00-0000-102",     SVR_ERROR_CHECK_SIGN);
            put("SDKS-00-0000-103",     SVR_ERROR_SESSION);
            put("SDKS-00-0000-104",     SVR_ERROR_CP_KEY);
            put("SDKS-00-0000-105",     SVR_ERROR_DB_OP);
            put("SDKS-01-101",          CPP_SVR_PHONE_REGISTERED);
            put("SDKS-01-102",          CPP_SVR_USERNAME_EXISTED);
            put("SDKS-01-103",          CPP_SVR_ACCOUNT_NOT_EXISTED);
            put("SDKS-01-104",          CPP_SVR_WRONG_PWD);
            put("SDKS-01-105",          CPP_SVR_ACCT_ALIASED);
            put("SDKS-01-106",          CPP_SVR_USER_INFO_COMPLETED);
            put("SDKS-01-107",          CPP_SVR_NO_FACE);
            put("SDKS-01-108",          CPP_SVR_WRONG_VERIFY_CODE);
            put("SDKS-01-109",          CPP_SVR_ERROR_SAME_PWD);
            put("SDKS-01-110",          CPP_SVR_ERROR_PHONE_FORMAT);
            put("SDKS-01-111",          CPP_SVR_ERROR_USER_NAME_FORMAT);
            put("SDKS-01-112",          CPP_SVR_ERROR_NO_PERMISSION);
            put("SDKS-01-113",          CPP_SVR_USER_NO_GUILD);
            put("SDKS-01-114",          CPP_SVR_TO_MANY_VERITY_REQ);
            put("SDKS-01-115",          CPP_SVR_ACCOUNT_EXISTED);
            put("SDKS-01-116",          CPP_SVR_ERROR_VERIFY_MSG);
            put("SDKS-01-117",          CPP_SVR_SECURITY_NOT_UPGRADE);
            put("SDKS-01-118",          CPP_SVR_WRONG_SECURITY_ANSWER);
            put("SDKS-01-119",          CPP_SVR_WRONG_SECURITY_QUESTION);
            put("SDKS-01-120",          CPP_SVR_SENSITIVE_NICK);
            put("SDKS-01-121",          CPP_SVR_SENSITIVE_SIGN);
            put("SDKS-01-122",          CPP_SVR_ALREADY_UPGRADE_SECURITY);
            put("SDKS-01-123",          CPP_SVR_CAN_NOT_REST_ACCT);
            put("SDKS-01-124",          CPP_SVR_FORBIDDEN_ACCT);
            put("SDKS-01-125",          CPP_SVR_TOO_MANY_REG_REQ);
            put("SDKS-01-126",          CPP_SVR_OPENID_EXISTED);
            put("SDKS-01-200",          USR_SVR_ERROR_NULL_USER_NAME);
            put("SDKS-01-201",          USR_SVR_ERROR_NULL_PWD);
            put("SDKS-01-202",          USR_SVR_FORBIDDEN_ACCT);
            put("SDKS-01-205",          USR_SVR_REG_NEED_VERIFY);
        }
    };

    public static int transRemoteError(String remoteError) {
        int result = UNKNOWN;
        if (!StringUtils.isBlank(remoteError)) {
            Integer code = REMOTE_CODE_MAP.get(remoteError);
            if (code != null) {
                result = code;
            }
        }
        return result;
    }

    public static String transRemoteErrorAsString(String remoteError) {
        return String.valueOf(transRemoteError(remoteError));
    }

    ///////////////////////////////////////////////////////////////////////////
    // REMOTE STATUS ORIGINAL DEF
    ///////////////////////////////////////////////////////////////////////////
    public static final String RS_SUCCESS                    = "0";
    public static final String RS_ERROR                      = "SDKS-00-0000-100";  //后台服务异常
    public static final String RS_RETURN_NULL                = "SDKS-00-0000-101";  //后台服务返回为null
    public static final String RS_ERROR_CHECK_SIGN           = "SDKS-00-0000-102";  //校验签名失败
    public static final String RS_ERROR_SESSION              = "SDKS-00-0000-103";  //校验会话失败
    public static final String RS_ERROR_CP_KEY               = "SDKS-00-0000-104";  //获取游戏密钥异常
    public static final String RS_ERROR_DB_OP                = "SDKS-00-0000-105";  //操作数据库异常
    public static final String RS_PHONE_REGISTERED           = "SDKS-01-101";       //手机号已注册
    public static final String RS_USERNAME_EXISTED           = "SDKS-01-102";       //用户名重复
    public static final String RS_ACCOUNT_NOT_EXISTED        = "SDKS-01-103";       //账号不存在
    public static final String RS_WRONG_PWD                  = "SDKS-01-104";       //密码错误
    public static final String RS_ACCT_ALIASED               = "SDKS-01-105";       //已经修改过账号名
    public static final String RS_USER_INFO_COMPLETED        = "SDKS-01-106";       //用户资料已经完善过了，不能再完善
    public static final String RS_NO_FACE                    = "SDKS-01-107";       //用户没有上传头像
    public static final String RS_WRONG_VERIFY_CODE          = "SDKS-01-108";       //验证码不正确
    public static final String RS_ERROR_SAME_PWD             = "SDKS-01-109";       //新密码不能与旧密码相同
    public static final String RS_ERROR_PHONE_FORMAT         = "SDKS-01-110";       //手机号码格式不对
    public static final String RS_ERROR_USER_NAME_FORMAT     = "SDKS-01-111";       //用户名不能包含特殊字符
    public static final String RS_ERROR_NO_PERMISSION        = "SDKS-01-112";       //权限不足
    public static final String RS_USER_NO_GUILD              = "SDKS-01-113";       //用户未加入工会
    public static final String RS_TO_MANY_VERITY_REQ         = "SDKS-01-114";       //发送验证短信太频繁
    public static final String RS_ACCOUNT_EXISTED            = "SDKS-01-115";       //账号已存在
    public static final String RS_ERROR_VERIFY_MSG           = "SDKS-01-116";       //获取验证码时的用途填写不正确
    public static final String RS_SECURITY_NOT_UPGRADE       = "SDKS-01-117";       //用户未设置密保
    public static final String RS_WRONG_SECURITY_ANSWER      = "SDKS-01-118";       //密保答案错误
    public static final String RS_WRONG_SECURITY_QUESTION    = "SDKS-01-119";       //密保问题错误
    public static final String RS_SENSITIVE_NICK             = "SDKS-01-120";       //昵称包含敏感词汇
    public static final String RS_SENSITIVE_SIGN             = "SDKS-01-121";       //签名包含敏感词汇
    public static final String RS_ALREADY_UPGRADE_SECURITY   = "SDKS-01-122";       //用户已经设置过密保
    public static final String RS_CAN_NOT_REST_ACCT          = "SDKS-01-123";       //已关闭重置帐号通道
    public static final String RS_CPP_FORBIDDEN_ACCT         = "SDKS-01-124";       //该帐号已被停封
    public static final String RS_TOO_MANY_REG_REQ           = "SDKS-01-125";       //设备注册频率限制
    public static final String RS_OPENID_EXISTED             = "SDKS-01-126";       //openid已存在
    public static final String RS_ERROR_NULL_USER_NAME       = "SDKS-01-200";       //用户名不能为空
    public static final String RS_ERROR_NULL_PWD             = "SDKS-01-201";       //旧密码或新密码为空
    public static final String RS_JAVA_FORBIDDEN_ACCT        = "SDKS-01-202";       //该用户被禁用
    public static final String RS_JAVA_REG_NEED_VERIFY       = "SDKS-01-205";       //一键注册需要验证

    ///////////////////////////////////////////////////////////////////////////
    // REMOTE STATUS CODE
    ///////////////////////////////////////////////////////////////////////////

    public static final int REMOTE_SUCCESS                  = 0;      //0
    //通用系统异常
    public static final int SVR_ERROR                       = -1100;  //SDKS-00-0000-100=后台服务异常
    public static final int SVR_RETURN_NULL                 = -1101;  //SDKS-00-0000-101=后台服务返回为null
    public static final int SVR_ERROR_CHECK_SIGN            = -1102;  //SDKS-00-0000-102=校验签名失败
    public static final int SVR_ERROR_SESSION               = -1103;  //SDKS-00-0000-103=校验会话失败
    public static final int SVR_ERROR_CP_KEY                = -1104;  //SDKS-00-0000-104=获取游戏密钥异常
    public static final int SVR_ERROR_DB_OP                 = -1105;  //SDKS-00-0000-105=操作数据库异常
    //用户服务  : SDKS-01 + c++定义错误代码code
    public static final int CPP_SVR_PHONE_REGISTERED        = -2101;  //SDKS-01-101=手机号已注册
    public static final int CPP_SVR_USERNAME_EXISTED        = -2102;  //SDKS-01-102=用户名重复
    public static final int CPP_SVR_ACCOUNT_NOT_EXISTED     = -2103;  //SDKS-01-103=账号不存在
    public static final int CPP_SVR_WRONG_PWD               = -2104;  //SDKS-01-104=密码错误
    public static final int CPP_SVR_ACCT_ALIASED            = -2105;  //SDKS-01-105=已经修改过账号名
    public static final int CPP_SVR_USER_INFO_COMPLETED     = -2106;  //SDKS-01-106=用户资料已经完善过了，不能再完善
    public static final int CPP_SVR_NO_FACE                 = -2107;  //SDKS-01-107=用户没有上传头像
    public static final int CPP_SVR_WRONG_VERIFY_CODE       = -2108;  //SDKS-01-108=验证码不正确
    public static final int CPP_SVR_ERROR_SAME_PWD          = -2109;  //SDKS-01-109=新密码不能与旧密码相同
    public static final int CPP_SVR_ERROR_PHONE_FORMAT      = -2110;  //SDKS-01-110=手机号码格式不对
    public static final int CPP_SVR_ERROR_USER_NAME_FORMAT  = -2111;  //SDKS-01-111=用户名不能包含特殊字符
    public static final int CPP_SVR_ERROR_NO_PERMISSION     = -2112;  //SDKS-01-112=权限不足
    public static final int CPP_SVR_USER_NO_GUILD           = -2113;  //SDKS-01-113=用户未加入工会
    public static final int CPP_SVR_TO_MANY_VERITY_REQ      = -2114;  //SDKS-01-114=发送验证短信太频繁
    public static final int CPP_SVR_ACCOUNT_EXISTED         = -2115;  //SDKS-01-115=账号已存在
    public static final int CPP_SVR_ERROR_VERIFY_MSG        = -2116;  //SDKS-01-116=获取验证码时的用途填写不正确
    public static final int CPP_SVR_SECURITY_NOT_UPGRADE    = -2117;  //SDKS-01-117=用户未设置密保
    public static final int CPP_SVR_WRONG_SECURITY_ANSWER   = -2118;  //SDKS-01-118=密保答案错误
    public static final int CPP_SVR_WRONG_SECURITY_QUESTION = -2119;  //SDKS-01-119=密保问题错误
    public static final int CPP_SVR_SENSITIVE_NICK          = -2120;  //SDKS-01-120=昵称包含敏感词汇
    public static final int CPP_SVR_SENSITIVE_SIGN          = -2121;  //SDKS-01-121=签名包含敏感词汇
    public static final int CPP_SVR_ALREADY_UPGRADE_SECURITY= -2122;  //SDKS-01-122=用户已经设置过密保
    public static final int CPP_SVR_CAN_NOT_REST_ACCT       = -2123;  //SDKS-01-123=已关闭重置帐号通道
    public static final int CPP_SVR_FORBIDDEN_ACCT          = -2124;  //SDKS-01-124=该帐号已被停封
    public static final int CPP_SVR_TOO_MANY_REG_REQ        = -2125;  //SDKS-01-125=设备注册频率限制
    public static final int CPP_SVR_OPENID_EXISTED          = -2126;  //SDKS-01-126=openid已存在
    //用户服务 :SDKS-01+2xx  sdkserver自定义错误代码
    public static final int USR_SVR_ERROR_NULL_USER_NAME    = -3200;  //SDKS-01-200=用户名不能为空
    public static final int USR_SVR_ERROR_NULL_PWD          = -3201;  //SDKS-01-201=旧密码或新密码为空
    public static final int USR_SVR_FORBIDDEN_ACCT          = -3202;  //SDKS-01-202=该用户被禁用
    public static final int USR_SVR_REG_NEED_VERIFY         = -3205;  //SDKS-01-205=一键注册需要验证
}
