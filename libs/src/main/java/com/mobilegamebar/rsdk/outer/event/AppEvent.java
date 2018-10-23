package com.mobilegamebar.rsdk.outer.event;

import android.app.Activity;
import android.content.Context;

import com.mobilegamebar.rsdk.outer.IOperateCallback;

/**
 * Created by levyyoung on 15/6/8.
 */
public class AppEvent {
    public static final String TYPE_SET_ORIENTATION = "com.yiyou.gamesdk.event.type.setOrientation";
    public static final String TYPE_ENTER_UI = "com.yiyou.gamesdk.event.type.enterUI";
    public static final String TYPE_EXIT_SDK = "com.yiyou.gamesdk.event.type.exitSDK";
    public static final String TYPE_EXIT_SDK_WITH_UI = "com.yiyou.gamesdk.event.type.exitSDKWithUI";

    public static class ExitWithUIParams<T>{
        public ExitWithUIParams() {
        }

        public ExitWithUIParams(Context context, IOperateCallback<T> callback) {
            this.context = context;
            this.callback = callback;
        }

        public Context context;
        public IOperateCallback<T> callback;
    }

    public static class OrientationParams {
        public int orientation;
        public OrientationParams(int orientation) {
            this.orientation = orientation;
        }

        @Override
        public String toString() {
            return "OrientationParams{" +
                    "orientation=" + orientation +
                    '}';
        }
    }

    public static class EnterUIParams{
        public Activity activity;
        public String biz;
        public IOperateCallback<String> callback;

        public EnterUIParams(Activity activity, String biz, IOperateCallback<String> callback) {
            this.activity = activity;
            this.biz = biz;
            this.callback = callback;
        }

        @Override
        public String toString() {
            return "EnterUIParams{" +
                    "activity=" + activity +
                    ", biz='" + biz + '\'' +
                    ", callback=" + callback +
                    '}';
        }
    }

}
