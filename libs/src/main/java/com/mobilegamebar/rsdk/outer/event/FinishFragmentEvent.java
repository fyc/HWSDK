package com.mobilegamebar.rsdk.outer.event;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by chenshuide on 15/6/25.
 */
public class FinishFragmentEvent {
    public static final String TYPE_FINISH_FRAGMENT = "finish_fragment";

    public static class Param extends BaseEventParam {
        public Fragment fragment;

        public Param(int code, Context context, Fragment fragment) {
            super(code, context);
            this.fragment = fragment;
        }
    }
}
