package com.mobilegamebar.rsdk.outer.event;

import android.content.Context;

import com.mobilegamebar.rsdk.outer.IOperateCallback;

/**
 * Created by Orange on 15/6/15.
 */
public interface IDialogParam {
    Context getActivityContext();

    IOperateCallback<String> getAppCallback();

}
