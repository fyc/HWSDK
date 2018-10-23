package com.mobilegamebar.rsdk.outer.event;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class StartActivityEvent {
    public static final String TYPE_START_ACTIVITY_ENVENT = "com.yiyou.gamesdk.event.start.activity";
    public static final int DISPLAY_TYPE_FULLSCREEN = 0;
    public static final int DISPLAY_TYPE_DIALOG = 1;


    public static final class FragmentParam extends BaseEventParam<Context> {
        public Bundle mBundle ;
    	public String fragmentName ;
        public int displayType = DISPLAY_TYPE_FULLSCREEN;
        public Activity startUpActivity = null;

        public FragmentParam(int code ,Context context , int displayType, Activity startUpActivity, String data,Bundle bundle) {
            super(code, context) ;
            this.mBundle = bundle ;
            this.fragmentName = data ;
            this.startUpActivity = startUpActivity;
            this.displayType = displayType;
        }

        public FragmentParam(int code ,Context context ,String data,Bundle bundle) {
            this(code, context, DISPLAY_TYPE_FULLSCREEN, null, data, bundle);
        }

    }

}
