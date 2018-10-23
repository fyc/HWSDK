package com.mobilegamebar.rsdk.outer.event;

import android.app.Activity;
import android.content.Context;

import com.mobilegamebar.rsdk.outer.IOperateCallback;

/**
 * Created by levyyoung on 15/6/8.
 */
public class AuthEvent {
    public static final String TYPE_LOGOUT = "com.yiyou.gamesdk.event.type.logout";
    public static final String TYPE_LOGIN = "com.yiyou.gamesdk.event.type.login";
    public static final String TYPE_GET_VERIFICATION_CODE = "com.yiyou.gamesdk.event.type.get_verification_code";
    public static final String TYPE_CHECK_VERIFICATION_CODE = "com.yiyou.gamesdk.event.type.check_verification_code";

    public static class LoginParams implements IDialogParam {
        public Activity activity;
        public IOperateCallback<String> callback;
        public String loginUITitle;

        public LoginParams(Activity activity, IOperateCallback<String> callback, String loginUITitle) {
            this.activity = activity;
            this.callback = callback;
            this.loginUITitle = loginUITitle;
        }

        @Override
        public String toString() {
            return "LoginParams{" +
                    "activity=" + activity +
                    ", callback=" + callback +
                    ", loginUITitle='" + loginUITitle + '\'' +
                    '}';
        }

        @Override
        public Context getActivityContext() {
            return activity;
        }

        @Override
        public IOperateCallback<String> getAppCallback() {
            return callback;
        }


    }

}
