package com.yiyou.gamesdk.testapp;

/**
 * Created by chenshuide on 15/8/14.
 */
public class SwitchUrl {

    private static SwitchUrl intance;

    private boolean isDebug;

    private SwitchUrl() {

    }





    public static SwitchUrl getIntance() {
        if (intance == null)
            intance = new SwitchUrl();
        return intance;
    }


    public boolean isDebug() {
        return isDebug;
    }

    public void setIsDebug(boolean isDebug) {
        this.isDebug = isDebug;
    }


    public String loginUrl() {

        return isDebug ? UrlPath.check_login_outer_test : UrlPath.check_login_online; //内网测试环境:生产环境
//        return isDebug ? UrlPath.check_login_internal_test : UrlPath.check_login_online;  //内网测试环境:生产环境
//        return UrlPath.test_cp_server_login;

    }

    public String payUrl() {

        return isDebug ? UrlPath.outgiving_test : UrlPath.outgiving_online;
//        return UrlPath.test_cp_server_outgiving;

    }

}
