package com.yiyou.gamesdk.core.ui.dialog.biz;

import android.content.Context;

import com.mobilegamebar.rsdk.outer.event.IDialogParam;
import com.yiyou.gamesdk.R;

public class RealNameAuthController extends BaseAuthViewController {
    public RealNameAuthController(Context context, IDialogParam params) {
        super(context, params);
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.tt_sdk_container_real_name;
    }

    @Override
    public void onShow() {

    }

    @Override
    public void onHide() {

    }
}
