package com.yiyou.gamesdk.core.ui.dialog.biz;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.CoreManager;
import com.mobilegamebar.rsdk.outer.util.ResourceHelper;

/**
 * Created by shui on 15-8-20.
 */
public class LoadingDialog extends Dialog {

    private View loadingAnimator;

    public LoadingDialog(Activity context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCanceledOnTouchOutside(false);
        setOwnerActivity(context);
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(CoreManager.getContext()).inflate(R.layout.tt_sdk_common_loading, null);
        setContentView(view);
        loadingAnimator = findViewById(R.id.loading_animator);
        Animation animation = AnimationUtils.loadAnimation(ResourceHelper.getContextHolder().get(),
                R.anim.anim_rotate_loading);
        loadingAnimator.startAnimation(animation);
    }


    @Override
    public void show() {
        if (!getOwnerActivity().isFinishing() && !isShowing()) {
            super.show();
        }


    }

    @Override
    public void dismiss() {
        if (!getOwnerActivity().isFinishing() && isShowing()) {
            loadingAnimator.clearAnimation();
            super.dismiss();
        }

    }
}
