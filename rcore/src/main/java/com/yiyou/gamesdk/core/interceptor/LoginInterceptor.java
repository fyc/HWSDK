package com.yiyou.gamesdk.core.interceptor;

import com.mobilegamebar.rsdk.outer.event.AuthEvent;

/**
 * Created by charles on 12/7/16.
 */
public interface LoginInterceptor extends Interceptor<LoginInterceptor.LoginParams> {


    class LoginParams {

        private AuthEvent.LoginParams loginParams;
        private int code;
        private String result;

        public LoginParams(AuthEvent.LoginParams loginParams, int code, String result) {
            this.loginParams = loginParams;
            this.code = code;
            this.result = result;
        }

        public AuthEvent.LoginParams getLoginParams() {
            return loginParams;
        }

        public int getCode() {
            return code;
        }

        public String getResult() {
            return result;
        }
    }


}
