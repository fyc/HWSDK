package com.yiyou.gamesdk.testapp;

/**
 * Created by chenshuide on 15/7/27.
 */
public class UrlPath {

    public static final String HOST = "http://120.132.68.148";


    //内网测试环境登陆态校验
    public static final String check_login_internal_test = HOST + "/im-server_18080/check.jsp";
    //外网测试环境登陆态校验
    public static final String check_login_outer_test = HOST + "/test-imitateCpServer/check.jsp";
    //    public static final String check_login_online = "http://120.132.68.148/r-imitateCpServer/check.jsp";
    //生产环境登陆态校验
    public static final String check_login_online = HOST + "/im-server/check.jsp";
    public static final String outgiving_test = HOST + "/test-imitateCpServer/outgiving.jsp";
    public static final String outgiving_online = HOST + "/im-server/check.jsp/outgiving.jsp";

    public static final String test_cp_server_login = "http://192.168.9.234:8080/imitateCpServer_232/check.jsp";
    public static final String test_cp_server_outgiving = "http://192.168.9.234:8080/imitateCpServer_232/outgiving.jsp";

}
