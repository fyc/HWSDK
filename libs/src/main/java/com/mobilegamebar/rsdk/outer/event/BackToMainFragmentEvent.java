package com.mobilegamebar.rsdk.outer.event;


import android.content.Context;

public class BackToMainFragmentEvent {

    public static final String TYPE_BACK_TO_MAIN_FRAGMENT = "back_to_main_fragment";
    public static class Param extends BaseEventParam {
        public String data ;

        public Param() {
        }

        public Param(int code, Context context, String data) {
            super(code,context);
            this.data = data;
        }

        @Override
        public String toString() {
            return "BackToMainFragmentEvent{" +
                    "data=" + data +
                    '}';
        }
    }
}
